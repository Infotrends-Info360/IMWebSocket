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

import org.java_websocket.WebSocket;

import util.StatusEnum;
import util.Util;
import websocket.bean.UserInfo;

//此類別給AgentFunction.java共同使用
//此類別給ClientFunction.java共同使用
//此類別給CommonFunction.java共同使用

//此類別給HeartBeat.java使用
//此類別給GetKPIServlet.java使用
public class WebSocketTypePool{
	/**********************************************************************************/
	/**
	 * Agent & Client Members Map
	 */
	// TYPEconnections第一層Map: 用來區分type是Agent or Client
	// TYPEconnections第二層Map: 用來區分每個WebSocket所對應到的Bean
	private static final Map<String, Map<WebSocket, UserInfo>> TYPEconnections = new HashMap<>();

	/** * Add User to Agent or Client * @param inbound */
	public static void addUserinTYPE(String aTYPE,String aUsername, String aUserID,String aDate, WebSocket aConn) {
		System.out.println("addUserinTYPE() called");
		Map<WebSocket, UserInfo> TYPEMap = TYPEconnections.get(aTYPE);
		if (TYPEMap == null || TYPEMap.isEmpty()){
			TYPEMap = new HashMap<>();
		}
		
		// 拿取對應的userInfo,並對其資料做更新
		UserInfo userInfo = WebSocketUserPool.getUserallconnections().get(aConn);
		if(aTYPE.equals("Agent")){
			userInfo.setStatusEnum(StatusEnum.NOTREADY);
		}else if(aTYPE.equals("Client")){
//			userInfo.setStatusEnum("wait"); // 應該用不到吧,觀察一下
		}
//		userInfo.setReadyTime(aDate);
		// 放入Map
		TYPEMap.put(aConn, userInfo);
		TYPEconnections.put(aTYPE, TYPEMap);
	}
	
	/** * Agent or Client User Information Update */
	public static void UserUpdate(String aTYPE,String aUsername, String aUserid,String aDate,StatusEnum aStatusEnum,String aReason, WebSocket aConn) {
		Map<WebSocket, UserInfo> TYPEmap = TYPEconnections.get(aTYPE);
		UserInfo userInfo = TYPEmap.get(aConn);
		userInfo.setStatusEnum(aStatusEnum);
		userInfo.setReason(aReason);
//		userInfo.setReadyTime(aDate); // 更新時間?離開時間?->登入時間是否會被覆蓋掉 ?還是這是專給Agent用的,算等待時間的?
		TYPEmap.put(aConn, userInfo);
		TYPEconnections.put(aTYPE, TYPEmap);
	}
	
	/** * Remove User from Agent or Client* @param inbound */
	public static void removeUserinTYPE(String aTYPE,WebSocket aConn) {
		System.out.println("removeUserinTYPE() called");
		Map<WebSocket,  UserInfo> TYPEmap = TYPEconnections.get(aTYPE);
		if (TYPEmap == null){
			System.out.println("注意: " + " can not find TYPEmap for " + aTYPE);
			return;
		}
		
//		System.out.println("TYPE: " + aTYPE);
//		System.out.println("TYPEmap.size(): " + TYPEmap.size());
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
	synchronized public static String getOnlineLongestUserinTYPE(String aTYPE) {
		System.out.println("getOnlineLongestUserinTYPE() called");
		Map<WebSocket,  UserInfo> TYPEmap = TYPEconnections.get(aTYPE);
		if (TYPEmap == null || TYPEmap.isEmpty()){ 
			return null;
		}
		//List<String> setUsers = new ArrayList<String>();
		String settingUser = null;
		UserInfo settingUserInfo = null;
		Date date = new Date();
		long UserStayTime = date.getTime();
		Collection<UserInfo> setUser = TYPEmap.values();
//		System.out.println("setUser.size()" + setUser.size());
		for (UserInfo userInfo : setUser) {
			if (!userInfo.getStatusEnum().equals(StatusEnum.READY)) {
				continue;
			}
			//setUsers.add(u.get("userid").toString());
			SimpleDateFormat sdf = new SimpleDateFormat(Util.getSdfTimeFormat());
			String userdatestring = userInfo.getReadyTime(); // 關鍵是這行
			if (userdatestring == null) continue;
//			System.out.println("userdatestring: " + userdatestring);
			Date userdate = null;
			try { 
				userdate = sdf.parse(userdatestring);
			} catch (ParseException e) {
				e.printStackTrace();
			} catch (Exception e){
				e.printStackTrace();
			}
//			System.out.println("Agent Status - userInfo.getStatus(): " + userInfo.getStatus());
//			System.out.println("Agent Status - StatusEnum.READY.getValue(): " + StatusEnum.READY.getDbid());
			if(userdate.getTime() <= UserStayTime && userInfo.getStatusEnum().equals(StatusEnum.READY)){
				UserStayTime = userdate.getTime(); // 每次都會將UserStayTime拿去當作"上一個"Uset的等待時間
				settingUser = userInfo.getUserid();
				settingUserInfo = userInfo;
			}
		}
//		return settingUser;
		// 若沒有任何Agent處於READY狀態,則回傳null
		if (settingUserInfo == null) return null;
		settingUserInfo.setStatusEnum(StatusEnum.NOTREADY); // 直接改了,避免一個以上Client找到同一個Agent
		return settingUserInfo.getUserid();
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
	
	
}
