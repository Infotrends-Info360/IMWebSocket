package websocket.pools;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.java_websocket.WebSocket;

import com.google.gson.Gson;

import util.StatusEnum;
import util.Util;
import websocket.bean.UpdateStatusBean;
import websocket.bean.UserInfo;
import websocket.function.CommonFunction;

//此類別給AgentFunction.java共同使用
//此類別給ClientFunction.java共同使用
//此類別給CommonFunction.java共同使用

//此類別給HeartBeat.java使用
//此類別給GetKPIServlet.java使用
public class WebSocketTypePool{
	public static final String AGENT = "Agent";
	public static final String CLIENT = "Client";
	
	/**********************************************************************************/
	/**
	 * Agent & Client Members Map
	 */
	// TYPEconnections第一層Map: 用來區分type是Agent or Client
	// TYPEconnections第二層Map: 用來區分每個WebSocket所對應到的Bean
	private static final Map<String, Map<WebSocket, UserInfo>> TYPEconnections = new HashMap<>();

	/** * Add User to Agent or Client * @param inbound */
	public static void addUserinTYPE(String aTYPE,String aUsername, String aUserID,String aDate, WebSocket aConn) {
		Util.getConsoleLogger().debug("addUserinTYPE() called");
		Map<WebSocket, UserInfo> TYPEMap = TYPEconnections.get(aTYPE);
		if (TYPEMap == null || TYPEMap.isEmpty()){
			TYPEMap = new HashMap<>();
		}
		
		// 拿取對應的userInfo,並對其資料做更新
		UserInfo userInfo = WebSocketUserPool.getUserallconnections().get(aConn);
//		if(aTYPE.equals("Agent")){
//			userInfo.setStatusEnum(StatusEnum.NOTREADY);
//		}else if(aTYPE.equals("Client")){
////			userInfo.setStatusEnum("wait"); // 應該用不到吧,觀察一下
//		}
//		userInfo.setReadyTime(aDate);
		// 放入Map
		TYPEMap.put(aConn, userInfo);
		TYPEconnections.put(aTYPE, TYPEMap);
	}
	
	/** * Agent or Client User Information Update */ //(不再使用)
//	public static void UserUpdate(String aTYPE,String aUsername, String aUserid,String aDate,StatusEnum aStatusEnum,String aReason, WebSocket aConn) {
//		Util.getConsoleLogger().debug("UserUpdate() called");
//		Map<WebSocket, UserInfo> TYPEmap = TYPEconnections.get(aTYPE);
//		UserInfo userInfo = TYPEmap.get(aConn);
//		userInfo.setStatusEnum(aStatusEnum);
//		userInfo.setReason(aReason);
////		userInfo.setReadyTime(aDate); // 更新時間?離開時間?->登入時間是否會被覆蓋掉 ?還是這是專給Agent用的,算等待時間的?
//		TYPEmap.put(aConn, userInfo);
//		TYPEconnections.put(aTYPE, TYPEmap);
//	}
	
	/** * Remove User from Agent or Client* @param inbound */
	public static void removeUserinTYPE(String aTYPE,WebSocket aConn) {
		Util.getConsoleLogger().debug("removeUserinTYPE() called");
		Map<WebSocket,  UserInfo> TYPEmap = TYPEconnections.get(aTYPE);
		if (TYPEmap == null){
			Util.getConsoleLogger().warn("注意: " + " can not find TYPEmap for " + aTYPE);
			Util.getFileLogger().warn("注意: " + " can not find TYPEmap for " + aTYPE);
			return;
		}
		
		if (TYPEmap != null && TYPEmap.containsKey(aConn)) {
			TYPEmap.remove(aConn);
		}
	}
	
