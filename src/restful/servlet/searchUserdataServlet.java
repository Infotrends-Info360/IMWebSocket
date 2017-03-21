package restful.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import util.Util;

/**
 * searchUserdata RESTful
 * 
 * @author Lin
 */

@Path("/searchUserdata")
public class searchUserdataServlet {

	JSONObject jsonObject = new JSONObject();

	@POST
	@Produces("application/json")
	public Response PostFromPath(
			@FormParam("searchtype") String searchtype,
			@FormParam("userID") String userID,
			@FormParam("userName") String userName,
			@FormParam("lang") String lang) throws IOException {
		
		SimpleDateFormat sdf = new SimpleDateFormat(
				"yyyy/MM/dd HH:mm:ss");
		Date now = new Date();
		String date = sdf.format(now);
		
		jsonObject.put("searchtype", searchtype);
		jsonObject.put("lang", lang);
		
		/*** debug - 測試時間 ***/
		long startTime;
		long endTime;

		// Util.getConsoleLogger().debug("attributes: "+attributes);
		/** 拿取searchkey, pkey - 透過GetServiceNameSetting() **/
		startTime = System.currentTimeMillis();
		JSONObject CfgServiceNameSettingjsonObject = null;
		String searchkey = null;
		String pkey = null;
		try {
			CfgServiceNameSettingjsonObject = GetServiceNameSetting(searchtype);
			searchkey = CfgServiceNameSettingjsonObject.getString("searchkey");
			pkey = CfgServiceNameSettingjsonObject.getString("uniquekey");
		} catch (org.json.JSONException e) {
			Util.getConsoleLogger().debug("org.json.JSONException - "
					+ "searchUserdataServlet.java");
			// e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		endTime = System.currentTimeMillis();
		Util.getConsoleLogger().info("RESTful GetServiceNameSetting time: " + (endTime - startTime)/1000 + "s" );
		Util.getFileLogger().info("RESTful GetServiceNameSetting time: " + (endTime - startTime)/1000 + "s" );

		
		jsonObject.put("searchkey", searchkey);
		jsonObject.put("pkey", pkey);
		
		/*** 將所有attributes key-value放入jsonObject中 ***/
		// 組出attributes物件
		JsonObject attributes = new JsonObject();
		attributes.addProperty("attributenames", searchkey + "," + "id");
		attributes.addProperty(searchkey,userName); // 特別注意一下名稱並沒對到
		attributes.addProperty("id",userID);
		
		String attributeNames = attributes.get("attributenames").getAsString();
		String[] attributenamesArray = attributeNames.split(",");
		for (int i = 0; i < attributenamesArray.length; i++) {
			String currAttriName = attributenamesArray[i];
			jsonObject.put(currAttriName, attributes.get(currAttriName).getAsString());
		}// end of for
		
		/*** GetCustomerLevel - 拿取CustomerData***/
		JSONArray CustomerLeveljsonarray = null;
		startTime = System.currentTimeMillis();
		try {
			// String sID = "A123456789";
			// String sCustLevel = "A";
			String bSelect = "false";
			CustomerLeveljsonarray = GetCustomerLevel(attributes.get(searchkey).getAsString(), searchtype, bSelect);
			Util.getConsoleLogger().debug("CustomerLeveljsonarray.toString(): " + CustomerLeveljsonarray.toString());
			Util.getConsoleLogger().debug("CustomerLeveljsonarray.length(): " + CustomerLeveljsonarray.length());
			jsonObject.put("CustomerData", CustomerLeveljsonarray);
		} catch (Exception e) {
			// e.printStackTrace();
			jsonObject.put("error", e.getMessage());
		}
		endTime = System.currentTimeMillis();
		Util.getConsoleLogger().info("RESTful GetCustomerLevel time: " + (endTime - startTime)/1000 + "s" );
		Util.getFileLogger().info("RESTful GetCustomerLevel time: " + (endTime - startTime)/1000 + "s" );

		/*** GetServiceNameCache - 拿取mapping***/
		startTime = System.currentTimeMillis();
		try {
			JSONObject ServiceNameCachejsonObj = GetServiceNameCache(searchtype);
			jsonObject.put("mapping", ServiceNameCachejsonObj);
		} catch (Exception e) {
			// e.printStackTrace();
			jsonObject.put("error", e.getMessage());
		}
		endTime = System.currentTimeMillis();
		Util.getConsoleLogger().info("RESTful GetServiceNameCache time: " + (endTime - startTime)/1000 + "s" );
		Util.getFileLogger().info("RESTful GetServiceNameCache time: " + (endTime - startTime)/1000 + "s" );
		
		/*** Set Contact Log - 拿取SetContactLog***/
		// 使用CustomerLeveljsonarray迴圈內資料
		// 疑問: 什麼時候 CustomerLeveljsonarray.length() > 1
		startTime = System.currentTimeMillis();
		JSONArray SetContactLogjsonarray = new JSONArray();
		try {
			for (int j = 0; j < CustomerLeveljsonarray.length(); j++) {
//				Util.getConsoleLogger().debug("CustomerLeveljsonarray: "
//						+ CustomerLeveljsonarray);
				JsonObject jsonObj = Util.getGJsonObject(
						CustomerLeveljsonarray.getJSONObject(j).toString()
				);
				Util.getConsoleLogger().debug("jsonObj: "+jsonObj);
				if(jsonObj.get(searchkey) != null){

					jsonObject.put("date", date);
					Util.getConsoleLogger().debug("searchkey: " + searchkey);
					Util.getConsoleLogger().debug("pkey: " + pkey);
					Util.getConsoleLogger().debug("date: " + date);
					Util.getConsoleLogger().debug("CustomerLeveljsonarray: " + CustomerLeveljsonarray.getJSONObject(j).toString());
					Util.getFileLogger().info("***** SetContactLog(param) - searchkey: " + searchkey);
					Util.getFileLogger().info("***** SetContactLog(param) - pkey: " + pkey);
					Util.getFileLogger().info("***** SetContactLog(param) - date: " + date);
					Util.getFileLogger().info("***** SetContactLog(param) - CustomerLeveljsonarray: " + CustomerLeveljsonarray.getJSONObject(j).toString());
					
					JSONObject SetContactLogjsonObject = SetContactLog(
									searchkey, pkey, date, 
									CustomerLeveljsonarray.getJSONObject(j).toString()
					);
//					jsonObject.put("SetContactLog", SetContactLogjsonObject); //改為抓取Contact陣列 請關閉
					SetContactLogjsonarray.put(SetContactLogjsonObject); //改為抓取Contact陣列 請開啟
					
					
				}
				jsonObject.put("SetContactLog", SetContactLogjsonarray); //改為抓取Contact陣列 請開啟
			}// end of for
			
			// 若無抓取到任何userdata, 如使用C123456789登入時,則 SetContactLog裡面使用insert方法
			if (CustomerLeveljsonarray.length() == 0){
				
				
			}
			
			
		} catch (Exception e) {
			// e.printStackTrace();
			jsonObject.put("error", e.getMessage());
		}
		endTime = System.currentTimeMillis();
		Util.getConsoleLogger().info("RESTful SetContactLog time: " + (endTime - startTime)/1000 + "s" );
		Util.getFileLogger().info("RESTful SetContactLog time: " + (endTime - startTime)/1000 + "s" );

		
		
		return Response
				.status(200)
				.entity(jsonObject.toString())
				.header("charser", "utf-8")
				.header("Access-Control-Allow-Origin", "*")
				.header("Access-Control-Allow-Methods",
						"POST, GET, PUT, UPDATE, OPTIONS")
				.header("Access-Control-Allow-Headers",
						"Content-Type, Accept, X-Requested-With").build();
	}

	public JSONObject GetServiceNameSetting(String typeid) throws Exception {
		Util.getConsoleLogger().debug("GetServiceNameSetting() called");
		StringBuilder responseSB = null;
		// Encode the query
		String postData = "typeid=" + typeid;

		// Connect to URL (是否可考慮在此直接呼叫DAO即可,可省去網路請求,提升效能)
		String hostURL = Util.getHostURLStr("IMWebSocket");
		Util.getConsoleLogger().debug("hostURL: " + hostURL);
		URL url = new URL( hostURL + "/IMWebSocket/RESTful/Cfg_ServiceName_Setting");
//		URL url = new URL(
//				"http://127.0.0.1:8080/IMWebSocket/RESTful/Cfg_ServiceName_Setting");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Length",
				String.valueOf(postData.length()));

		// Write data
		OutputStream os = connection.getOutputStream();
		os.write(postData.getBytes());

		// Read response
		responseSB = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				connection.getInputStream(), "UTF-8"));

		String line;
		while ((line = br.readLine()) != null)
			responseSB.append(line.trim());

		// Close streams
		br.close();
		os.close();
		
		// ex. {"status":"POST","uniquekey":"id","searchkey":"Phone"} 
