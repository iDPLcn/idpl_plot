package cn.edu.buaa.jsi.servlet;

import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.*;
import org.dom4j.io.*;
import org.json.*;

public class PerfsonarServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6660969145810933406L;

	public void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
	}

	/**
	 * The doPost method of the servlet. <br>
	 * 
	 * This method is called when a form has its tag value method equals to
	 * post.
	 * 
	 * @param request
	 *            the request send by the client to the server
	 * @param response
	 *            the response send by the server to the client
	 * @throws ServletException
	 *             if an error occurred
	 * @throws IOException
	 *             if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String type = request.getParameter("type");
		String src = request.getParameter("src");
		String dst = request.getParameter("dst");
		String time_start = request.getParameter("time_start");
		String time_end = request.getParameter("time_end");
		String url = null;
		JSONObject json = new JSONObject();
		try {
			SAXReader sr = new SAXReader(); // 获取读取xml的对象。
			org.dom4j.Document doc = sr.read("../webapps/config/Node.xml");// windows下
			// org.dom4j.Document doc =
			// sr.read("/home/kwang/apache-tomcat-7.0.56/webapps/config/Node.xml");//linux下
			Element el_root = doc.getRootElement();// 向外取数据，获取xml的根节点。
			url = el_root.elementText("jsonService");
			src = el_root.element(src).elementText(type);
			dst = el_root.element(dst).elementText(type);
		} catch (DocumentException e) {
			System.out.println("reading configuration failed!");
		}
		String lossURL;
		String owdelayURL;
		String pingURL;
		String throughputURL;
		lossURL = url + "loss/?time-start=" + time_start + "&time-end="
				+ time_end + "&format=json&dst=" + dst + "&src=" + src;
		owdelayURL = url + "owdelay/?time-start=" + time_start + "&time-end="
				+ time_end + "&format=json&dst=" + dst + "&src=" + src;
		pingURL = url + "ping/?time-start=" + time_start + "&time-end="
				+ time_end + "&format=json&dst=" + dst + "&src=" + src;
		throughputURL = url + "throughput/?time-start=" + time_start
				+ "&time-end=" + time_end + "&format=json&dst=" + dst + "&src="
				+ src;
		String lossmessage = null, owdelaymessage = null, pingmessage = null, throughputmessage = null;
		JSONArray lossjsonarray = null, owdelayjsonarray = null, pingjsonarray = null, throughputjsonarray = null;
		try {
			lossmessage = loadJSON(lossURL); // jsonmessage接口获取的字符串
			owdelaymessage = loadJSON(owdelayURL);
			pingmessage = loadJSON(pingURL);
			throughputmessage = loadJSON(throughputURL);
			lossjsonarray = new JSONArray(lossmessage); // 字符串转成jsonarray
			owdelayjsonarray = new JSONArray(owdelaymessage);
			pingjsonarray = new JSONArray(pingmessage);
			throughputjsonarray = new JSONArray(throughputmessage);
			json.put("loss", lossjsonarray);
			json.put("owdelay", owdelayjsonarray);
			json.put("ping", pingjsonarray);
			json.put("throughput", throughputjsonarray);
		} catch (JSONException e) {
			System.out.println("get data from perfsonar failed!");
			System.out.println("lossURL:" + lossURL + "\nowdelayURL:"
					+ owdelayURL + "\npingURL:" + pingURL + "\nthroughputURL:"
					+ throughputURL);
			System.out.println("lossmessage:" + lossmessage
					+ "\nowdelaymessage:" + owdelaymessage + "\npingmessage:"
					+ pingmessage + "\nthroughputmessage:" + throughputmessage);
			try {
				lossjsonarray = new JSONArray("[]"); // 获取数据失败时四组数据均返回空数组到前台
				owdelayjsonarray = new JSONArray("[]");
				pingjsonarray = new JSONArray("[]");
				throughputjsonarray = new JSONArray("[]");
				json.put("loss", lossjsonarray);
				json.put("owdelay", owdelayjsonarray);
				json.put("ping", pingjsonarray);
				json.put("throughput", throughputjsonarray);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
			}
		} finally {
			response.getWriter().print(json);
		}
	}

	/**
	 * 访问url获取json数据
	 * 
	 * @param url
	 *            地址
	 * @return json字符串
	 */
	private String loadJSON(String url) {
		StringBuilder json = new StringBuilder();
		try {
			URL oracle = new URL(url);
			URLConnection yc = oracle.openConnection();
			BufferedReader in = new BufferedReader(new InputStreamReader(
					yc.getInputStream()));
			String inputLine = null;
			while ((inputLine = in.readLine()) != null) {
				json.append(inputLine);
			}
			in.close();
		} catch (Exception e) {
		}
		return json.toString();
	}
}
