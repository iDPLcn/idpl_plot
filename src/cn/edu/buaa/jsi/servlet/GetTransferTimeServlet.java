package cn.edu.buaa.jsi.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Date;

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
		String srcIP = request.getParameter("srcip");
		String dstIP = request.getParameter("dstip");
		String time_start = request.getParameter("time_start");
		String time_end = request.getParameter("time_end");
		String url_getMeasurementData = null;			//conder测得数据的url；
		String url_perfsonar = null;					//perfsonar测得数据的url；
		String url_getMeasurementInfo = null;			//获取节点显示数据类型的url；
		JSONObject json = new JSONObject();
		try {
			SAXReader sr = new SAXReader(); // 获取读取xml的对象。
			org.dom4j.Document doc = sr.read("../webapps/config/Node_info.xml");// windows下
			// sr.read("/home/kwang/apache-tomcat-7.0.56/webapps/config/Node.xml");//linux下
			Element el_root = doc.getRootElement();// 向外取数据，获取xml的根节点。
			url_getMeasurementData = el_root.elementText("getMeasurementData");
			url_perfsonar = el_root.elementText("getPerfsonarTransferTime");	
			url_getMeasurementInfo = el_root.elementText("getMeasurementInfo");
		} catch (DocumentException e) {
			System.out.println("reading configuration failed!");
		}
		
		String measurementInfoMessage = null;							//Measurement Information
		JSONArray measurementInfoJsonArray = null;
		try {
			measurementInfoMessage = loadJSON(url_getMeasurementInfo); 		// jsonmessage接口获取的字符串			
			measurementInfoJsonArray = new JSONArray(measurementInfoMessage);	// 字符串转成jsonarray	
		} catch (JSONException e1) {
			// TODO Auto-generated catch block
			System.out.println("measurementInfoMessage字符串转成jsonarray失败");
			e1.printStackTrace();
		} 	
		
		JSONArray allTransferTimeJsonArray = new JSONArray();	
		for(int i=0; i<measurementInfoJsonArray.length(); i++){
			String url_getData = null;
			String transferTimeMessage = null;
			JSONArray transferTimeJsonArray = null;
			JSONObject transferTimeJsonObject = new JSONObject();
			try {
				url_getData = url_getMeasurementData + 
						"?source=" + src +
						"&destination=" + dst +
						"&timeEnd-start=" + time_start +
						"&timeEnd-end=" + time_end +
						"&tool_name=" + measurementInfoJsonArray.getJSONObject(i).get("tool_name") +
						"&format=json";
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				System.out.println("获取tool_name失败！");
				e.printStackTrace();
			}
			transferTimeMessage = loadJSON(url_getData); 						// jsonmessage接口获取的conder数据字符串
			try {
				transferTimeJsonArray = new JSONArray(transferTimeMessage);
				transferTimeJsonObject.put("transferTime", transferTimeJsonArray);
				allTransferTimeJsonArray.put(transferTimeJsonObject);
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}	
			
		}

		url_perfsonar = url_perfsonar +
				"?src=" + srcIP +
				"&dst=" + dstIP +
				"&time-start=" + time_start +
				"&time-end=" + time_end +
				"&format=json";
		String perfsonarThroughputMessage = null;		
		JSONArray perfsonarThroughputJsonArray = null;
		try {			
			perfsonarThroughputMessage = loadJSON(url_perfsonar); 		// jsonmessage接口获取的perfsonar数据字符串			
			perfsonarThroughputJsonArray = new JSONArray(perfsonarThroughputMessage);
			json.put("allTransferTime", allTransferTimeJsonArray);
			json.put("perfsonarThroughput", perfsonarThroughputJsonArray);
//System.out.println(url_transferTime);
//System.out.println(url_iperfTime);
//System.out.println(url_perfsonar);
		} catch (JSONException e) {
			System.out.println("get transferTime data from:" + url_getMeasurementData + "failed!");
			try {
				allTransferTimeJsonArray = new JSONArray("[]"); // 获取数据失败时返回空数组到前台
				json.put("allTransferTime", allTransferTimeJsonArray);
				json.put("perfsonarThroughput", perfsonarThroughputJsonArray);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
			}
		} finally {
			SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			System.out.println("发送" + df.format(new Date()));
//			response.getWriter().print(json.toString());
			response.getOutputStream().print(json.toString());
			System.out.println("发送" + df.format(new Date()));
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
