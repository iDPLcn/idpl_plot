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

import cn.edu.buaa.jsi.util.LogUtil;

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
		String nodeUrl = null; 						//获取节点信息的url
		String measurementInfoUrl = null; 			//获取节点显示数据类型的url
		String getMeasurementAvgdata = null; 		//获取平均值
		String getCondorTransferTimeUrl = null;  	//获取condor平均值的url前缀
		String getIperfTransferTimeUrl = null;  	//获取iperf平均值的url前缀
		String getPerfsonarTransferTimeUrl = null;  //获取Perfsonar平均值的url前缀
		JSONObject json = new JSONObject();    		//返回前端的json数据
		try {
			SAXReader sr = new SAXReader(); // 获取读取xml的对象。
			org.dom4j.Document doc = sr.read("../webapps/config/Node_info.xml");// windows下
			// org.dom4j.Document doc =
			// sr.read("/home/kwang/apache-tomcat-7.0.56/webapps/config/Node.xml");//linux下
			Element el_root = doc.getRootElement();// 向外取数据，获取xml的根节点。
			nodeUrl = el_root.elementText("conderNodeInfo");
			measurementInfoUrl = el_root.elementText("getMeasurementInfo");
			getMeasurementAvgdata = el_root.elementText("getMeasurementAvgdata");
			getCondorTransferTimeUrl = el_root.elementText("getTransferTime");
			getIperfTransferTimeUrl = el_root.elementText("getIperfTime");			
			getPerfsonarTransferTimeUrl = el_root.elementText("getPerfsonarTransferTime");
		} catch (DocumentException e) {
			System.out.println("reading configuration failed!");
		}		
		String nodeInfoMessage = null;
		JSONArray nodeInfoJsonArray = null;			 			//未分类的node数组
		JSONArray orderedJsonArray = new JSONArray();			//分类后的node数组(国际在前，国内在后)
		try {
			nodeInfoMessage = loadJSON(nodeUrl); 				// jsonmessage接口获取的字符串			
			nodeInfoJsonArray = new JSONArray(nodeInfoMessage); // 字符串转成jsonarray	
			orderedJsonArray = sortNode(nodeInfoJsonArray);
			json = getNodeInfo(orderedJsonArray, measurementInfoUrl, 
					getMeasurementAvgdata, getCondorTransferTimeUrl, 
					getIperfTransferTimeUrl, getPerfsonarTransferTimeUrl);
			LogUtil.debug(json.toString());
		} catch (JSONException e) {
			LogUtil.info("get data from:" + nodeUrl + "failed!");
			try {
				nodeInfoJsonArray = new JSONArray("[]"); 		// 获取数据失败时返回空数组到前台
				json.put("nodeInfo", nodeInfoJsonArray);
			} catch (JSONException e1) {
				// TODO Auto-generated catch block
			}
		} finally {
			response.getWriter().print(json);
		}
	}
	
	private JSONObject getNodeInfo(JSONArray nodeInfoJsonArray, 
									String measurementInfoUrl, 
									String getMeasurementAvgdata, 
									String getCondorTransferTimeUrl, 
									String getIperfTransferTimeUrl, 
									String getPerfsonarTransferTimeUrl){		
		String measurementInfoMessage = null;	//Measurement Information
		JSONArray measurementInfoJsonArray = null;
		try {
			measurementInfoMessage = loadJSON(measurementInfoUrl); 		// jsonmessage接口获取的字符串			
			measurementInfoJsonArray = new JSONArray(measurementInfoMessage);	// 字符串转成jsonarray	
		} catch (JSONException e1) {
			LogUtil.info("measurementInfoMessage字符串转成jsonarray失败");
		} 	
		
		JSONArray condorPositiveAvgJsonArray = new JSONArray();
		int length = measurementInfoJsonArray.length();
		for(int i=0; i<length; i++){
			String positiveAvgUrl = null;
			JSONArray positiveAvgJsonArray = new JSONArray();
			String positiveAvgMessage = null;
			JSONObject positiveAvgJson = null;
			JSONObject condorPositiveAvgJsonObject = new JSONObject();
			for(int j=0; j<nodeInfoJsonArray.length(); j++){
				try {
					positiveAvgUrl = getMeasurementAvgdata +
							"?source=" + nodeInfoJsonArray.getJSONObject(j).getJSONObject("source").getString("host") +
							"&destination=" + nodeInfoJsonArray.getJSONObject(j).getJSONObject("destination").getString("host") +
							"&tool_name=" + measurementInfoJsonArray.getJSONObject(i).get("tool_name") +
							"&format=json";
					positiveAvgMessage = loadJSON(positiveAvgUrl); 
					positiveAvgJson = new JSONObject(positiveAvgMessage);
					positiveAvgJsonArray.put(positiveAvgJson.get("bandwidth__avg"));
				} catch (JSONException e) {
					e.printStackTrace();
				}
						
			}			
			
			try {
				condorPositiveAvgJsonObject.put("condorPositiveAvg", positiveAvgJsonArray);
				condorPositiveAvgJsonArray.put(condorPositiveAvgJsonObject);
			} catch (JSONException e) {
				e.printStackTrace();
			}			
		}
		
		String perfsonarPositiveAvgUrl;
		JSONArray perfsonarPositiveAvgJsonArray = new JSONArray();
		try {
			for(int i=0; i<nodeInfoJsonArray.length(); i++){
				perfsonarPositiveAvgUrl = getPerfsonarTransferTimeUrl +
						"average?src=" + nodeInfoJsonArray.getJSONObject(i).getJSONObject("source").getString("ip_address") +
						"&dst=" + nodeInfoJsonArray.getJSONObject(i).getJSONObject("destination").getString("ip_address") +
						"&format=json";
				
				String perfsonarPositiveAvgMessage = null;
				
				JSONObject perfsonarPositiveAvgJson = null;

				perfsonarPositiveAvgMessage = loadJSON(perfsonarPositiveAvgUrl); 
				
				perfsonarPositiveAvgJson = new JSONObject(perfsonarPositiveAvgMessage);

				perfsonarPositiveAvgJsonArray.put(perfsonarPositiveAvgJson.get("throughput__avg"));
			}

			
		} catch (JSONException e) {
			e.printStackTrace();
		}		
		
		JSONObject nodeInfoJson = new JSONObject();		
		try {
			nodeInfoJson.put("nodeInfo", nodeInfoJsonArray);
			nodeInfoJson.put("condorPositiveAvgArray", condorPositiveAvgJsonArray);
			nodeInfoJson.put("perfsonarPositiveAvgArray", perfsonarPositiveAvgJsonArray);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		
		return nodeInfoJson;
	}

	
	private JSONArray sortNode(JSONArray nodeInfoJsonArray){
		JSONArray orderedJsonArray = new JSONArray();	
		int length = nodeInfoJsonArray.length();
		try {
			for(int i=0; i<length; i++){
				String type = nodeInfoJsonArray.getJSONObject(i).getJSONObject("source").getString("pool_no");
				if(type.equals("2")){
					orderedJsonArray.put(nodeInfoJsonArray.getJSONObject(i));
				}
			}
			for(int i=0; i<length; i++){
				String type = nodeInfoJsonArray.getJSONObject(i).getJSONObject("source").getString("pool_no");
				if(type.equals("1")){
					orderedJsonArray.put(nodeInfoJsonArray.getJSONObject(i));
				}
			}
		} catch (JSONException e) {
			LogUtil.info("国内国外节点分类错误！");
		}		
		return orderedJsonArray;
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
