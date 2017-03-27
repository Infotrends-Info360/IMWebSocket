package websocket.function;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.AbstractMap;
import java.util.ArrayList;
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
import com.google.gson.JsonNull;
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
		JsonObject obj = Util.getGJsonObject(message);
		String lang = Util.getGString(obj, "lang");
		String searchtype = Util.getGString(obj, "searchtype");
		String sendto = Util.getGString(obj, "sendto");
		String channel = Util.getGString(obj, "channel");
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
			
			if (sendto != null){
				postData += "&sendto=" + sendto;
			}
			
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
		
		Util.getConsoleLogger().debug("responseSB.toString(): " + responseSB.toString());
		
		JSONObject responseSBjson = new JSONObject(responseSB.toString());
		Util.getConsoleLogger().debug("senduserdata result: " + responseSBjson);
		Util.getFileLogger().debug("senduserdata result: " + responseSBjson);

		/** 組出senduserdata送出JSON物件 **/
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "senduserdata");
		sendjson.put("from", userID);
		sendjson.put("userdata",  responseSBjson);
		sendjson.put("channel", channel);
		
		/** 通知Agent, 其Client的userdata資料(loginj狀況) **/		
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
		
		/** 通知client, 其userdata資料(loginj狀況) **/
		if (sendtoConn == null){
//		Util.getConsoleLogger().debug("senduserdata() - sendjson" + sendjson);
			WebSocketUserPool.sendMessageToUser(conn, sendjson.toString());			
		}

		

	}
	
	/** * send entry log */
	public static void entrylog(String message, org.java_websocket.WebSocket conn) {
		Util.getConsoleLogger().debug("entrylog() called");
		JsonObject obj = Util.getGJsonObject(message);
		String userid = Util.getGString(obj, "userid");
		String username = Util.getGString(obj, "username");
		String ipaddress = Util.getGString(obj, "ipaddress");
		String browser = Util.getGString(obj, "browser");
		String platfrom = Util.getGString(obj, "platfrom");
		String channel = Util.getGString(obj, "channel");
		String language = Util.getGString(obj, "language");
		String enterkey = Util.getGString(obj, "enterkey");
		String contactid = Util.getGString(obj, "contactid");

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
					+ "&enterkey=" + enterkey;
			if (contactid != null)
				postData += "&contactid=" + contactid;
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

		SimpleDateFormat sdf = new SimpleDateFormat( Util.getSdfDateTimeFormat());
		// 抓取使用者登入時間
		String startdate = sdf.format(WebSocketUserPool.getStartdateByKey(conn));
		obj.addProperty("startdate", startdate);
		// 放入使用者登出時間
		Date now = new Date();
		String enddate = sdf.format(now);
		obj.addProperty("enddate", enddate);
		
		/** 開始建立請求Json物件List **/
		Set<Map.Entry<String, JsonElement>> entriesSet = obj.entrySet(); //will return members of your object
		List<AbstractMap.SimpleEntry<String, String>> params = new ArrayList<>();
		synchronized (entriesSet) {
			for (Map.Entry<String, JsonElement> entry : entriesSet) {
				// "structuredtext" value 為JsonArray型態,故須特別處理
				if (entry.getKey().equals("structuredtext")) {
					params.add(new AbstractMap.SimpleEntry<String, String>(entry.getKey(), entry.getValue().toString()));
					continue;
				}
				// 進行新增
				params.add(new AbstractMap.SimpleEntry<String, String>(entry.getKey(), entry.getValue().getAsString()));
				// 特殊處理-若closefrom為"server:heartbeatlose",則連帶更新status,activitycode
				if (entry.getKey().equals("closefrom") && entry.getValue().equals("server:heartbeatlose")){
					params.add(new AbstractMap.SimpleEntry<String, String>("status","3"));
					params.add(new AbstractMap.SimpleEntry<String, String>("activitycode", entry.getValue().getAsString()));
				}				
			}// end of for
		}// end of synchronized
		
		/** 建立連線 **/
		StringBuilder responseSB = null;
		try {
			// 連線建立設定
			String hostURL = Util.getHostURLStr("IMWebSocket");
//			Util.getConsoleLogger().debug("hostURL: " + hostURL);
			URL url = new URL( hostURL + "/IMWebSocket/RESTful/Interaction");
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded; charset=utf-8");
			connection.setRequestProperty("Content-Length",  Integer.toString(util.Util.getQuery(params).length()));
			// 將請求資訊寫入request中
			OutputStream os = connection.getOutputStream();
			BufferedWriter writer = new BufferedWriter( new OutputStreamWriter(os, "UTF-8"));
			writer.write(util.Util.getQuery(params));
			writer.flush();
			writer.close();
			os.close();
			// 建立連線
			connection.connect();
			// 拿取結果(下面暫時沒用到)
			responseSB = new StringBuilder();
			BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
			String line;
			while ((line = br.readLine()) != null)
				responseSB.append(line);
			br.close();
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
		msgJson.addProperty("userid", userInfo.getUserid()); // 注意: 先暫時這樣加, 讓interaction與serviceEntry能透過userid來連結
		Util.getConsoleLogger().debug("setinteraction() - userID: " + Util.getGString(msgJson, "userID"));
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
