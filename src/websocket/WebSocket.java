package websocket;

import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;




import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import filter.startFilter;
import util.Util;
import websocket.bean.RoomInfo;
import websocket.function.AgentFunction;
import websocket.function.ClientFunction;
import websocket.function.CommonFunction;
import websocket.pools.WebSocketRoomPool;
import websocket.pools.WebSocketTypePool;
import websocket.pools.WebSocketUserPool;
 
public class WebSocket extends WebSocketServer {
	
	public WebSocket(InetSocketAddress address) {
		super(address);
		Util.getConsoleLogger().info("WebSocket IP address initialized: " + address);
		Util.getFileLogger().info("WebSocket IP address initialized: " + address);
	}

	public WebSocket(int port) throws UnknownHostException {
		super(new InetSocketAddress(port));
		Util.getConsoleLogger().info("WebSocket Port initialized: " + port);
		Util.getFileLogger().info("WebSocket Port initialized: " + port);
		
//		Logger log = LogManager.getLogger(WebSocket.class);
//		log.printf(Level.INFO,"%s *********************************************%n",5);
	}

	/** * trigger close Event */
	@Override
	public void onClose(org.java_websocket.WebSocket conn, int message,
			String reason, boolean remote) {
		// 此方法沒有用到,先放著,並不會影響到主流程
		//userLeave(conn);
		Util.getConsoleLogger().info( WebSocketUserPool.getUserNameByKey(conn) + " is disconnected. (onClose)");
		Util.getFileLogger().info( WebSocketUserPool.getUserNameByKey(conn) + " is disconnected. (onClose)");
		// 將Heartbeat功能移轉到這裡:
		inputInteractionLog(conn,reason);
		clearUserData(conn); // 包含removeUser, removerUserinTYPE, removeUserinroom
	}


	/** * trigger Exception Event */
	@Override
	public void onError(org.java_websocket.WebSocket conn, Exception message) {
		Util.getConsoleLogger().warn("On Error: Socket Exception:" + message.toString());
		Util.getFileLogger().warn("On Error: Socket Exception:" + message.toString());
		
		message.printStackTrace();
		e++;
	}

	/** * trigger link Event */
	@Override
	public void onOpen(org.java_websocket.WebSocket conn,
			ClientHandshake handshake) {
		Util.getConsoleLogger().info( conn + " is connected");
		Util.getFileLogger().info( conn + " is connected");
		l++;
	}

	/** * The event is triggered when the client sends a message to the server */
	int j = 0;
	int h = 0;
	int e = 0;
	int l = 0;

