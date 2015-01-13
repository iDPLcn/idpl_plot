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

public class NodeInfoServlet extends HttpServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4612075491643503144L;

	/**
	 * Constructor of the object.
	 */
	public NodeInfoServlet() {
		super();
	}

	/**
	 * Destruction of the servlet. <br>
	 */
	public void destroy() {
		super.destroy(); // Just puts "destroy" string in log
		// Put your code here
	}

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
		String nodeUrl = null; //获取节点信息的url
		String preUrl = null;  //获取平均值的url前缀
		String positiveAvgUrl= null;//取正向平均值url
		String negativeAvgUrl= null;//取反向平均值url
		JSONObject json = new JSONObject();    //返回前端的json数据
		try {
			SAXReader sr = new SAXReader(); // 获取读取xml的对象。
			org.dom4j.Document doc = sr.read("../webapps/config/Node_CN.xml");// windows下
			// org.dom4j.Document doc =
			// sr.read("/home/kwang/apache-tomcat-7.0.56/webapps/config/Node.xml");//linux下
			Element el_root = doc.getRootElement();// 向外取数据，获取xml的根节点。
			nodeUrl = el_root.elementText("conderNodeInfo");
			preUrl = el_root.elementText("getTransferTime");
		} catch (DocumentException e) {
			System.out.println("reading configuration failed!");
		}		
		String nodeInfoMessage = null;
		JSONArray nodeInfoJsonArray = null;
		JSONArray positiveAvgJsonArray = new JSONArray();
		JSONArray negativeAvgJsonArray = new JSONArray();
		try {
			nodeInfoMessage = loadJSON(nodeUrl); // jsonmessage接口获取的字符串
			nodeInfoJsonArray = new JSONArray(nodeInfoMessage); // 字符串转成jsonarray
			for(int i=0; i<nodeInfoJsonArray.length()-1; i++){
				for(int j=i+1; j<nodeInfoJsonArray.length(); j++){
					positiveAvgUrl = preUrl +
							  "average/?source=" + nodeInfoJsonArray.getJSONObject(i).getString("host") +
							  "&destination=" + nodeInfoJsonArray.getJSONObject(j).getString("host") +
							  "&format=json";
					negativeAvgUrl = preUrl +
							  "average/?source=" + nodeInfoJsonArray.getJSONObject(j).getString("host") +
							  "&destination=" + nodeInfoJsonArray.getJSONObject(i).getString("host") +
							  "&format=json";
					String positiveAvgMessage = null;
					String negativeAvgMessage = null;
					JSONObject positiveAvgJson = null;
					JSONObject negativeAvgJson = null;
					positiveAvgMessage = loadJSON(positiveAvgUrl); 
					negativeAvgMessage = loadJSON(negativeAvgUrl);
					positiveAvgJson = new JSONObject(positiveAvgMessage);
					negativeAvgJson = new JSONObject(negativeAvgMessage);
					positiveAvgJsonArray.put(positiveAvgJson.get("duration__avg"));
					negativeAvgJsonArray.put(negativeAvgJson.get("duration__avg"));
				}
			}
			json.put("nodeInfo", nodeInfoJsonArray);
			json.put("positiveAvgArray", positiveAvgJsonArray);
			json.put("negativeAvgArray", negativeAvgJsonArray);
//			System.out.println(json);
		} catch (JSONException e) {
			System.out.println("get data from:" + nodeUrl + "failed!");
			try {
				nodeInfoJsonArray = new JSONArray("[]"); // 获取数据失败时返回空数组到前台
				json.put("nodeInfo", nodeInfoJsonArray);
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