//		Util.getConsoleLogger().debug("responseSB: " + responseSB);
		// Util.getConsoleLogger().debug("responseSB: "+responseSB.toString().trim());
		JSONObject CfgServiceNameSettingjsonObject = new JSONObject(
				responseSB.toString());
		return CfgServiceNameSettingjsonObject;
	}

	public JSONArray GetCustomerLevel(String searchkeyvalue, String searchtype,
			String bSelect) throws Exception {
		StringBuilder responseSB = null;
		// Encode the query
		String postData = "sID=" + searchkeyvalue + "&sCustLevel=" + searchtype
				+ "&bSelect=" + bSelect;

		// Connect to URL
		// 小林哥表示: 須建立判斷機制log,設定timeout協助以後判斷問題來源:
		String hostURL = Util.getHostURLStr("infoacd");
		Util.getConsoleLogger().debug("hostURL(infoacd): " + hostURL);
		URL url = new URL( hostURL + "/infoacd/infoCenterWebService.asmx/GetCustomerLevel");
//		URL url = new URL(
//				"http://192.168.10.7/infoacd/infoCenterWebService.asmx/GetCustomerLevel");
		// URL url = new
		// URL("http://192.168.10.40:8080/infoacd/infoCenterWebService.asmx/GetCustomerLevel");
		// // 當192.168.10.7掛點時,使用此server
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Length",
				String.valueOf(postData.length()));
		// connection.setRequestProperty("Accept-Charset", "UTF-8");
		// connection.setRequestProperty("charset", "UTF-8");

		// Write data
		OutputStream os = connection.getOutputStream();
		os.write(postData.getBytes());

		// Read response
		responseSB = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				connection.getInputStream(), "UTF-8"));

		String line;
		while ((line = br.readLine()) != null)
			// responseSB.append(new
			// String(line.toString().getBytes(),"UTF-8"));
			responseSB.append(line.trim());

		// Close streams
		br.close();
		os.close();

		// Util.getConsoleLogger().debug("responseSB: "+responseSB.toString().trim());

		DocumentBuilderFactory factory_xpath = DocumentBuilderFactory
				.newInstance();
		Document doc = factory_xpath.newDocumentBuilder().parse(
				new InputSource(new StringReader(
				// new String(responseSB.toString().getBytes(),"UTF-8"))));
						responseSB.toString().trim())));
		XPath xpath = XPathFactory.newInstance().newXPath();

		int count = ((Number) xpath.evaluate(
				"count(DataSet/diffgram/NewDataSet/CustomerData)", doc,
				javax.xml.xpath.XPathConstants.NUMBER)).intValue();
		// Util.getConsoleLogger().debug("count: "+count);
		jsonObject.put("CustomerDataCount", count);

		JSONArray jsonarray = new JSONArray();
		
		Util.getConsoleLogger().debug("count:"+count);
		for (int j = 0; j < count; j++) {
			JSONObject infojsonObject = new JSONObject();
			int c = j + 1;
			int CustomerDataChildcount = ((Number) xpath.evaluate(
					"count(DataSet/diffgram/NewDataSet/CustomerData[" + c
							+ "]/*)", doc,
					javax.xml.xpath.XPathConstants.NUMBER)).intValue();
			Util.getConsoleLogger().debug("CustomerDataChildcount:"+CustomerDataChildcount);
			for (int k = 0; k < CustomerDataChildcount; k++) {
				
				Object CustomerDatasO = (xpath
						.evaluate("DataSet/diffgram/NewDataSet/CustomerData["
								+ c + "]/*", doc,
								javax.xml.xpath.XPathConstants.NODESET));
				NodeList CustomerDatasn = (NodeList) CustomerDatasO;
				String CustomerDataskey = (String) CustomerDatasn.item(k)
						.getNodeName();
				String CustomerDatasvalue = (String) CustomerDatasn.item(k)
						.getTextContent();
				if(!CustomerDataskey.toLowerCase().equals("dbid")){
					infojsonObject.put(CustomerDataskey, CustomerDatasvalue);
				}
				
			}

			jsonarray.put(infojsonObject);

			//return jsonarray;
		}
		return jsonarray;
	}

	public JSONObject GetServiceNameCache(String searchtype) throws Exception {
		StringBuilder responseSB = null;
		// Encode the query
		String GetData = "typeid=" + searchtype + "&method=get" + "&key=all";

		// Connect to URL
		String hostURL = Util.getHostURLStr("ServiceNameCache");
		Util.getConsoleLogger().debug("hostURL(ServiceNameCache): " + hostURL);
		URL url = new URL( hostURL + "/ServiceNameCache/RESTful/datacache?"+ GetData);
//		URL url = new URL(
//				"http://ws.crm.com.tw:8080/ServiceNameCache/RESTful/datacache?"
//						+ GetData);
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("GET");
		// connection.setRequestProperty("Content-Type",
		// "application/x-www-form-urlencoded");
		// connection.setRequestProperty("Content-Length",
		// String.valueOf(postData.length()));

		// Write data
		// OutputStream os = connection.getOutputStream();
		// os.write(postData.getBytes());

		// Read response
		responseSB = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				connection.getInputStream(), "UTF-8"));

		String line;
		while ((line = br.readLine()) != null)
			responseSB.append(line.trim());

		// Close streams
		br.close();
		// os.close();

		// Util.getConsoleLogger().debug("responseSB: "+responseSB.toString().trim());
		JSONObject ServiceNameCachejsonObj = new JSONObject(
				responseSB.toString());
		return ServiceNameCachejsonObj;
	}

	public JSONObject SetContactLog(String searchkey, String pkey, String date,
			String userdata) throws Exception {
		StringBuilder responseSB = null;
		Util.getConsoleLogger().info("***** SetContactLog userdata: " + userdata);
		Util.getFileLogger().info("***** SetContactLog userdata: " + userdata);
		// Encode the query
		String postData = "searchkey=" + searchkey + "&pkey=" + pkey + "&date="
				+ date + "&userdata=" + URLEncoder.encode(userdata, "utf-8");

		// Connect to URL
		String hostURL = Util.getHostURLStr("IMWebSocket");
		Util.getConsoleLogger().debug("hostURL: " + hostURL);
		URL url = new URL( hostURL + "/IMWebSocket/RESTful/ContactData");
//		URL url = new URL(
//				"http://127.0.0.1:8080/IMWebSocket/RESTful/ContactData");
		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setDoOutput(true);
		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type",
				"application/x-www-form-urlencoded");
		connection.setRequestProperty("Content-Length",
				String.valueOf(postData.length()));

		// Write data
		OutputStream os = connection.getOutputStream();
		os.write(postData.getBytes());

		// Read response
		responseSB = new StringBuilder();
		BufferedReader br = new BufferedReader(new InputStreamReader(
				connection.getInputStream(), "UTF-8"));

		String line;
		while ((line = br.readLine()) != null)
			responseSB.append(line.trim());

		// Close streams
		br.close();
		os.close();

		// Util.getConsoleLogger().debug("responseSB: "+responseSB.toString().trim());
		JSONObject SetContactLogjsonObject = new JSONObject(
				responseSB.toString());
		return SetContactLogjsonObject;
	}
}
