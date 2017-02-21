package websocket.pools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.java_websocket.WebSocket;
import org.json.JSONObject;

import com.google.gson.JsonObject;

import util.Util;
import websocket.bean.RoomInfo;
import websocket.bean.UserInfo;

//此類別給AgentFunction.java共同使用
//此類別給ClientFunction.java共同使用
//此類別給CommonFunction.java共同使用

//此類別給HeartBeat.java使用
//此類別給GetKPIServlet.java使用
public class WebSocketRoomPool{
	/**********************************************************************************/
	/**
	 * Room Members Map
	 */
//	private static final Map<String, Map<WebSocket, RoomInfo>> roomuserconnections = new HashMap<String, Map<WebSocket, RoomInfo>>();
	private static final Map<String, RoomInfo> roomMap = new HashMap<>();
	
	
	/** * Add User to Room Map * @param inbound */
	public static void addUserInRoom(String aRoomID,String username,String userid, WebSocket conn) {
		
		// 找是否已有roomInfo(表示已經有人先加入此room了)
		RoomInfo roomInfo = roomMap.get(aRoomID);	
		if (roomInfo == null){
			//建立實體
			roomInfo = new RoomInfo();					
			//拿ID
			roomInfo.setRoomID(aRoomID);
			//拿room建立時間
			Date starttime = new Date();
			roomInfo.setStarttime(starttime);
			//拿ClientConn
			if (WebSocketTypePool.isClient(conn)){
				roomInfo.setClientConn(conn);
			}
		}
				
		//拿UserInfo:
		UserInfo userInfo = WebSocketUserPool.getUserallconnections().get(conn);
		roomInfo.getUserConns().put(conn, userInfo);		
		
		//將roomInfo放入roomMap中
		roomMap.put(aRoomID, roomInfo);
		
		//告知room成員已變動,請瀏覽器對前端頁面做更新
		JsonObject sendJson = new JsonObject();
		sendJson.addProperty("Event", "addUserInRoom");		
		sendJson.addProperty("roomMembers", getOnlineUserNameinroom(aRoomID).toString());
		sendJson.addProperty("roomMemberIDs", getOnlineUserIDinroom(aRoomID).toString());
		sendJson.addProperty("roomSize", roomInfo.getUserConns().size());
		WebSocketRoomPool.sendMessageinroom(aRoomID, sendJson.toString());
		
//		System.out.println("room: " + room + " now has " + roomuserconnections.get(room).size() + " users online");
	}
	
	/** * Remove User from Room * @param inbound */
	public static boolean removeRoom(String aRoomID) {
		//Map<WebSocket, Map<String,String>> roommap = roomuserconnections.get(room);
		if (roomMap.containsKey(aRoomID)) {
			roomMap.remove(aRoomID);
			return true;
		} else {
			return false;
		}
	}
	
	/** * Remove User from Room * @param inbound */
	public static void removeUserinroom(String aRoomID,WebSocket aConn) {
		System.out.println("removeUserinroom() called");
		// 把離開的邏輯坐在這裡
		// 1. 若是Client離開 -> 則把所有人都踢出此room
		// 2. 若是Agent離開 && 剩餘人數 > 1 -> 自己退出就好
		// 3. 若是Agent離開 && 剩餘人數 == 1 -> 則把所有人都踢出此room
//		System.out.println("removeUserinroom(String aRoomID,WebSocket aConn) called");
		RoomInfo roomInfo = roomMap.get(aRoomID);
//		Set<WebSocket> tmpMemberConns = new HashSet(roommap.keySet());
		Map<WebSocket, UserInfo> connsInRoomMap = roomInfo.getUserConns();
		Set<WebSocket> tmpConnsInRoom = new HashSet<>(connsInRoomMap.keySet()); // 通知用,可避免出現concurrent Exception
		JSONObject sendJson = new JSONObject();
		sendJson.put("Event", "removeUserinroom");
		sendJson.put("roomID", aRoomID);
		sendJson.put("fromUserID", WebSocketUserPool.getUserID(aConn));
		
		if (connsInRoomMap != null && connsInRoomMap.containsKey(aConn)) {
//			System.out.println(conn + "'s room is " + " removed");
			String currACType = WebSocketUserPool.getACTypeByKey(aConn);
//			System.out.println("ACType: " + WebSocketUserPool.getACTypeByKey(aConn));
			
			/** 清除room相關資料 **/
			if ("Client".equals(currACType)){
				System.out.println("Client 全清");
				//全清:
				for (WebSocket conn: tmpConnsInRoom){
					WebSocketUserPool.removeUserRoom(conn);
				}
				connsInRoomMap.clear();
				sendJson.put("result", WebSocketUserPool.getUserNameByKey(aConn) + " closed the room" + aRoomID);
			// 之後可將一二條件式合併:
			}else if (connsInRoomMap.size() == 2){ 
				System.out.println("connsInRoom.size() == 2 全清");
				//也全清:
				for (WebSocket conn: tmpConnsInRoom){
					WebSocketUserPool.removeUserRoom(conn);
				}
				connsInRoomMap.clear();
				sendJson.put("result", WebSocketUserPool.getUserNameByKey(aConn) + " closed the room" + aRoomID);				
			}else if (connsInRoomMap.size() > 2){
				System.out.println("connsInRoom.size() > 2  清自己");
				//清Agent自己
				WebSocketUserPool.removeUserRoom(aConn);
				connsInRoomMap.remove(aConn);
				sendJson.put("result", WebSocketUserPool.getUserNameByKey(aConn) + " left the room" + aRoomID);				
			}
			System.out.println("roomId: " + aRoomID + " size: " + connsInRoomMap.size());
			sendJson.put("roomMembers", getOnlineUserNameinroom(aRoomID).toString());
			sendJson.put("roomMemberIDs", getOnlineUserIDinroom(aRoomID).toString());
			sendJson.put("roomSize", connsInRoomMap.size());
			
			System.out.println("removeUserinroom() - sendJson: " + sendJson);
			
			/** 告知所有成員有人離開room,請更新前端頁面 **/
			for (WebSocket conn: tmpConnsInRoom){
				// 若是Logout()觸發的,則跳過 ; 若是自己發出的,也跳過
				if (conn.isClosed() || conn.isClosing()){
					continue;
				}
				
				WebSocketUserPool.sendMessageToUser(conn, sendJson.toString());
			}
			
			System.out.println("roomMap.size() - before: " + roomMap.size());
			// 如果一個room都空了,就把它從Map中清掉
			if (connsInRoomMap.size() == 0){
				roomMap.remove(aRoomID);
			}
			System.out.println("roomMap.size() - after: " + roomMap.size());
			
		}
	}
	
