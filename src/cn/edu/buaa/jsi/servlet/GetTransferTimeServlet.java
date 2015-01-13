package cn.edu.buaa.jsi.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class GetTransferTimeServlet extends HttpServlet {

	/**
	 * The doPost method of the servlet. <br>
	 *
	 * This method is called when a form has its tag value method equals to post.
	 * 
	 * @param request the request send by the client to the server
	 * @param response the response send by the server to the client
	 * @throws ServletException if an error occurred
	 * @throws IOException if an error occurred
	 */
	public void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		String src = request.getParameter("src");
		String dst = request.getParameter("dst");
		String time_start = request.getParameter("time_start");
		String time_end = request.getParameter("time_end");
		String url = null;
		JSONObject json = new JSONObject();
		try {
			SAXReader sr = new SAXReader(); // 获取读取xml的对象。
			org.dom4j.Document doc = sr.read("../webapps/config/Node_CN.xml");// windows下
			// org.dom4j.Document doc =
			// sr.read("/home/kwang/apache-tomcat-7.0.56/webapps/config/Node.xml");//linux下
			Element el_root = doc.getRootElement();// 向外取数据，获取xml的根节点。
			url = el_root.elementText("getTransferTime")+
				  "?source=" + src +
				  "&destination=" + dst +
				  "&timeEnd-start=" + time_start +
				  "&timeEnd-end=" + time_end +
				  "&format=json";
		} catch (DocumentException e) {
			System.out.println("reading configuration failed!");
		}
		String transferTimeMessage = null;
		JSONArray transferTimeJsonArray = null;
		try {
			transferTimeMessage = loadJSON(url); // jsonmessage接口获取的字符串
			transferTimeJsonArray = new JSONArray(transferTimeMessage); // 字符串转成jsonarray
			json.put("transferTime", transferTimeJsonArray);
//			System.out.println(url);
		} catch (JSONException e) {
			System.out.println("get transferTime data from:" + url + "failed!");
			try {
				transferTimeJsonArray = new JSONArray("[]"); // 获取数据失败时返回空数组到前台
				json.put("transferTime", transferTimeJsonArray);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
			}
		} finally {
//			System.out.println(json);
			response.getWriter().print(json);
		}
	}
	
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