	/**
	 * WebSocket GetMessage
	 * 
	 * message login online Exit addRoom leaveRoom messagetoRoom roomonline
	 * typeonline typein typeout AcceptEvent
	 * Event updateStatus getUserStatus findAgent createroomId senduserdata
	 */
	@Override
	public void onMessage(org.java_websocket.WebSocket conn, String message) {
		message = message.toString();
		Util.getConsoleLogger().trace("WebSocket :\n " +message);
		Util.getFileLogger().trace("WebSocket :\n " +message);
		JSONObject obj = new JSONObject(message);
		switch (obj.getString("type").trim()) {
		case "message":
			CommonFunction.getMessage(message.toString(), conn);
			break;
		case "login":
			CommonFunction.userjoin(message.toString(), conn);
			break;
		case "online":
			CommonFunction.online(message.toString(), conn);
			break;
		case "Exit":
			CommonFunction.userExit(message.toString(), conn);
			break;
//		case "addRoom":
//			CommonFunction.userjointoRoom(message.toString(), conn);
//			break;
		case "leaveRoom":
			CommonFunction.userExitfromRoom(message.toString(), conn);
			break;
		case "messagetoRoom":
			CommonFunction.getMessageinRoom(message.toString(), conn);
			break; 
		case "roomonline":
			CommonFunction.onlineinRoom(message.toString(), conn);
			break;
		case "typeonline":
			CommonFunction.onlineinTYPE(message.toString(), conn);
			break;
		case "typein":
			CommonFunction.userjointoTYPE(message.toString(), conn);
			break;
		case "typeout":
			CommonFunction.userExitfromTYPE(message.toString(), conn);
			break;
		case "AcceptEvent":
			AgentFunction.AcceptEvent(message.toString(), conn);
			break;
		case "RejectEvent":
			AgentFunction.RejectEvent(message.toString(), conn);
			break;
		case "findAgent":
			ClientFunction.findAgent(message.toString(), conn);
			break;
		case "findAgentEvent":
			ClientFunction.findAgentEvent(message.toString(), conn);
			break;
		case "updateStatus":
			CommonFunction.updateStatus(message.toString(), conn);
			break;
		case "getUserStatus":
			AgentFunction.getUserStatus(message.toString(), conn);
			break;
		case "createroomId":
			AgentFunction.createRoomId(message.toString(), conn);
			break;
		case "senduserdata":
			ClientFunction.senduserdata(message.toString(), conn);			
			break;
		case "entrylog":
			ClientFunction.entrylog(message.toString(), conn);
			break;
		case "interactionlog":
			ClientFunction.interactionlog(message.toString(), conn);
			break;
		case "setinteraction":
			ClientFunction.setinteraction(message.toString(), conn);
			break;
		case "inviteAgentThirdParty":
			inviteAgentThirdParty(message.toString(), conn);
			break;
		case "responseThirdParty":
			responseThirdParty(message.toString(), conn);			
			break;			
		case "refreshRoomList":
			CommonFunction.refreshRoomList(conn);
			break;
		case "addRoomForMany":
			addRoomForMany(message.toString(), conn);
			break;
		case "sendComment":
			sendComment(message.toString(), conn);
			break;
		case "test":
			this.test();
			break;
		}
	}

	/** * user leave websocket (Demo) */
	// 此方法沒有用到,先放著,並不會影響到主流程
	public void userLeave(org.java_websocket.WebSocket conn) {
		String user = WebSocketUserPool.getUserByKey(conn);
		boolean b = WebSocketUserPool.removeUser(conn);
		if (b) {
			WebSocketUserPool.sendMessage(user.toString());
			String joinMsg = "[Server]" + user + "Offline";
			WebSocketUserPool.sendMessage(joinMsg);
		}
	}
	
	private void clearUserData(org.java_websocket.WebSocket conn) {
		Util.getConsoleLogger().debug("clearUserData() called");
		// 清GROUP:
		// 取得一個user所屬的所有roomid
		List<String> roomids = WebSocketUserPool.getUserRoomByKey(conn);
		List<String> tmpRoomids = new ArrayList<String>(roomids); // 重要: 為了接下來要動態移除掉UserInfo.userRoom List, 為了在跑迴圈時仍保留著reference variable, 故須用light copy建立另一相對物件, 這樣就能實現動態刪除
		Util.getConsoleLogger().debug("before - roomids.size(): " + roomids.size());
		Iterator<String> itr = tmpRoomids.iterator();
		while(itr.hasNext()){
			String roomid = itr.next();
			Util.getConsoleLogger().debug("***** get roomid: " + roomid);
			//使用每個roomid,並找出相對應的room,再將其中的conn remove掉
			WebSocketRoomPool.removeUserinroom(roomid, conn);
		}
		Util.getConsoleLogger().debug("after - roomids.size(): " + roomids.size());

		// 清TYPE:
		String ACType = WebSocketUserPool.getACTypeByKey(conn);
		WebSocketTypePool.removeUserinTYPE(ACType, conn);
		/*** 更新Agent list - 私訊用 ***/
		if ("Agent".equals(ACType)){
			AgentFunction.refreshAgentList();
		}
		
		// 清USER:
		WebSocketUserPool.removeUser(conn);
	}
	
