package websocket.function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Set;

import org.json.JSONObject;

import websocket.pools.WebSocketRoomPool;
import websocket.pools.WebSocketTypePool;
import websocket.pools.WebSocketUserPool;

//此類別給WebSocjet.java使用
public class ClientFunction {
	/** * Client close Group */
	public static void Clientclosegroup(String message,
			org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String group = obj.getString("group");
		System.out.println("Clientclosegroup() - group " + group);
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "Clientclosegroup");
		sendjson.put("from", obj.getString("id"));
		sendjson.put("fromName",  obj.getString("UserName"));
		sendjson.put("channel", obj.getString("channel"));
		WebSocketRoomPool.sendMessageinroom(group,sendjson.toString());
	}
	
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
		try {
			Agent = WebSocketTypePool.getOnlineLongestUserinTYPE("Agent");
		} catch (Exception e) {
			Agent = null;
		}
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "findAgent");
		sendjson.put("from", obj.getString("id"));
		sendjson.put("Agent",  Agent);
		sendjson.put("channel", obj.getString("channel"));
		WebSocketUserPool.sendMessageToUser(conn, sendjson.toString());
	}
	
	/** * Get user data */
	public static void senduserdata(String message, org.java_websocket.WebSocket conn) {
		System.out.println(message);
		JSONObject obj = new JSONObject(message);
		String lang = obj.getString("lang");
		String searchtype = obj.getString("searchtype");
		JSONObject attributes = obj.getJSONObject("attributes");
		String attributenames = obj.getJSONObject("attributes").getString(
				"attributenames");
		StringBuilder responseSB = null;
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

		JSONObject responseSBjson = new JSONObject(responseSB.toString());
		JSONObject sendjson = new JSONObject();
		
		sendjson.put("Event", "searchuserdata");
		sendjson.put("from", obj.getJSONObject("attributes").getString("id"));
		sendjson.put("userdata",  responseSBjson);
		sendjson.put("channel", obj.getString("channel"));
		WebSocketUserPool.sendMessageToUser(conn, sendjson.toString());
		// 掉換順序
		org.java_websocket.WebSocket sendto = WebSocketUserPool
				.getWebSocketByUser(obj.getString("sendto"));
		WebSocketUserPool.sendMessageToUser(sendto, sendjson.toString());
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
		JSONObject obj = new JSONObject(message);

		String contactid = null;
		String ixnid = null;
		String agentid = null;
		int status = 0;
		String typeid = null;
		int entitytypeid = 0;
		String subtypeid = null;
		String text = null;
		String structuredtext = null;
		String thecomment = null;
		String stoppedreason = null;
		String activitycode = null;
		String structuredmimetype = null;
		String subject = null;
		String closefrom = null;

		Set<String> keySet = obj.keySet();
		synchronized (keySet) {
			for (String key : keySet) {
				switch (key) {
				case "contactid":
					contactid = obj.getString(key);
					break;
				case "ixnid":
					ixnid = obj.getString(key);
					break;
				case "agentid":
					agentid = obj.getString(key);
					break;
				case "status":
					status = obj.getInt(key);
					break;
				case "typeid":
					typeid = obj.getString(key);
					break;
				case "entitytypeid":
					entitytypeid = obj.getInt(key);
					break;
				case "subtypeid":
					subtypeid = obj.getString(key);
					break;
				case "text":
					text = obj.getString(key);
					break;
				case "structuredtext":
					structuredtext = obj.getString(key);
					break;
				case "thecomment":
					thecomment = obj.getString(key);
					break;
				case "stoppedreason":
					stoppedreason = obj.getString(key);
					break;
				case "activitycode":
					activitycode = obj.getString(key);
					break;
				case "structuredmimetype":
					structuredmimetype = obj.getString(key);
					break;
				case "subject":
					subject = obj.getString(key);
					break;
				case "closefrom":
					closefrom = obj.getString(key);
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
		Date startdated = null;
		try {
			startdated = getsdf.parse(obj.getString("startdate"));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		String startdate = sdf.format(startdated);
		StringBuilder responseSB = null;
		try {
			// Encode the query
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
	
	public static void setinteraction(String message, org.java_websocket.WebSocket conn){
		System.out.println("setinteraction:"+message);
		WebSocketUserPool.addUserInteraction(message, conn);
	}
	
}
