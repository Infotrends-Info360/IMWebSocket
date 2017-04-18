package BackendService.function;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.java_websocket.WebSocket;
import org.json.JSONArray;
import org.json.JSONObject;

import BackendService.bean.RoomInfo;
import BackendService.bean.SystemInfo;
import BackendService.bean.ThirdPartyBean;
import BackendService.bean.UpdateStatusBean;
import BackendService.bean.UserInfo;
import BackendService.pools.WebSocketRoomPool;
import BackendService.pools.WebSocketUserPool;
import BackendService.thread.rings.RingCountDownConfTask;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import util.StatusEnum;
import util.Util;

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
				.getWebSocketByUserID(obj.getString("sendto"));
				
		/*** 寄送"AcceptEvent"事件 ***/
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "AcceptEvent");
		sendjson.put("from", obj.getString("id"));
		sendjson.put("fromName", obj.getString("UserName"));
		sendjson.put("roomID", roomID);
		sendjson.put("channel", obj.getString("channel"));
		WebSocketUserPool.sendMessageToUserWithTryCatch(sendto, sendjson.toString());
	}

	/** * RejectEvent */
	public static void RejectEvent(String message,
			org.java_websocket.WebSocket aConn) {
		JSONObject obj = new JSONObject(message);
		org.java_websocket.WebSocket sendto = WebSocketUserPool
				.getWebSocketByUserID(obj.getString("sendto"));
		UserInfo agentUserInfo = WebSocketUserPool.getUserInfoByKey(aConn);
		
		/*** Agent - 更新狀態 ***/
		UpdateStatusBean usb = null;
		// RING狀態結束
		agentUserInfo.setRingEndExpected(true);
		agentUserInfo.setStopRing(true);
		// 寫入REJECT狀態開始與結束
		Util.getStatusFileLogger().info("###### [RejectEvent()]");
		usb = new UpdateStatusBean();
		usb.setStatus(StatusEnum.REJECT.getDbid());
		usb.setStartORend("start");
		CommonFunction.updateStatus(new Gson().toJson(usb), aConn);	
		
		
		/*** 通知已處理完成RejectEvent ***/
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "RejectEvent");
		sendjson.put("from", obj.getString("id"));
		sendjson.put("fromName", obj.getString("UserName"));
		sendjson.put("channel", obj.getString("channel"));
		sendjson.put(SystemInfo.TAG_SYS_MSG, SystemInfo.getCancelLedReqMsg()); // 增加系統訊息
		WebSocketUserPool.sendMessageToUserWithTryCatch(sendto, sendjson.toString());
		WebSocketUserPool.sendMessageToUserWithTryCatch(aConn, sendjson.toString());
	}

	/** * get Agent Status */ // (不再使用)
