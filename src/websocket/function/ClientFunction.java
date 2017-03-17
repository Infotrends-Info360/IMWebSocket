package websocket.function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import util.StatusEnum;
import util.Util;
import websocket.bean.RoomInfo;
import websocket.bean.SystemInfo;
import websocket.bean.UpdateStatusBean;
import websocket.bean.UserInfo;
import websocket.pools.WebSocketRoomPool;
import websocket.pools.WebSocketTypePool;
import websocket.pools.WebSocketUserPool;
import websocket.thread.findAgent.FindAgentThread;

//此類別給WebSocjet.java使用
public class ClientFunction {
	
	/** * findAgentEvent */
	public static void findAgentEvent(String message, org.java_websocket.WebSocket conn) {
		Util.getConsoleLogger().debug("findAgentEvent() called");
		JSONObject obj = new JSONObject(message);
		org.java_websocket.WebSocket sendto = WebSocketUserPool
				.getWebSocketByUserID(obj.getString("sendto"));
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "findAgentEvent");
		sendjson.put("from", obj.getString("id"));
		sendjson.put("fromName",  obj.getString("UserName"));
		sendjson.put("channel", obj.getString("channel"));
		WebSocketUserPool.sendMessageToUser(sendto, sendjson.toString());
	}
	
	/** * find online Longest Agent */
	public static void findAgent(final String aMessage, final org.java_websocket.WebSocket aConn) {
		JSONObject obj = new JSONObject(aMessage);
		String userID = WebSocketUserPool.getUserID(aConn);
		String userName = WebSocketUserPool.getUserNameByKey(aConn);
		String ACType = WebSocketTypePool.getUserType(aConn);
		String AgentID;
		JSONObject sendjson = new JSONObject();
		try {
			AgentID = WebSocketTypePool.getOnlineLongestUserinTYPE(aConn, "Agent"); // this method will block current thread if readyAgentQueue is empty
			sendjson.put("AgentName", WebSocketUserPool.getUserNameByKey(WebSocketUserPool.getWebSocketByUserID(AgentID)));
		} catch (Exception e) {
			AgentID = null;
			e.printStackTrace();
			Util.getFileLogger().info(FindAgentThread.TAG + "agent not found");
			Util.getFileLogger().error(e.getMessage());
		}
		Util.getFileLogger().info(FindAgentThread.TAG + " ************ agent found - agentID: " + AgentID);
		Util.getConsoleLogger().info(FindAgentThread.TAG + " ************ agent found - agentID: " + AgentID);
//				Util.getConsoleLogger().debug("findAgent : " + Agent);
		
		// 在這邊執行senduserdata
		// 若有找到Agent, 去抓senduserdata, 並將此資訊送給Agent
		if (AgentID != null){
			JsonObject senduserdataObj = new JsonObject();
			senduserdataObj.addProperty("lang", "chiname");
			senduserdataObj.addProperty("sendto", AgentID); // 重點
			senduserdataObj.addProperty("searchtype", "A");
			senduserdataObj.addProperty("channel", "chat");
			
			ClientFunction.senduserdata(senduserdataObj.toString(), aConn);
			
			// 新增系統訊息
			org.java_websocket.WebSocket agentConn = WebSocketUserPool.getWebSocketByUserID(AgentID);
			sendjson.put(SystemInfo.TAG_SYS_MSG, SystemInfo.getWaitingForAgentMsg(WebSocketUserPool.getUserNameByKey(agentConn)));
		}
		
		sendjson.put("Event", "findAgent");
		sendjson.put("from", obj.getString("id"));
		sendjson.put("Agent",  AgentID);
		sendjson.put("channel", obj.getString("channel"));
		WebSocketUserPool.sendMessageToUser(aConn, sendjson.toString());
	}
	
	/** * Get user data */
	public static void senduserdata(String message, org.java_websocket.WebSocket conn) {
//		Util.getConsoleLogger().debug("senduserdata()" + message);
		Util.getConsoleLogger().debug("senduserdata() called ");
		Util.getFileLogger().info("senduserdata() called ");
		JSONObject obj = new JSONObject(message);
		String lang = obj.getString("lang");
		String searchtype = obj.getString("searchtype");
		String sendto = obj.getString("sendto");
		String channel = obj.getString("channel");
		String userID = WebSocketUserPool.getUserID(conn);
		String userName = WebSocketUserPool.getUserNameByKey(conn);
		
		StringBuilder responseSB = null;
		long startTime = System.currentTimeMillis();
		try {
			// Encode the query
			String postData = 
					"&searchtype=" + searchtype
					+ "&userID=" + userID
					+ "&userName=" + userName
					+ "&lang=" + lang;
			// Connect to URL
			String hostURL = Util.getHostURLStr("IMWebSocket");
//			Util.getConsoleLogger().debug("hostURL: " + hostURL);
			URL url = new URL( hostURL + "/IMWebSocket/RESTful/searchUserdata");
//			URL url = new URL(
//					"http://127.0.0.1:8080/IMWebSocket/RESTful/searchUserdata");

			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
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
				responseSB.append(line);
			// Close streams
			br.close();
			os.close();
			// Util.getConsoleLogger().debug(responseSB);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
		Util.getConsoleLogger().debug("RESTful searchUserdata search time: " + (endTime - startTime)/1000 + "s" );
		Util.getFileLogger().debug("RESTful searchUserdata search time: " + (endTime - startTime)/1000 + "s" );
		
		
		JSONObject responseSBjson = new JSONObject(responseSB.toString());
		Util.getConsoleLogger().debug("senduserdata result: " + responseSBjson);
		Util.getFileLogger().debug("senduserdata result: " + responseSBjson);
		
		JSONObject sendjson = new JSONObject();
		
		sendjson.put("Event", "senduserdata");
		sendjson.put("from", userID);
		sendjson.put("userdata",  responseSBjson);
		sendjson.put("channel", channel);
//		Util.getConsoleLogger().debug("senduserdata() - sendjson" + sendjson);
		WebSocketUserPool.sendMessageToUser(conn, sendjson.toString());
		// 通知Agent,有新的通話請求 RING
		// 掉換順序
		// 若尚未找到Agent,則會出現JSONException
		// 之後若熟悉RESTful,則可試著將抓取contactID與找到Agent後通知兩方這兩件事情分開處理
		org.java_websocket.WebSocket sendtoConn = null;
		sendtoConn = WebSocketUserPool.getWebSocketByUserID(sendto);
		Util.getConsoleLogger().debug("sendtoConn: " + sendtoConn);
		Util.getConsoleLogger().debug("WebSocketUserPool.getUserNameByKey(sendtoConn): " + WebSocketUserPool.getUserNameByKey(sendtoConn));
		
		if (sendtoConn != null){
			sendjson.put("clientID", WebSocketUserPool.getUserID(conn).trim());
			sendjson.put("clientName", WebSocketUserPool.getUserNameByKey(conn).trim());
			WebSocketUserPool.sendMessageToUser(sendtoConn, sendjson.toString());			
			
			// 開始RING倒數機制
//				// 3. RING狀態開始
			Util.getStatusFileLogger().info("###### [findAgent]");
			UpdateStatusBean usb = new UpdateStatusBean();
			usb.setStatus(StatusEnum.RING.getDbid());
			usb.setStartORend("start");
			usb.setClientID( WebSocketUserPool.getUserID(conn));
			CommonFunction.updateStatus(new Gson().toJson(usb), sendtoConn);				
		}// end of if 
//		String status_dbid = Util.getGString(obj, "status"); // 以數字代表 dbid
//		String startORend = Util.getGString(obj, "startORend"); 
//		String clientID = Util.getGString(obj, "clientID"); // for RING

	}
	
	/** * send entry log */
	public static void entrylog(String message, org.java_websocket.WebSocket conn) {
		Util.getConsoleLogger().debug("entrylog() called");
		JSONObject obj = new JSONObject(message);
		String userid = obj.getString("userid");
		String username = obj.getString("username");
		String ipaddress = obj.getString("ipaddress");
		String browser = obj.getString("browser");
		String platfrom = obj.getString("platfrom");
		String channel = obj.getString("channel");
		String language = obj.getString("language");
		String enterkey = obj.getString("enterkey");
		String contactid = obj.getString("contactid");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date now = new Date();
		String entertime = sdf.format(now);
		StringBuilder responseSB = null;
		try {
			// Encode the query
			String postData = "userid=" + userid + "&username=" + username
					+ "&entertime=" + entertime + "&ipaddress=" + ipaddress
					+ "&browser=" + browser + "&platfrom=" + platfrom
					+ "&channel=" + channel + "&language=" + language
					+ "&enterkey=" + enterkey + "&contactid=" + contactid;
			// Connect to URL
			String hostURL = Util.getHostURLStr("IMWebSocket");
//			Util.getConsoleLogger().debug("hostURL: " + hostURL);
			URL url = new URL( hostURL + "/IMWebSocket/RESTful/ServiceEntry");			
//			URL url = new URL(
//					"http://127.0.0.1:8080/IMWebSocket/RESTful/ServiceEntry");

			
			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
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
				responseSB.append(line);
			// Close streams
			br.close();
			os.close();
			// Util.getConsoleLogger().debug(responseSB);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** * send interaction log */
	public static void interactionlog(String message, org.java_websocket.WebSocket conn) {
		// 可考慮去除在client.js儲存的多於資訊,只讓client.js傳入重要key值,如ixnid, contactid,
		// 其餘資料皆由server保存,在透過key值去取用,如取得typeid, status, text等,之後整理可考慮進行
		Util.getConsoleLogger().debug("interactionlog() called");
		Util.getConsoleLogger().debug("interactionlog - message: " + message);
		Util.getFileLogger().info("interactionlog - message: " + message);
//		JSONObject obj = new JSONObject(message);
		JsonObject obj = Util.getGJsonObject(message);

		String contactid = "";
		String ixnid = "";
		String agentid = "";
		int status = 0;
		String typeid = "";
		int entitytypeid = 0;
		String subtypeid = "";
		String text = "";
//		String structuredtext = null;
		JsonArray structuredtext = null;
		String structuredtextStr = "";
		String thecomment = "";
		String stoppedreason = "";
		String activitycode = "";
		String structuredmimetype = "";
		String subject = "";
		String closefrom = "";

		Set<Map.Entry<String, JsonElement>> entriesSet = obj.entrySet(); //will return members of your object
//		for (Map.Entry<String, JsonElement> entry: entrieSet) {
//		    Util.getConsoleLogger().debug(entry.getKey());
//		}				
//		Set<String> keySet = obj. .keySet();
		synchronized (entriesSet) {
			for (Map.Entry<String, JsonElement> entry : entriesSet) {
				switch (entry.getKey()) {
				case "contactid":
					contactid = entry.getValue().getAsString();
					break;
				case "ixnid":
					ixnid = entry.getValue().getAsString();
					break;
				case "agentid":
					Util.getConsoleLogger().debug("entry.getValue().getAsString(): " + entry.getValue().getAsString());
//					agentid = entry.getValue().getAsString();
					agentid = entry.getValue().getAsString();
					break;
				case "status":
					status = entry.getValue().getAsInt();
					break;
				case "typeid":
					typeid = entry.getValue().getAsString();
					break;
				case "entitytypeid":
					entitytypeid = entry.getValue().getAsInt();
					break;
				case "subtypeid":
					subtypeid = entry.getValue().getAsString();
					break;
//				case "text":
//					text = obj.getString(key);
//					break;
//				case "structuredtext":
////					structuredtext = obj.getString(key);
//					structuredtext = obj.getJSONArray(key);
//					break;
				case "thecomment":
					thecomment = entry.getValue().getAsString();
					break;
				case "stoppedreason":
					stoppedreason = entry.getValue().getAsString();
					break;
				case "activitycode":
					activitycode = entry.getValue().getAsString();
					break;
				case "structuredmimetype":
					structuredmimetype = entry.getValue().getAsString();
					break;
				case "subject":
					subject = entry.getValue().getAsString();
					break;
				case "closefrom":
					closefrom = entry.getValue().getAsString();
					break;
				}
			}
		}
		
		if(closefrom.equals("server:heartbeatlose")){
			status=3;
			activitycode=closefrom;
		}

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date now = new Date();
		String enddate = sdf.format(now);

		SimpleDateFormat getsdf = new SimpleDateFormat(
				"yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
		// 抓取使用者登入時間
		String startdate = sdf.format(WebSocketUserPool.getStartdateByKey(conn));

		StringBuilder responseSB = null;
		try {
			// Encode the query
//			Util.getConsoleLogger().debug("obj.get(\"text\")" + obj.get("text"));
//			Util.getConsoleLogger().debug("obj.get(\"structuredtext\")" + obj.get("structuredtext"));
			// 將RoomInfo對話歷史訊息更新上去
			if (obj.get("text") != null &&
				obj.get("structuredtext") != null){
				text = obj.get("text").getAsString();
				structuredtext = obj.get("structuredtext").getAsJsonArray();
				structuredtextStr = URLEncoder.encode(structuredtext.toString(), "utf-8");
				
			}else{
				
			}
			
			String postData = "contactid=" + contactid + "&ixnid=" + ixnid
					+ "&agentid=" + agentid + "&startdate=" + startdate
					+ "&enddate=" + enddate + "&status=" + status + "&typeid="
					+ typeid + "&entitytypeid=" + entitytypeid + "&subtypeid="
					+ subtypeid + "&text=" + URLEncoder.encode(text, "utf-8") + "&structuredtext="
					+ structuredtextStr + "&thecomment=" + thecomment
					+ "&stoppedreason=" + stoppedreason + "&activitycode="
					+ activitycode + "&structuredmimetype="
					+ structuredmimetype + "&subject=" + subject;
			

			// Connect to URL
			String hostURL = Util.getHostURLStr("IMWebSocket");
//			Util.getConsoleLogger().debug("hostURL: " + hostURL);
			URL url = new URL( hostURL + "/IMWebSocket/RESTful/Interaction");
//			URL url = new URL(
//					"http://127.0.0.1:8080/IMWebSocket/RESTful/Interaction");

			HttpURLConnection connection = (HttpURLConnection) url
					.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type",
					"application/x-www-form-urlencoded; charset=utf-8");
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
				responseSB.append(line);
			// Close streams
			br.close();
			os.close();
			// Util.getConsoleLogger().debug(responseSB);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void setinteraction(String aMsg, org.java_websocket.WebSocket aConn){
		Util.getConsoleLogger().debug("setinteraction() called");
		UserInfo userInfo = WebSocketUserPool.getUserInfoByKey(aConn);
		// 在此更新RoomInfo中的text, structuredtext到interaction log中
			// 拿取需要物件
		JsonObject msgJson = Util.getGJsonObject(aMsg);
		// 如果有room歷史訊息,則更新上最新的interaction上 (這邊邏輯較特別,本應該是拿原本的來更新,但因為setinteraction欄位太多,暫時倒過來處理)
		if (WebSocketUserPool.getUserInteractionByKey(aConn) != null){
			JsonObject msgJsonOld = Util.getGJsonObject(WebSocketUserPool.getUserInteractionByKey(aConn));
			Util.getConsoleLogger().debug("setinteraction() - msgJsonOld: " + msgJsonOld);
			if (msgJsonOld.get("text") != null &&
				msgJsonOld.get("structuredtext") != null){
				msgJson.addProperty("text", msgJsonOld.get("text").getAsString());
				msgJson.add("structuredtext", msgJsonOld.get("structuredtext").getAsJsonArray());							
			}
			
			// 若曾經有Agent更改過Client ContactID,則用舊的就好(暫時處理方案,之後須整理interaction相關流程與傳輸資料)
			if (userInfo.isContactIDupdatedByAgent()){
				msgJson.addProperty("contactid", msgJsonOld.get("contactid").getAsString());
			}
			
		}
		

//		List<String> roomID = WebSocketUserPool.getUserRoomByKey(aConn);
//			// 因此方法只有Client呼叫,故最多一個Client也就只有一個roomID,若有再更新即可
//		if (roomID.size() == 1){
//			RoomInfo roomInfo = WebSocketRoomPool.getRoomInfo(roomID.get(0));
//			msgJson.addProperty("text", roomInfo.getText().toString());
//			msgJson.add("structuredtext", roomInfo.getStructuredtext());
//		}else{
//			msgJson.addProperty("text", "");
//			msgJson.add("structuredtext", null);			
//		}
		
//		Util.getConsoleLogger().debug("setinteraction - msgJson: " + msgJson);
		
		// 更新UserInteraction
		WebSocketUserPool.addUserInteraction(msgJson.toString(), aConn);
//		WebSocketUserPool.addUserInteraction(aMsg, aConn);
	}
	
}
