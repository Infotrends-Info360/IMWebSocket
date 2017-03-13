package websocket.function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.java_websocket.WebSocket;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;

import util.StatusEnum;
import util.Util;
import websocket.bean.SystemInfo;
import websocket.bean.UpdateStatusBean;
import websocket.bean.UserInfo;
import websocket.pools.WebSocketRoomPool;
import websocket.pools.WebSocketTypePool;
import websocket.pools.WebSocketUserPool;

//此類別給WebSocjet.java使用
public class AgentFunction {

	/** * send Accept Event */
	// (此方法已沒在使用)
	public static void AcceptEvent(String message,
			org.java_websocket.WebSocket conn) {
		Util.getConsoleLogger().debug("AcceptEvent() called");
		JSONObject obj = new JSONObject(message);
		String roomID = obj.getString("roomID");
		org.java_websocket.WebSocket sendto = WebSocketUserPool
				.getWebSocketByUser(obj.getString("sendto"));
				
		/*** 寄送"AcceptEvent"事件 ***/
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "AcceptEvent");
		sendjson.put("from", obj.getString("id"));
		sendjson.put("fromName", obj.getString("UserName"));
		sendjson.put("roomID", roomID);
		sendjson.put("channel", obj.getString("channel"));
		WebSocketUserPool.sendMessageToUser(sendto, sendjson.toString());
	}

	/** * RejectEvent */
	public static void RejectEvent(String message,
			org.java_websocket.WebSocket aConn) {
		JSONObject obj = new JSONObject(message);
		org.java_websocket.WebSocket sendto = WebSocketUserPool
				.getWebSocketByUser(obj.getString("sendto"));
		UserInfo agentUserInfo = WebSocketUserPool.getUserInfoByKey(aConn);
		
		/*** Agent - 更新狀態 ***/
		UpdateStatusBean usb = null;
		// RING狀態結束
		agentUserInfo.setStopRing(true);
		
		/*** 通知已處理完成RejectEvent ***/
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "RejectEvent");
		sendjson.put("from", obj.getString("id"));
		sendjson.put("fromName", obj.getString("UserName"));
		sendjson.put("channel", obj.getString("channel"));
		sendjson.put(SystemInfo.TAG_SYS_MSG, SystemInfo.getCancelLedReqMsg()); // 增加系統訊息
		WebSocketUserPool.sendMessageToUser(sendto, sendjson.toString());
		WebSocketUserPool.sendMessageToUser(aConn, sendjson.toString());
	}

	/** * get Agent Status */
	public static void getUserStatus(String message,
			org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String ACtype = obj.getString("ACtype");
		StatusEnum statusEnum = WebSocketTypePool.getUserStatusByKeyinTYPE(ACtype, conn);
		String reason = WebSocketTypePool
				.getUserReasonByKeyinTYPE(ACtype, conn);
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "getUserStatus");
		sendjson.put("from", obj.getString("id"));
		sendjson.put("Status", statusEnum.getDbid());
		sendjson.put("Reason", reason);
		sendjson.put("channel", obj.getString("channel"));
		WebSocketUserPool.sendMessageToUser(conn, sendjson.toString());
	}

	/** * create a roomId * @param message */
	public static void createRoomId(String message,
			org.java_websocket.WebSocket conn) {
		String roomId = java.util.UUID.randomUUID().toString();
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "createroomId");
		sendjson.put("roomId", roomId);
		WebSocketUserPool.sendMessageToUser(conn, sendjson.toString());
	}

	/** * Get Message from Agent or Client list */
	// 此方法尚未用到,廣播用途,給Agent呼叫,功用為發給所有Agent
	public static void getMessageinTYPE(String message,
			org.java_websocket.WebSocket conn) {
		JSONObject obj = new JSONObject(message);
		String ACtype = obj.getString("ACtype");
		String username = obj.getString("UserName");
		String text = obj.getString("text");
		WebSocketTypePool.sendMessageinTYPE(ACtype, username + ": " + text);
	}

	public static void refreshAgentList() {
		Util.getConsoleLogger().debug("refreshAgentList() called");
		Map<WebSocket, UserInfo> agentMap = WebSocketTypePool
				.getTypeconnections().get("Agent");
		Collection<String> agentIDList = WebSocketTypePool
				.getOnlineUserIDinTYPE("Agent");
//		Util.getConsoleLogger().debug("refreshAgentList() - agentIDList.size(): " 	+ agentIDList.size());
		Set<WebSocket> agentConnList = agentMap.keySet();
		for (WebSocket tmpConn : agentConnList) {

			JSONObject sendjson02 = new JSONObject();
			sendjson02.put("Event", "refreshAgentList");
			sendjson02.put("agentIDList", agentIDList);
			if (tmpConn.isClosed() || tmpConn.isClosing()) continue;
			WebSocketUserPool.sendMessageToUser(tmpConn, sendjson02.toString());
		}
	}
	
	//從DB撈取出AgentReason,並塞入Util.setAgentReason Bean，flag請塞"0"
	public static void GetAgentReasonInfo(String flag) {
		
		StringBuilder responseSB = null;

		String postData = "flag="+flag;

		try {
			// Connect to URL
			String hostURL = Util.getHostURLStr("IMWebSocket");
//			Util.getConsoleLogger().debug("hostURL: " + hostURL);
			URL url = new URL( hostURL + "/IMWebSocket/RESTful/Select_agentreason");
//			URL url = new URL(
//					"http://127.0.0.1:8080/IMWebSocket/RESTful/Select_agentreason");

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
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		JSONObject jsonobject = new JSONObject(responseSB.toString());
//		Util.getConsoleLogger().debug("jsonobject: "+jsonobject);
		JSONArray jsonarray= jsonobject.getJSONArray("agentreason");
//		Util.getConsoleLogger().debug("GetAgentReasonInfo - jsonarray: " + jsonarray);
		Map<String, Map<String, String>> agentreasonmap = new HashMap<String, Map<String, String>>();
		for(int a = 0; a < jsonarray.length(); a++){
			Map<String, String> agentreasonmapinfo = new HashMap<String, String>();
			agentreasonmapinfo.put("flag", jsonarray.getJSONObject(a).getString("flag"));
			agentreasonmapinfo.put("dbid", String.valueOf(jsonarray.getJSONObject(a).getInt("dbid")));
			agentreasonmapinfo.put("description", jsonarray.getJSONObject(a).getString("description"));
			agentreasonmapinfo.put("alarmcolor", jsonarray.getJSONObject(a).getString("alarmcolor"));
			agentreasonmapinfo.put("statusname_tw", jsonarray.getJSONObject(a).getString("statusname_tw"));
			agentreasonmapinfo.put("statusname_en", jsonarray.getJSONObject(a).getString("statusname_en"));
			agentreasonmapinfo.put("statusname_cn", jsonarray.getJSONObject(a).getString("statusname_cn"));
			agentreasonmapinfo.put("alarmduration", jsonarray.getJSONObject(a).getString("alarmduration"));
			agentreasonmap.put(jsonarray.getJSONObject(a).getString("statusname"), agentreasonmapinfo);
		}
		Util.setAgentReason(agentreasonmap);
	}
	
	//寫入狀態開始時間，請給入Person id、Status id(請至Util.getAgentStatus取得列表)、Reason id(請至Util.getAgentReason取得列表)
	public static String RecordStatusStart(String person_dbid,
			String status_dbid, String reason_dbid) {
		
		StringBuilder responseSB = null;

		String postData = "person_dbid=" + person_dbid + "&status_dbid="
				+ status_dbid + "&reason_dbid=" + reason_dbid;

		try {
			// Connect to URL
			String hostURL = Util.getHostURLStr("IMWebSocket");
//			Util.getConsoleLogger().debug("hostURL: " + hostURL);
			URL url = new URL( hostURL + "/IMWebSocket/RESTful/Insert_rpt_agentstatus");
//			URL url = new URL(
//					"http://127.0.0.1:8080/IMWebSocket/RESTful/Insert_rpt_agentstatus");

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
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
		JSONObject jsonobject = new JSONObject(responseSB.toString());
//		String dbid = jsonobject.getString("dbid"); // exception: "dbid" not a String type
		String dbid = Integer.toString(jsonobject.getInt("dbid"));
//		Util.getConsoleLogger().debug("RecordStatusStart() - dbid: " + dbid);
		
		return dbid;
	}
	
	//寫入狀態結束時間，狀態開始時會回傳dbid，請記住並回入在此
	public static String RecordStatusEnd(String dbid) {
		Util.getConsoleLogger().debug("RecordStatusEnd() called");
		StringBuilder responseSB = null;
		
		String postData = "dbid=" + dbid;

		try {
			// Connect to URL
			String hostURL = Util.getHostURLStr("IMWebSocket");
//			Util.getConsoleLogger().debug("hostURL: " + hostURL);
			URL url = new URL( hostURL + "/IMWebSocket/RESTful/Update_rpt_agentstatus");
//			URL url = new URL(
//					"http://127.0.0.1:8080/IMWebSocket/RESTful/Update_rpt_agentstatus");

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
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		
		return responseSB.toString();
	}

	//寫入Activity log與更新interaction
	public static String RecordActivitylog(String interactionid, String activitydataids, String comment) {
			
			StringBuilder responseSB = null;
			
			String postData = "interactionid=" + interactionid
					+"&activitydataids=" + activitydataids
					+"&comment="+comment;

			try {
				// Connect to URL
				String hostURL = Util.getHostURLStr("IMWebSocket");
//				Util.getConsoleLogger().debug("hostURL: " + hostURL);
				URL url = new URL( hostURL + "/IMWebSocket/RESTful/Insert_rpt_activitylog");
//				URL url = new URL(
//						"http://127.0.0.1:8080/IMWebSocket/RESTful/Insert_rpt_activitylog");

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
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			
			return responseSB.toString();
		}
	
}