//	public static void getUserStatus(String message,
//			org.java_websocket.WebSocket conn) {
//		JSONObject obj = new JSONObject(message);
//		String ACtype = obj.getString("ACtype");
//		StatusEnum statusEnum = WebSocketTypePool.getUserStatusByKeyinTYPE(ACtype, conn);
//		String reason = WebSocketTypePool.getUserReasonByKeyinTYPE(ACtype, conn);
//		JSONObject sendjson = new JSONObject();
//		sendjson.put("Event", "getUserStatus");
//		sendjson.put("from", obj.getString("id"));
//		sendjson.put("Status", statusEnum.getDbid());
//		sendjson.put("Reason", reason);
//		sendjson.put("channel", obj.getString("channel"));
//		WebSocketUserPool.sendMessageToUserWithTryCatch(conn, sendjson.toString());
//	}

	/** * create a roomId * @param message */
	public static void createRoomId(String message,
			org.java_websocket.WebSocket conn) {
		String roomId = java.util.UUID.randomUUID().toString();
		JSONObject sendjson = new JSONObject();
		sendjson.put("Event", "createroomId");
		sendjson.put("roomId", roomId);
		WebSocketUserPool.sendMessageToUserWithTryCatch(conn, sendjson.toString());
	}

	public static void refreshAgentList() {
		Util.getConsoleLogger().debug("refreshAgentList() called");
		Map<WebSocket, UserInfo> agentMap = WebSocketUserPool.getTypeconnections().get("Agent");
		Gson gson = new Gson();
		
		// 通知用
		Collection<UserInfo> agentUserInfoCollection = agentMap.values();
		JSONArray jsArray = new JSONArray(agentUserInfoCollection);
		Util.getConsoleLogger().debug("refreshAgentList() - jsArray: " + jsArray);
		for (WebSocket tmpConn : agentMap.keySet()) {
			if (tmpConn.isClosed() || tmpConn.isClosing()) continue;

			JSONObject sendjson02 = new JSONObject();
			sendjson02.put("Event", "refreshAgentList");
			sendjson02.put("agentList", jsArray);
			WebSocketUserPool.sendMessageToUserWithTryCatch(tmpConn, sendjson02.toString());
		}// end of for
	}
	
	//從DB撈取出AgentReason,並塞入Util.setAgentReason Bean，flag請塞"0"
	public static void GetAgentReasonInfo(String flag) {
		
		StringBuilder responseSB = null;

		String postData = "flag="+flag;

		try {
			// Connect to URL
			String hostURL = Util.getHostURLStr("RESTful");
			String projectName = Util.getProjectStr("RESTful");
			Util.getConsoleLogger().debug("hostURL: " + hostURL);
			URL url = new URL( hostURL + projectName + "/RESTful/Select_agentreason");
			Util.getConsoleLogger().debug("url: " + url.toString() );			

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
			String hostURL = Util.getHostURLStr("RESTful");
			String projectName = Util.getProjectStr("RESTful");
			Util.getConsoleLogger().debug("hostURL: " + hostURL);
			URL url = new URL( hostURL + projectName + "/RESTful/Insert_rpt_agentstatus");
			Util.getConsoleLogger().debug("url: " + url.toString());			
			
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
			String hostURL = Util.getHostURLStr("RESTful");
			String projectName = Util.getProjectStr("RESTful");
			Util.getConsoleLogger().debug("hostURL: " + hostURL);
			URL url = new URL( hostURL + projectName + "/RESTful/Update_rpt_agentstatus");
			Util.getConsoleLogger().debug("url: " + url.toString());			
			
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
				String hostURL = Util.getHostURLStr("RESTful");
				String projectName = Util.getProjectStr("RESTful");
				Util.getConsoleLogger().debug("hostURL: " + hostURL);
				URL url = new URL( hostURL + projectName + "/RESTful/Insert_rpt_activitylog");
				Util.getConsoleLogger().debug("url: " + url.toString());					

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
	
	
	
	static public void inviteAgentThirdParty(String message, org.java_websocket.WebSocket aConn){
		/** 讀出送進來的JSON物件 **/
		Util.getConsoleLogger().debug("inviteAgentThirdParty() called");
		Util.getConsoleLogger().debug("message: " + message);
		
		Gson gson = new Gson();
		ThirdPartyBean thirdPartyBeanIn = gson.fromJson(message, ThirdPartyBean.class);
		UserInfo invitingAgentUserInfo = WebSocketUserPool.getUserInfoByKey(aConn);
		org.java_websocket.WebSocket invitedAgentIDConn = WebSocketUserPool.getWebSocketByUserID(thirdPartyBeanIn.getInvitedAgentID());
		UserInfo invitedAgentUserInfo = WebSocketUserPool.getUserInfoByKey(invitedAgentIDConn);
		Util.getConsoleLogger().debug("inviteAgentThirdParty - userdata: " + thirdPartyBeanIn.getUserdata().toString());
		
		/** 籌備要寄出的JSON物件 **/
		thirdPartyBeanIn.setEvent("inviteAgentThirdParty");
		
		/** 寄給invitedAgent **/
		org.java_websocket.WebSocket invitedAgent_conn = WebSocketUserPool.getWebSocketByUserID(thirdPartyBeanIn.getInvitedAgentID());
		WebSocketUserPool.sendMessageToUserWithTryCatch(invitedAgent_conn, gson.toJson(thirdPartyBeanIn, ThirdPartyBean.class));
		
		/** 建立timeout機制 **/
		new RingCountDownConfTask(invitingAgentUserInfo, invitedAgentUserInfo, thirdPartyBeanIn).operate();
	}

	static public void responseThirdParty(String message, org.java_websocket.WebSocket aConn) {
		Util.getConsoleLogger().debug("responseThirdParty() called");
		Gson gson = new Gson();
		ThirdPartyBean thirdPartyBeanIn = gson.fromJson(message, ThirdPartyBean.class);
		org.java_websocket.WebSocket fromAgentConn = WebSocketUserPool.getWebSocketByUserID(thirdPartyBeanIn.getFromAgentID());
		String fromAgentName = WebSocketUserPool.getUserNameByKey(fromAgentConn);
		String invitedAgentName = WebSocketUserPool.getUserNameByKey(aConn);
		UserInfo invitedAgentUserInfo = WebSocketUserPool.getUserInfoByKey(aConn);
		
		/** 終止timeout機制 **/
		invitedAgentUserInfo.setStopConfRing(true);
		
		/** 根據response,建立回應事件 **/
		thirdPartyBeanIn.setEvent("responseThirdParty");
		// 為了讓SystemInfo.TAG_SYS_MSG field是動態的,這邊仍然得轉為JsonObject
		JsonObject jsonOut	= (JsonObject)gson.toJsonTree(thirdPartyBeanIn);
		if ("accept".equals(thirdPartyBeanIn.getResponse())){
			Util.getConsoleLogger().debug("responseThirdParty() - accept");
			/** 新增room成員 **/
			WebSocketRoomPool.addUserInRoom(thirdPartyBeanIn.getRoomID(), invitedAgentName, thirdPartyBeanIn.getInvitedAgentID(), aConn);
			/** 新增user所加入的room list **/
			WebSocketUserPool.addUserRoom(thirdPartyBeanIn.getRoomID(), aConn);
			
			// 通知更新roomList
			// 若是屬於轉接的要求,則將原Agent(邀請者)踢出
			if ("transfer".equals(thirdPartyBeanIn.getInviteType())){
				Util.getConsoleLogger().debug("responseThirdParty() - transfer");
				// 更新roomInfo - owner資訊
				RoomInfo roomInfo = WebSocketRoomPool.getRoomInfo(thirdPartyBeanIn.getRoomID());
				//roomInfo.setRoomOwnerAgentID(invitedAgentID);
				UserInfo clientUserInfo = WebSocketUserPool.getUserInfoByKey(roomInfo.getClientConn());
				clientUserInfo.setRoomOwner(thirdPartyBeanIn.getInvitedAgentID());
				
				jsonOut.addProperty(SystemInfo.TAG_SYS_MSG, SystemInfo.getLeftRoomMsg(fromAgentName) + "<br>" 
												+ SystemInfo.getJoinedRoomMsg(invitedAgentName)); // 傳出系統訊息
				
				// 通知要離開的使用者清除前端頁面
				WebSocketUserPool.sendMessageToUserWithTryCatch(
					WebSocketUserPool.getWebSocketByUserID(thirdPartyBeanIn.getFromAgentID()), 
					jsonOut.toString()
				);
				WebSocketRoomPool.removeUserinroom(thirdPartyBeanIn.getRoomID(), WebSocketUserPool.getWebSocketByUserID(thirdPartyBeanIn.getFromAgentID()));
			}else if("thirdParty".equals(thirdPartyBeanIn.getInviteType())){
				
				jsonOut.addProperty(SystemInfo.TAG_SYS_MSG, SystemInfo.getJoinedRoomMsg(invitedAgentName)); // 傳出系統訊息
			}
			// 通知剩下的各房間成員成員數改變了
			WebSocketRoomPool.sendMessageinroom(thirdPartyBeanIn.getRoomID(), jsonOut.toString());
			
		}else if("reject".equals(thirdPartyBeanIn.getResponse())){
			Util.getConsoleLogger().debug("responseThirdParty() - reject");
			// 通知邀請者
			WebSocketUserPool.sendMessageToUserWithTryCatch(WebSocketUserPool.getWebSocketByUserID(thirdPartyBeanIn.getFromAgentID()), jsonOut.toString());
		}
	
	}	
	
	
	static public void sendComment(String aMsg, org.java_websocket.WebSocket aConn) {
		Util.getConsoleLogger().debug("sendComment() called");
		JsonObject jsonIn = Util.getGJsonObject(aMsg);
		String interactionid = Util.getGString(jsonIn, "interactionid");
		String activitydataids = Util.getGString(jsonIn, "activitydataids");
		String comment = Util.getGString(jsonIn, "comment");
		UserInfo settingUserInfo = WebSocketUserPool.getUserInfoByKey(aConn);
		AgentFunction.RecordActivitylog(interactionid, activitydataids, comment);
		
		/*** Agent - 更新狀態 ***/
		Util.getStatusFileLogger().info("###### [sendComment()]");
		UpdateStatusBean usb = null;
		// AFTERCALLWORK狀態結束
		usb = new UpdateStatusBean();
		usb.setStatus(StatusEnum.AFTERCALLWORK.getDbid());
		usb.setDbid(settingUserInfo.getStatusDBIDMap().get(StatusEnum.AFTERCALLWORK));
		usb.setStartORend("end");
		CommonFunction.updateStatus(new Gson().toJson(usb), aConn);
	}
	
	
	public static void addRoomForMany(String aMsg, org.java_websocket.WebSocket aConn) {
		boolean stopNow = false;
		Util.getConsoleLogger().debug("addRoomForMany() called");
		JsonObject msgJson = Util.getGJsonObject(aMsg);
		String agentID = WebSocketUserPool.getUserID(aConn);
		UserInfo userInfo = WebSocketUserPool.getUserInfoByKey(aConn);
		List<String> userNameList = new ArrayList<>(); // 儲存房間成員,給系統訊息使用
		Util.getConsoleLogger().trace("addRoomForMany - msgJson: " + msgJson);
		// hardcoded - 之後想想看如何改善"none"這樣寫死的判斷方式
		String roomID = "";
		if ("none".equals(msgJson.get("roomID").getAsString())){
			roomID = java.util.UUID.randomUUID().toString(); // 在這邊直接建一個roomID,省略原本"createroomId"
		}else{
			roomID = msgJson.get("roomID").getAsString();
		}
		
		String channel = msgJson.get("channel").getAsString();
		JsonArray userIDJsonAry = msgJson.getAsJsonArray("memberListToJoin");
		Util.getConsoleLogger().trace("addRoomForMany() - userIDJsonAry: " + userIDJsonAry);
		String clientID = null;
		
		// 若此客戶已經被接通,則無法再被第二個Agent接起
		for (JsonElement userIDJsonE : userIDJsonAry){
			JsonObject userIDJsonObj = userIDJsonE.getAsJsonObject();
			String userID = userIDJsonObj.get("ID").getAsString();
			org.java_websocket.WebSocket userConn = WebSocketUserPool.getWebSocketByUserID(userID);
			if (WebSocketUserPool.isClient(userConn)){
				if (WebSocketUserPool.getUserRoomCount(userConn) > 0){
					Util.getConsoleLogger().debug("stopNow: " + stopNow);
					stopNow = true;
					break;
				}
			}
		}// end of for
		if (stopNow){
			for (JsonElement userIDJsonE : userIDJsonAry){
				JsonObject userIDJsonObj = userIDJsonE.getAsJsonObject();
				String userID = userIDJsonObj.get("ID").getAsString();
				org.java_websocket.WebSocket userConn = WebSocketUserPool.getWebSocketByUserID(userID);
				if (WebSocketUserPool.isAgent(userConn)){
					// RING狀態結束			
					userInfo.setRingEndExpected(true);
					userInfo.setStopRing(true);
					
					// READY狀態開始
					Util.getStatusFileLogger().info("###### [addRoomForMany-client_serverd]");
//					Util.getConsoleLogger().debug("userInfo.isNotReady(): " + userInfo.isNotReady());
					if (Util.getEstablishedStatus().equals(StatusEnum.READY.getDbid()) &&
							userInfo.isNotReady()){
						UpdateStatusBean usb = new UpdateStatusBean();
						usb = new UpdateStatusBean();
						usb.setStatus(StatusEnum.READY.getDbid());
						usb.setStartORend("start");
						CommonFunction.updateStatus(new Gson().toJson(usb), userConn);
					}
					
					// 寄出事件通知Agent
					JsonObject sendJson = new JsonObject();
					sendJson.addProperty("Event", "clientServerd");
					sendJson.addProperty("text",  "client already server!");
					WebSocketUserPool.sendMessageToUserWithTryCatch(userConn,sendJson.toString());					
				}//	 end of if	(WebSocketTypePool.isAgent(userConn))
			}// end of for (JsonElement userIDJsonE : userIDJsonAry)
			return; // 在此結束
		}// end of if (stopNow)
		
		/** 若Client沒有任何房間,才會往下跑 **/
		
		// 將成員一一加入到rooom中 (memberListToJoin為一個JsonArray)
		for (JsonElement userIDJsonE : userIDJsonAry){
			JsonObject userIDJsonObj = userIDJsonE.getAsJsonObject();
			String userID = userIDJsonObj.get("ID").getAsString();
			org.java_websocket.WebSocket userConn = WebSocketUserPool.getWebSocketByUserID(userID);
			
			// 處理於Client登入後 到 Agent按下AcceptEvent期間, 有人登出的狀況:
			if (userConn == null || userConn.isClosed() || userConn.isClosing()){
				String ACType = WebSocketUserPool.getACTypeByKey(userConn);
				Util.getConsoleLogger().warn("addRoomForMany() someone is disconnected");
				Util.getFileLogger().warn("addRoomForMany() someone is disconnected");
				if ("Client".equals(ACType)){
					Util.getConsoleLogger().warn("addRoomForMany() Client:" + userID +" is disconnected - addRoomFailed");
					Util.getFileLogger().warn("addRoomForMany() Client:" + userID +" is disconnected - addRoomFailed");
					return;
				}else if (userIDJsonAry.size() <= 2){
					Util.getConsoleLogger().warn("addRoomForMany() room member only 1 - addRoomFailed");
					Util.getFileLogger().warn("addRoomForMany() room member only 1 - addRoomFailed");
					return;
				}else{
					Util.getConsoleLogger().warn("addRoomForMany() skip one");
					Util.getFileLogger().warn("addRoomForMany() skip one");
					continue;
				}				
			}
			String username = WebSocketUserPool.getUserNameByKey(userConn);
			String joinMsg = "[Server]" + username + " join " + roomID + " room";
			userNameList.add(username);
			
			// 最後進行資料更新
			WebSocketRoomPool.addUserInRoom(roomID, username, userID, userConn); //重要步驟
			WebSocketUserPool.addUserRoom(roomID, userConn); //重要步驟
			WebSocketRoomPool.sendMessageinroom(roomID, joinMsg);
			WebSocketRoomPool.sendMessageinroom(roomID, "room people: "
					+ WebSocketRoomPool.getOnlineUserNameinroom(roomID).toString());
			
			/*** 更新狀態 ***/
			if (WebSocketUserPool.isAgent(userConn)){
				// RING狀態結束			
				userInfo.setRingEndExpected(true);
				userInfo.setStopRing(true);
				
				// IESTABLISHED狀態開始 
				Util.getStatusFileLogger().info("###### [addRoomForMany]");
				UpdateStatusBean usb = new UpdateStatusBean();
				usb.setStatus(StatusEnum.IESTABLISHED.getDbid());
				usb.setStartORend("start");
				usb.setRoomID(roomID);
				CommonFunction.updateStatus(new Gson().toJson(usb), userConn);
				
				
				// READY狀態開始
				Util.getStatusFileLogger().info("###### [addRoomForMany]");
//				Util.getConsoleLogger().debug("userInfo.isNotReady(): " + userInfo.isNotReady());
				if (Util.getEstablishedStatus().equals(StatusEnum.READY.getDbid()) &&
						userInfo.isNotReady()){
					usb = new UpdateStatusBean();
					usb.setStatus(StatusEnum.READY.getDbid());
					usb.setStartORend("start");
					CommonFunction.updateStatus(new Gson().toJson(usb), userConn);	
				}// end of if (需要更新狀態)
			}// end of 更新狀態
			
			/** 當下更新interaction,避免當Client網路斷掉後,interaction漏掉了IxnID欄位資料 **/
			if (WebSocketUserPool.isClient(userConn)){
				JsonObject ixnJson = Util.getGJsonObject(WebSocketUserPool.getUserInteractionByKey(userConn));
				ixnJson.addProperty("ixnid", roomID);
				WebSocketUserPool.addUserInteraction(ixnJson.toString(), userConn);
			}// end of if
			
			
		}// end of 將成員一一加入到rooom中
		
		
		// 更新roomInfo - owner資訊
		RoomInfo roomInfo = WebSocketRoomPool.getRoomInfo(roomID);
//		roomInfo.setRoomOwnerAgentID(agentID);
		UserInfo clientUserInfo = WebSocketUserPool.getUserInfoByKey(roomInfo.getClientConn());
		clientUserInfo.setRoomOwner(agentID);
		
		// 通知Client與Agent,要開啟layim(將原本AcceptEvent移到此處)
		for (JsonElement userIDJsonE : userIDJsonAry){
			JsonObject userIDJsonObj = userIDJsonE.getAsJsonObject();
			String userID = userIDJsonObj.get("ID").getAsString();
			org.java_websocket.WebSocket userConn = WebSocketUserPool.getWebSocketByUserID(userID);
			UserInfo currUserInfo = WebSocketUserPool.getUserInfoByKey(userConn);
			
			JsonArray roomIDListJson = new JsonArray();
			List<String> roomIDList = WebSocketUserPool.getUserRoomByKey(aConn);
			for (String tmpRoomID: roomIDList){
				roomIDListJson.add(tmpRoomID);
			}
			
			/*** 通知已處理完"AcceptEvent"事件 ***/
			JsonObject sendJson = new JsonObject();
			sendJson.addProperty("Event", "AcceptEvent");
			sendJson.addProperty("from",  WebSocketUserPool.getUserID(aConn));
			sendJson.addProperty("fromName", WebSocketUserPool.getUserNameByKey(aConn));
			sendJson.addProperty("roomID",  roomID);
			sendJson.addProperty("channel", channel);
			sendJson.addProperty("EstablishedStatus", Util.getEstablishedStatus()); //增加AfterCallStatus變數 20170222 Lin
			String userNameListStr = userNameList.toString();
			sendJson.addProperty(SystemInfo.TAG_SYS_MSG, SystemInfo.getJoinedRoomMsg(userNameListStr.substring(1,userNameListStr.length()-1))); // 增加系統訊息
			sendJson.add("roomList", roomIDListJson);
			WebSocketUserPool.sendMessageToUserWithTryCatch(userConn,sendJson.toString());	
		}// end of for
		
	}// end of addRoomForMany()
	
	
	/** * 更新ClientContactID */
	public static void updateClientContactID(String aMsg, org.java_websocket.WebSocket aConn) {
		JsonObject jsonIn = Util.getGJsonObject(aMsg);
		String contactID = Util.getGString(jsonIn, "contactID");
		String roomID = Util.getGString(jsonIn, "roomID");
		RoomInfo roomInfo = WebSocketRoomPool.getRoomInfo(roomID);
		org.java_websocket.WebSocket clientConn = roomInfo.getClientConn();
		
		UserInfo userInfo = WebSocketUserPool.getUserInfoByKey(clientConn);
		userInfo.setContactIDupdatedByAgent(true);
		
		String interaction = WebSocketUserPool.getUserInteractionByKey(clientConn);
		JsonObject interactionJsonMsg = Util.getGJsonObject(interaction);
		interactionJsonMsg.addProperty("contactid", contactID);
		Util.getConsoleLogger().debug("contactID: " + contactID);
		Util.getConsoleLogger().debug("roomID: " + roomID);
		WebSocketUserPool.addUserInteraction(interactionJsonMsg.toString(), clientConn);
		Util.getConsoleLogger().debug("WebSocketUserPool.getUserInteractionByKey(clientConn): " + WebSocketUserPool.getUserInteractionByKey(clientConn));
	}// end of updateClientContactID()
	
}
