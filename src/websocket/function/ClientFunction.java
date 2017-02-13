package websocket.function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import util.Util;
import websocket.bean.RoomInfo;
import websocket.pools.WebSocketRoomPool;
import websocket.pools.WebSocketTypePool;
import websocket.pools.WebSocketUserPool;

//此類別給WebSocjet.java使用
public class ClientFunction {
	
	/** * findAgentEvent */
	public static void findAgentEvent(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		org.java_websocket.WebSocket sendto = WebSocketUserPool
				.getWebSocketByUser(obj.getString("sendto"));
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "findAgentEvent");
		sendjson.put("from", obj.getString("id"));
		sendjson.put("fromName",  obj.getString("UserName"));
		sendjson.put("channel", obj.getString("channel"));
		WebSocketUserPool.sendMessageToUser(sendto, sendjson.toString());
	}
	
	/** * find online Longest Agent */
	public static void findAgent(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String Agent;
		JSONObject sendjson = new JSONObject();
		try {
			Agent = WebSocketTypePool.getOnlineLongestUserinTYPE("Agent");
			sendjson.put("AgentName", WebSocketUserPool.getUserNameByKey(WebSocketUserPool.getWebSocketByUser(Agent)));
		} catch (Exception e) {
			Agent = null;
		}
		
		sendjson.put("Event", "findAgent");
		sendjson.put("from", obj.getString("id"));
		sendjson.put("Agent",  Agent);
		sendjson.put("channel", obj.getString("channel"));
		WebSocketUserPool.sendMessageToUser(conn, sendjson.toString());
	}
	
	/** * Get user data */
	public static void senduserdata(String message, org.java_websocket.WebSocket conn) {
//		System.out.println("senduserdata()" + message);
//		System.out.println("senduserdata() called ");
		JSONObject obj = new JSONObject(message);
		String lang = obj.getString("lang");
		String searchtype = obj.getString("searchtype");
		JSONObject attributes = obj.getJSONObject("attributes");
		String attributenames = obj.getJSONObject("attributes").getString(
				"attributenames");
		StringBuilder responseSB = null;
//		System.out.println("http://127.0.0.1:8080/IMWebSocket/RESTful/searchUserdata start ****");
		long startTime = System.currentTimeMillis();
		try {
			// Encode the query
			String postData = 
					"&searchtype=" + searchtype
					+ "&attributes=" + attributes.toString()
					+ "&attributenames=" + attributenames
					+ "&lang=" + lang;
			// Connect to URL
			URL url = new URL(
					"http://127.0.0.1:8080/IMWebSocket/RESTful/searchUserdata");
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
			// System.out.println(responseSB);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long endTime = System.currentTimeMillis();
//		System.out.println("RESTful searchUserdata search time: " + (endTime - startTime)/1000 + "s" );
//		System.out.println("http://127.0.0.1:8080/IMWebSocket/RESTful/searchUserdata done ****");

		JSONObject responseSBjson = new JSONObject(responseSB.toString());
		JSONObject sendjson = new JSONObject();
		
		sendjson.put("Event", "senduserdata");
		sendjson.put("from", obj.getJSONObject("attributes").getString("id"));
		sendjson.put("userdata",  responseSBjson);
		sendjson.put("channel", obj.getString("channel"));
		System.out.println("senduserdata() - sendjson" + sendjson);
		WebSocketUserPool.sendMessageToUser(conn, sendjson.toString());
		// 掉換順序
		// 若尚未找到Agent,則會出現JSONException
		// 之後若熟悉RESTful,則可試著將抓取contactID與找到Agent後通知兩方這兩件事情分開處理
		try{
			org.java_websocket.WebSocket sendto = WebSocketUserPool
					.getWebSocketByUser(obj.getString("sendto"));
			sendjson.put("clientID", WebSocketUserPool.getUserID(conn).trim());
			WebSocketUserPool.sendMessageToUser(sendto, sendjson.toString());			
		}catch(org.json.JSONException e) {
//			System.out.println("JSONObject[\"sendto\"] not found.");
		}

	}
	
	/** * send entry log */
	public static void entrylog(String message, org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String userid = obj.getString("userid");
		String username = obj.getString("username");
		String ipaddress = obj.getString("ipaddress");
		String browser = obj.getString("browser");
		String platfrom = obj.getString("platfrom");
		String channel = obj.getString("channel");
		String language = obj.getString("language");
		String enterkey = obj.getString("enterkey");

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
			// Connect to URL
			URL url = new URL(
					"http://127.0.0.1:8080/IMWebSocket/RESTful/ServiceEntry");
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
			// System.out.println(responseSB);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/** * send interaction log */
	public static void interactionlog(String message, org.java_websocket.WebSocket conn) {
		System.out.println("interactionlog() called");
//		JSONObject obj = new JSONObject(message);
		JsonObject obj = Util.getGJsonObject(message);

		String contactid = null;
		String ixnid = null;
		String agentid = null;
		int status = 0;
		String typeid = null;
		int entitytypeid = 0;
		String subtypeid = null;
		String text = null;
//		String structuredtext = null;
		JsonArray structuredtext = null;
		String thecomment = null;
		String stoppedreason = null;
		String activitycode = null;
		String structuredmimetype = null;
		String subject = null;
		String closefrom = null;

		Set<Map.Entry<String, JsonElement>> entriesSet = obj.entrySet(); //will return members of your object
//		for (Map.Entry<String, JsonElement> entry: entrieSet) {
//		    System.out.println(entry.getKey());
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
//			System.out.println("obj.get(\"text\")" + obj.get("text"));
//			System.out.println("obj.get(\"structuredtext\")" + obj.get("structuredtext"));
			// 將RoomInfo對話歷史訊息更新上去
			if (obj.get("text") != null &&
				obj.get("structuredtext") != null){
				text = obj.get("text").getAsString();
				structuredtext = obj.get("structuredtext").getAsJsonArray();				
			}else{
				text = "";
				structuredtext = null;				
			}
			
			String postData = "contactid=" + contactid + "&ixnid=" + ixnid
					+ "&agentid=" + agentid + "&startdate=" + startdate
					+ "&enddate=" + enddate + "&status=" + status + "&typeid="
					+ typeid + "&entitytypeid=" + entitytypeid + "&subtypeid="
					+ subtypeid + "&text=" + text + "&structuredtext="
					+ structuredtext + "&thecomment=" + thecomment
					+ "&stoppedreason=" + stoppedreason + "&activitycode="
					+ activitycode + "&structuredmimetype="
					+ structuredmimetype + "&subject=" + subject;

			// Connect to URL
			URL url = new URL(
					"http://127.0.0.1:8080/IMWebSocket/RESTful/Interaction");
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
			// System.out.println(responseSB);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void setinteraction(String aMsg, org.java_websocket.WebSocket aConn){
		System.out.println("setinteraction() called");
		// 在此更新RoomInfo中的text, structuredtext到interaction log中
			// 拿取需要物件
		JsonObject msgJson = Util.getGJsonObject(aMsg);
		// 如果有room歷史訊息,則更新上最新的interaction上 (這邊邏輯較特別,本應該是拿原本的來更新,但因為setinteraction欄位太多,暫時倒過來處理)
		if (WebSocketUserPool.getUserInteractionByKey(aConn) != null){
			JsonObject msgJsonOld = Util.getGJsonObject(WebSocketUserPool.getUserInteractionByKey(aConn));
			System.out.println("setinteraction() - msgJsonOld: " + msgJsonOld);
			if (msgJsonOld.get("text") != null &&
				msgJsonOld.get("structuredtext") != null){
				msgJson.addProperty("text", msgJsonOld.get("text").getAsString());
				msgJson.add("structuredtext", msgJsonOld.get("structuredtext").getAsJsonArray());							
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
		
//		System.out.println("setinteraction - msgJson: " + msgJson);
		
		// 更新UserInteraction
		WebSocketUserPool.addUserInteraction(msgJson.toString(), aConn);
//		WebSocketUserPool.addUserInteraction(aMsg, aConn);
	}
	
}