	/** * Send Message to all of User in Agent or Client * @param message */
	public static void sendMessageinTYPE(String aTYPE,String aMessage) {
		Map<WebSocket,  UserInfo> TYPEmap = TYPEconnections.get(aTYPE);
		Set<WebSocket> connsInType = TYPEmap.keySet();
		synchronized (connsInType) {
			for (WebSocket conn : connsInType) {
				/*
				String user = TYPEmap.get(conn).get("userid").toString();
				if (user != null) {
					conn.send(message);
				}
				*/
				UserInfo userInfo = TYPEmap.get(conn);
				if (userInfo != null) {
					conn.send(aMessage);
				}
			}
		}
	}
	
	/** * Get Online Longest User(Agent) * @return */
	public static String getOnlineLongestUserinTYPE(WebSocket aConn, String aTYPE) {
//		Util.getConsoleLogger().debug("getOnlineLongestUserinTYPE() called");
		String poppedAgentID = null;
		UserInfo settingUserInfo = null;
		try {
//				String poppedAgentID = WebSocketUserPool.getReadyAgentQueue().poll();
			poppedAgentID = WebSocketUserPool.getReadyAgentQueue().take();// this will block the current Thread if the queue is empty
		} catch (InterruptedException e) {
			// if client logout or disconnect, this task in the current thread will be terminated
			Util.getFileLogger().error("[Exception] Client " + WebSocketUserPool.getUserNameByKey(aConn) + " cancelled findAgent task");
			Util.getConsoleLogger().error("[Exception] Client " + WebSocketUserPool.getUserNameByKey(aConn) + " cancelled findAgent task");
//			e.printStackTrace();
		} 
		Util.getConsoleLogger().debug("poppedAgentID: " + poppedAgentID);
		
		if (poppedAgentID == null) return poppedAgentID;
		
		// 拿取相對應UserInfo資訊
		WebSocket poppedAgentConn = WebSocketUserPool.getWebSocketByUserID(poppedAgentID);
		settingUserInfo = WebSocketUserPool.getUserInfoByKey(poppedAgentConn);
		Util.getConsoleLogger().debug("agentUserInfo.getUsername(): " + settingUserInfo.getUsername() + " popped out");
		Util.getConsoleLogger().debug("WebSocketUserPool.getReadyAgentQueue().size(): " + WebSocketUserPool.getReadyAgentQueue().size());			
		
		// 開始更新狀態: 
//		settingUserInfo.setStatusEnum(StatusEnum.NOTREADY); // 此方法已經不再有影響 // 直接改了,避免一個以上Client找到同一個Agent
		Gson gson = new Gson();
		WebSocket agentConn = WebSocketUserPool.getWebSocketByUserID(settingUserInfo.getUserid());
		// NOTREADY狀態開始
		Util.getStatusFileLogger().info("###### [findAgent]");
		UpdateStatusBean usb = new UpdateStatusBean();
		usb.setStatus(StatusEnum.NOTREADY.getDbid());
		usb.setStartORend("start");
		CommonFunction.updateStatus(gson.toJson(usb), agentConn);				

//		ExecutorService service = Executors.newCachedThreadPool();
//		Future<String> taskResult = service.submit(task);
//		while(!taskResult.isDone()){
//			// let current thread do something else
////			Util.getConsoleLogger().debug("taskResult is not done yet");
//		}
//		Util.getConsoleLogger().debug("taskResult.getClass(): " + taskResult.getClass() + " is Done");
//		
//		try {
//			poppedAgentID = taskResult.get(); // blocks if the result haven't come out
//		} catch (InterruptedException | ExecutionException e) {
//			e.printStackTrace();
//		}

		return poppedAgentID;
	}
	
	/** * Get Online User Name in Agent or Client * @return */
	public static Collection<String> getOnlineUserNameinTYPE(String aTYPE) {
		Map<WebSocket, UserInfo> TYPEmap = TYPEconnections.get(aTYPE);
		List<String> setUsers = new ArrayList<String>();
		Collection<UserInfo> setUser = TYPEmap.values();
		for (UserInfo u : setUser) {
			setUsers.add(u.getUsername());
		}
		return setUsers;
	}
	