	/** * Send Message to all of User in Room * @param message */
	public static void sendMessageinroom(String aRoomID,String aMessage) {
		RoomInfo roomInfo = roomMap.get(aRoomID);
		Map<WebSocket, UserInfo> connsInRoomMap = roomInfo.getUserConns();
		
		Set<WebSocket> connsInRoom = connsInRoomMap.keySet();
		synchronized (connsInRoom) {
			for (WebSocket conn : connsInRoom) {
				String userID = connsInRoomMap.get(conn).getUserid();
				if (userID != null) {
					conn.send(aMessage);
				}
			}
		}
	}
	
	/** * Get Online User Name in Room * @return */
	// 先預留著,以後判斷須不須保留
	public static Collection<String> getOnlineUserNameinroom(String aRoomID) {
		RoomInfo roomInfo = roomMap.get(aRoomID);
		Map<WebSocket, UserInfo> connsInRoomMap = roomInfo.getUserConns();
		
		List<String> setUsers = new ArrayList<>();
		Collection<UserInfo> setUser = connsInRoomMap.values();
		
		for (UserInfo u : setUser) {
			setUsers.add(u.getUsername());
		}
		return setUsers;
	}
	
	/** * Get Online User ID in Room * @return */
	// 先預留著,以後判斷須不須保留
	public static Collection<String> getOnlineUserIDinroom(String aRoomID) {
		RoomInfo roomInfo = roomMap.get(aRoomID);
		Map<WebSocket, UserInfo> connsInRoomMap = roomInfo.getUserConns();
		
		List<String> setUsers = new ArrayList<>();
		Collection<UserInfo> setUser = connsInRoomMap.values();
		
		for (UserInfo u : setUser) {
			setUsers.add(u.getUserid());
		}
		return setUsers;
	}
	
	/** * Get Online count in Room * @return */
	// 先預留著,以後判斷須不須保留
	public static int getOnlineUserInRoomCount(String aRoomID) {
		return roomMap.get(aRoomID).getUserConns().size();
	}
	
	/** * Get User Name By WebSocket Key from Room * @param session */
	// 先預留著,以後判斷須不須保留
	public static String getUserByKeyInRoom(String aRoomID, WebSocket aConn) {
		RoomInfo roomInfo = roomMap.get(aRoomID);
		Map<WebSocket, UserInfo> connsInRoomMap = roomInfo.getUserConns();
		return connsInRoomMap.get(aConn).getUsername();
	}

	/** * Get Online Room Count * @param */
	public static int getRoomCount() {
		return roomMap.size();
	}
	
	/** * Get Process Time Count * @param */
	public static String getProcessTimeCount(String aRoomID, WebSocket aConn) {
		Date nowtime = new Date();
		Date starttime = roomMap.get(aRoomID).getStarttime(); // 房間開啟時間
		long ProcessTime = starttime.getTime() - nowtime.getTime();
		long seconds = 0, minutes = 0, hours = 0;
	    seconds = ProcessTime / 1000;
	    hours = seconds / 3600;
	    seconds = seconds % 3600;
	    seconds = seconds / 60;
	    minutes = minutes % 60;
		return hours + ":" + minutes + ":" + seconds;
	}
	
	/** * Get Online Room * @param */
	public static Collection<String> getRoomIDs() { // get RoomID
		List<String> setRooms = new ArrayList<String>();
		Collection<String> setRoom = roomMap.keySet();
		for (String u : setRoom) {
			//System.out.println("u: "+u);
			setRooms.add(u);
		}
		return setRooms;
	}

	/** * Get WebSocket Key By User ID from Room * @param user */
	// 先預留著,以後判斷須不須保留
	public static WebSocket getWebSocketByUserInRoom(String aRoomID,String aUser) {
		RoomInfo roomInfo = roomMap.get(aRoomID);
		Map<WebSocket, UserInfo> connsInRoomMap = roomInfo.getUserConns();

		Set<WebSocket> connsInRoom = connsInRoomMap.keySet();
		synchronized (connsInRoom) {
			for (WebSocket conn : connsInRoom) {
				String userID = connsInRoomMap.get(conn).getUserid();
				if (userID.equals(aUser)) {
					return conn;
				}
			}
		}
		return null;
	}

	public static Map<String, RoomInfo> getRoomMaps() {
		return roomMap;
	}
	
	public static RoomInfo getRoomInfo(String aRoomID){
		return roomMap.get(aRoomID);
	}
	
	
}