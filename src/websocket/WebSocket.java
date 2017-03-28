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






import java.util.concurrent.BlockingQueue;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import org.java_websocket.WebSocket;
import org.java_websocket.WebSocketImpl;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;
import org.json.JSONArray;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import filter.SystemListener;
import filter.startFilter;
import util.StatusEnum;
import util.Util;
import websocket.bean.RoomInfo;
import websocket.bean.SystemInfo;
import websocket.bean.UpdateStatusBean;
import websocket.bean.UserInfo;
import websocket.function.AgentFunction;
import websocket.function.ClientFunction;
import websocket.function.CommonFunction;
import websocket.pools.WebSocketRoomPool;
import websocket.pools.WebSocketTypePool;
import websocket.pools.WebSocketUserPool;
import websocket.thread.findAgent.FindAgentCallable;
import websocket.thread.findAgent.FindAgentThread;

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
		
		//啟動FindAgentThread
		Thread findAgentThread = new FindAgentThread();
		findAgentThread.start();
		Util.getFileLogger().info("findAgentThread started");
		
	}

	/** * trigger close Event */
	@Override
	public void onClose(org.java_websocket.WebSocket conn, int message,
			String reason, boolean remote) {
		CommonFunction.onCloseHelper(conn, reason);
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
	public void onMessage(final org.java_websocket.WebSocket conn, final String message) {
		Util.getConsoleLogger().trace("WebSocket :\n " +message);
		Util.getFileLogger().trace("WebSocket :\n " +message);
		JSONObject obj = new JSONObject(message);
		// if fromSrc = "WeChat";
		// List<MsgWrapper> msgWrapperList // 拿取各方法回傳物件(包含 傳給誰+回傳資訊)
		switch (obj.getString("type").trim()) {
		case "message":
			CommonFunction.getMessage(message.toString(), conn);
			// we will get MSG + sendToID			
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
			//確保client進線找Agent的順序性 ,將requesy物件化後放入queue中處理
			FindAgentCallable task = new FindAgentCallable(message,conn);
			BlockingQueue<FindAgentCallable> queue = WebSocketUserPool.getClientfindagentqueue();
			Util.getFileLogger().info("findAgent - add task to queue - " + "before queue size: " + queue.size());
			// 將請求放入queue
			if(queue.offer(task)){
				Util.getFileLogger().info("findAgent - add task to queue - succeed - " + " queue after size: " + queue.size());
			}else{;
				Util.getFileLogger().info("findAgent - add task to queue - failed - " + " queue after size: " + queue.size());			
			}
			// 將此請求放入Client UserInfo中,onClose()清理時使用
			UserInfo userInfo = WebSocketUserPool.getUserInfoByKey(conn);
			userInfo.setFindAgentCallable(task);
			
			break;
		case "findAgentEvent":
			ClientFunction.findAgentEvent(message.toString(), conn);
			break;
		case "updateStatus":
			JsonObject tmpObj = Util.getGJsonObject(message);
			String status_dbid = Util.getGString(tmpObj, "status");
			if (StatusEnum.READY.getDbid().equals(status_dbid))
				Util.getStatusFileLogger().info("###### [User request] ######");
			if (StatusEnum.NOTREADY.getDbid().equals(status_dbid))
				Util.getStatusFileLogger().info("###### [User request] ######");
			
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
		case "updateClientContactID":
			updateClientContactID(message.toString(), conn);
			break;
		case "test":
			this.test();
			break;
		}
		
		// if fromSrc.equals("WeChat") -> use WeChatAPI to send msg
		// if fromSrc.equals("js") -> use the same old way 

		
	}

	/** * 更新ClientContactID */
	private void updateClientContactID(String aMsg, org.java_websocket.WebSocket aConn) {
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
		org.java_websocket.WebSocket invitedAgent_conn = WebSocketUserPool.getWebSocketByUserID(invitedAgentID);
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
		org.java_websocket.WebSocket fromAgentConn = WebSocketUserPool.getWebSocketByUserID(fromAgentID);
		String fromAgentName = WebSocketUserPool.getUserNameByKey(fromAgentConn);
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
				// 更新roomInfo - owner資訊
				RoomInfo roomInfo = WebSocketRoomPool.getRoomInfo(roomID);
				//roomInfo.setRoomOwnerAgentID(invitedAgentID);
				UserInfo clientUserInfo = WebSocketUserPool.getUserInfoByKey(roomInfo.getClientConn());
				clientUserInfo.setRoomOwner(invitedAgentID);
				obj.put(SystemInfo.TAG_SYS_MSG, SystemInfo.getLeftRoomMsg(fromAgentName) + "<br>" 
												+ SystemInfo.getJoinedRoomMsg(invitedAgentName)); // 傳出系統訊息
				
				// 通知要離開的使用者清除前端頁面
				WebSocketUserPool.sendMessageToUser(WebSocketUserPool.getWebSocketByUserID(fromAgentID), obj.toString());
				WebSocketRoomPool.removeUserinroom(roomID, WebSocketUserPool.getWebSocketByUserID(fromAgentID));
			}else if("thirdParty".equals(inviteType)){
				obj.put(SystemInfo.TAG_SYS_MSG, SystemInfo.getJoinedRoomMsg(invitedAgentName)); // 傳出系統訊息
			}
			// 通知剩下的各房間成員成員數改變了
			WebSocketRoomPool.sendMessageinroom(roomID, obj.toString());
			
		}else if("reject".equals(response)){
			Util.getConsoleLogger().debug("responseThirdParty() - reject");			
			WebSocketUserPool.sendMessageToUser(WebSocketUserPool.getWebSocketByUserID(fromAgentID), obj.toString());
		}
	
	}
	
	private void addRoomForMany(String aMsg, org.java_websocket.WebSocket aConn) {
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
			if (WebSocketTypePool.isAgent(userConn)){
				// RING狀態結束			
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
			WebSocketUserPool.sendMessageToUser(userConn,sendJson.toString());	
		}
		
	}
	
	private void sendComment(String aMsg, org.java_websocket.WebSocket aConn) {
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

	private void processReqQueue(){
//		BlockingQueue<Callable<Object>> queue = WebSocketUserPool.getClientfindagentqueue();
//		queue.offer(task);
//		while(!queue.isEmpty()){
//			Callable<Object> currTask;
//			try {
//				currTask = queue.take();
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