	private void inputInteractionLog(org.java_websocket.WebSocket conn, String reason) {
		Util.getConsoleLogger().debug("inputInteractionLog() called");
		
		// only Client stores userInteraction
		String message = WebSocketUserPool.getUserInteractionByKey(conn); // 一定取得到: 1. 在user login時就會呼叫setUserInteraction, 2. 在user logut or 重整時也會呼叫setUserInteraction
		String username = WebSocketUserPool.getUserNameByKey(conn);
		
		// 目前只有client端會再登入時、登出時、重整時寫入Log
		if (message != null){
			JSONObject obj = new JSONObject(message);
			String closefrom = obj.getString("closefrom");
			if (closefrom.equals("default")) {
				obj.put("status", 3);
				obj.put("stoppedreason", "server:HeartBeatLose"); // 看之後是否考慮更改為變數reason
				obj.put("closefrom", "server:HeartBeatLose"); 
			}
			ClientFunction.interactionlog(obj.toString(), conn);			
		}

	}
	
	private void inviteAgentThirdParty(String message, org.java_websocket.WebSocket conn){
		// 讀出送進來的JSON物件
		Util.getConsoleLogger().debug("inviteAgentThirdParty() called");
		JSONObject obj = new JSONObject(message);
		String ACtype = obj.getString("ACtype");
		String roomID = obj.getString("roomID");
		String fromAgentID = obj.getString("fromAgentID");
		String invitedAgentID = obj.getString("invitedAgentID");
		String fromAgentName = obj.getString("fromAgentName");
		String inviteType = obj.getString("inviteType");
		String userdata = obj.getJSONObject("userdata").toString();
		String text = obj.getString("text");
		Util.getConsoleLogger().trace("inviteAgentThirdParty - userdata: " + userdata);
				
		//籌備要寄出的JSON物件
		obj.put("Event", "inviteAgentThirdParty");
		
		// 寄給invitedAgent:
		org.java_websocket.WebSocket invitedAgent_conn = WebSocketUserPool.getWebSocketByUser(invitedAgentID);
		WebSocketUserPool.sendMessageToUser(invitedAgent_conn, obj.toString());
		
//		type : "inviteAgentThirdParty",
//		ACtype : "Agent",
//		roomID : RoomID, //先預設目前每個Agent最多也就只有一個RoomID,之後會再調整
//		fromAgentID : UserID,
//		invitedAgentID : myInvitedAgentID,
//		fromAgentName : UserName
	}

	private void responseThirdParty(String message, org.java_websocket.WebSocket aConn) {
		Util.getConsoleLogger().debug("responseThirdParty() called");
		JSONObject obj = new JSONObject(message);
		String ACtype = obj.getString("ACtype");
		String roomID = obj.getString("roomID");
		String fromAgentID = obj.getString("fromAgentID");
		String invitedAgentID = obj.getString("invitedAgentID");
		String invitedAgentName = WebSocketUserPool.getUserNameByKey(aConn);
		String response = obj.getString("response");
		String inviteType = obj.getString("inviteType");
//		String userdata = obj.getString("userdata");
		JSONObject userdata = obj.getJSONObject("userdata");
		String text = obj.getString("text");
		
		obj.put("Event", "responseThirdParty");		
		if ("accept".equals(response)){
			Util.getConsoleLogger().debug("responseThirdParty() - accept");
			/** 新增room成員 **/
			WebSocketRoomPool.addUserInRoom(roomID, invitedAgentName, invitedAgentID, aConn);
			/** 新增user所加入的room list **/
			WebSocketUserPool.addUserRoom(roomID, aConn);
			
			// 通知更新roomList
			// 若是屬於轉接的要求,則將原Agent(邀請者)踢出
			if ("transfer".equals(inviteType)){
				Util.getConsoleLogger().debug("responseThirdParty() - transfer");
				// 通知使用者清除前端頁面
				WebSocketUserPool.sendMessageToUser(WebSocketUserPool.getWebSocketByUser(fromAgentID), obj.toString());
				WebSocketRoomPool.removeUserinroom(roomID, WebSocketUserPool.getWebSocketByUser(fromAgentID));
			}
			// 通知各房間成員成員數改變了
			WebSocketRoomPool.sendMessageinroom(roomID, obj.toString());
			
		}else if("reject".equals(response)){
			Util.getConsoleLogger().debug("responseThirdParty() - reject");			
			WebSocketUserPool.sendMessageToUser(WebSocketUserPool.getWebSocketByUser(fromAgentID), obj.toString());
		}
	
	}
	