	/** * Get Online User ID in Agent or Client * @return */
	public static Collection<String> getOnlineUserIDinTYPE(String aTYPE) {
		Map<WebSocket,  UserInfo> TYPEmap = TYPEconnections.get(aTYPE);
		List<String> setUsers = new ArrayList<String>();
		if (TYPEmap != null && !TYPEmap.isEmpty()){
			Collection<UserInfo> setUser = TYPEmap.values();
			for (UserInfo u : setUser) {
				setUsers.add(u.getUserid());
			}			
		}
		return setUsers;
	}
	
	/** * Get Online count in Agent or Client * @return */
	public static int getOnlineUserIDinTYPECount(String aTYPE) {
		if (TYPEconnections.get(aTYPE) == null){
			return 0;
		}
		return TYPEconnections.get(aTYPE).size();
	}
	
	/** * Get User Status in Agent or Client * ready * not ready * established * party remove * @param session */
	public static StatusEnum getUserStatusByKeyinTYPE(String aTYPE, WebSocket aConn) {
		Map<WebSocket,  UserInfo> TYPEmap = TYPEconnections.get(aTYPE);
		return TYPEmap.get(aConn).getStatusEnum();
	}
	
	/** * Get User Status in Agent or Client * ready * not ready * established * party remove * @param session */
	public static String getUserReasonByKeyinTYPE(String aTYPE, WebSocket aConn) {
		Map<WebSocket,  UserInfo> TYPEmap = TYPEconnections.get(aTYPE);
		return TYPEmap.get(aConn).getReason();
	}
	
	/** * Get User ID By Key in Agent or Client * @param session */
	public static String getUserByKeyinTYPE(String aTYPE, WebSocket aConn) {
		Map<WebSocket,  UserInfo> TYPEmap = TYPEconnections.get(aTYPE);
		return TYPEmap.get(aConn).getUserid();
	}

	/** * Get Agent or Client Count * @param */
	public static int getUserTYPECount(String aTYPE) {
		return TYPEconnections.size();
	}

	/** * Get WebSocket By User ID in Agent or Client * @param user */
	public static WebSocket getWebSocketByUserinTYPE(String aTYPE,String aUser) {
		Map<WebSocket,  UserInfo> TYPEmap = TYPEconnections.get(aTYPE);
		Set<WebSocket> keySet = TYPEmap.keySet();
		synchronized (keySet) {
			for (WebSocket conn : keySet) {
				String cuser = TYPEmap.get(conn).getUserid();
				if (cuser.equals(aUser)) {
					return conn;
				}
			}
		}
		return null;
	}
	
	private static int leaveClient = 0;
	
	/** * Add a count to leaveClient* @param inbound */
	public static void addleaveClient() {
		leaveClient += 1;
	}
	
	/** * clean leaveClient* @param inbound */
	public static void cleanleaveClient() {
		leaveClient = 0;
	}
	
	/** * Get leaveClient * @param */
	public static int getleaveClient() {
		return leaveClient;
	}

	public static Map<String, Map<WebSocket, UserInfo>> getTypeconnections() {
		return TYPEconnections;
	}
	
	public static boolean isAgent(WebSocket conn){
		Map<WebSocket, UserInfo> TYPEmap = TYPEconnections.get("Agent");
		if (TYPEmap != null && TYPEmap.containsKey(conn)){
			return true;
		}
		return false;
	}
	
	public static boolean isClient(WebSocket conn){
		Map<WebSocket, UserInfo> TYPEmap = TYPEconnections.get("Client");
		if (TYPEmap != null && TYPEmap.containsKey(conn)){
			return true;
		}
		return false;
	}	
	
//	public static String getUserType(WebSocket conn){
//		if (isAgent(conn)){
//			return "Agent";
//		}else if (isClient(conn)){
//			return "Client";
//		}
//		return null;
//	}
	
}