	private void addRoomForMany(String aMsg, org.java_websocket.WebSocket aConn) {
		Util.getConsoleLogger().debug("addRoomForMany() called");
		JsonObject msgJson = Util.getGJsonObject(aMsg);
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
		
		for (JsonElement userIDJsonE : userIDJsonAry){
			JsonObject userIDJsonObj = userIDJsonE.getAsJsonObject();
			String userID = userIDJsonObj.get("ID").getAsString();
			org.java_websocket.WebSocket userConn = WebSocketUserPool.getWebSocketByUser(userID);
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
			
			WebSocketRoomPool.addUserInRoom(roomID, username, userID, userConn); //重要步驟
			WebSocketUserPool.addUserRoom(roomID, userConn); //重要步驟
			WebSocketRoomPool.sendMessageinroom(roomID, joinMsg);
			WebSocketRoomPool.sendMessageinroom(roomID, "room people: "
					+ WebSocketRoomPool.getOnlineUserNameinroom(roomID).toString());
			
			// 更新room list
//			String ACtype = WebSocketUserPool.getACTypeByKey(userConn);
//			if ("Agent".equals(ACtype)){
//				Util.getConsoleLogger().debug("userjointoroom - one Agent joined");
//				CommonFunction.refreshRoomList(userConn);
//			}else if ("Client".equals(ACtype)){
//				clientID = userID;
//			}
		}
		
		// 通知Client與Agent,要開啟layim(將原本AcceptEvent移到此處)
		for (JsonElement userIDJsonE : userIDJsonAry){
			JsonObject userIDJsonObj = userIDJsonE.getAsJsonObject();
			String userID = userIDJsonObj.get("ID").getAsString();
			org.java_websocket.WebSocket userConn = WebSocketUserPool.getWebSocketByUser(userID);
			
			JsonArray roomIDListJson = new JsonArray();
			List<String> roomIDList = WebSocketUserPool.getUserRoomByKey(aConn);
			for (String tmpRoomID: roomIDList){
				roomIDListJson.add(tmpRoomID);
			}
			
			JsonObject sendJson = new JsonObject();
			sendJson.addProperty("Event", "AcceptEvent");
			sendJson.addProperty("from",  WebSocketUserPool.getUserID(aConn));
			sendJson.addProperty("fromName", WebSocketUserPool.getUserNameByKey(aConn));
			sendJson.addProperty("roomID",  roomID);
			sendJson.addProperty("channel", channel);
			sendJson.addProperty("EstablishedStatus", Util.getEstablishedStatus()); //增加AfterCallStatus變數 20170222 Lin
			sendJson.add("roomList", roomIDListJson);

			WebSocketUserPool.sendMessageToUser(
					userConn,sendJson.toString());	
		}
	}
	
	private void sendComment(String aMsg, org.java_websocket.WebSocket aConn) {
		Util.getConsoleLogger().debug("sendComment() called");
		JsonObject jsonIn = Util.getGJsonObject(aMsg);
		String interactionid = Util.getGString(jsonIn, "interactionid");
		String activitydataids = Util.getGString(jsonIn, "activitydataids");
		String comment = Util.getGString(jsonIn, "comment"); 
		AgentFunction.RecordActivitylog(interactionid, activitydataids, comment);
	}

	
	
	private void test() {
		Util.getConsoleLogger().debug("test() called");

	/*********** log4j 1測試 **************/
//	Logger logger = Logger.getLogger(WebSocket.class);
//
//	// 對應的 Log4j.properties 設定要在等級 Info 之上才會顯示，所以logger.debug 不會出現
//	logger.debug("Hello Log4j, this is debug message");
//
//	// 以下的訊息會出現在 console 和 log file 中
//	logger.info("Hi Log4j, this will appear in console and log file");
//	logger.error("This is error message!!!");
	
	}
	
}


