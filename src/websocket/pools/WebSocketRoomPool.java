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
	private static final Map<String, Map<WebSocket, RoomInfo>> roomuserconnections = new HashMap<String, Map<WebSocket, RoomInfo>>();
	
	/** * Add User to Room Map * @param inbound */
	public static void addUserinroom(String room,String username,String userid, WebSocket conn) {
		
		RoomInfo roominfo = new RoomInfo();
		roominfo.setUserid(userid);
		roominfo.setUsername(username);
		Date starttime = new Date();
		roominfo.setStarttime(starttime);
		
		Map<WebSocket, RoomInfo> roommap = roomuserconnections.get(room);
		if (roommap == null || roommap.isEmpty()){
			roommap = new HashMap<WebSocket, RoomInfo>();			
		}
		roommap.put(conn, roominfo);
		roomuserconnections.put(room, roommap);
		
		System.out.println("room: " + room + " now has " + roomuserconnections.get(room).size() + " users online");
	}
	
	/** * Remove User from Room * @param inbound */
	public static boolean removeRoom(String room) {
		//Map<WebSocket, Map<String,String>> roommap = roomuserconnections.get(room);
		if (roomuserconnections.containsKey(room)) {
			roomuserconnections.remove(room);
			return true;
		} else {
			return false;
		}
	}
	
	/** * Remove User from Room * @param inbound */
	public static boolean removeUserinroom(String room,WebSocket conn) {
		// 把離開的邏輯坐在這裡
		// 1. 若是Client離開 -> 則把所有人都踢出此room
		// 2. 若是Agent離開 && 剩餘人數 > 1 -> 自己退出就好
		// 3. 若是Agent離開 && 剩餘人數 == 1 -> 則把所有人都踢出此room
		System.out.println("removeUserinroom(String room,WebSocket conn) called");
		Map<WebSocket, RoomInfo> roommap = roomuserconnections.get(room);
		if (roommap.containsKey(conn)) {
//			System.out.println(conn + "'s room is " + " removed");
			String currACType = WebSocketUserPool.getACTypeByKey(conn);
			System.out.println("ACType: " + WebSocketUserPool.getACTypeByKey(conn));
			if ("Client".equals(currACType)){
				System.out.println("Client 全清");
				//全清:
				//補上清WebSocketUserPool
				//補上告知相關人員的迴圈.send
				roommap.clear();
			}else if (roommap.size() == 2){
				System.out.println("roommap.size() == 2 全清");
				//也全清:
				//補上清WebSocketUserPool
				//補上告知相關人員的迴圈.send
				roommap.clear();
			}else if (roommap.size() > 2){
				System.out.println("roommap.size() > 2  清自己");
				//清Agent自己
				//補上清WebSocketUserPool
				//補上告知相關人員的迴圈.send // 有人離開了就好
				roommap.remove(conn);
			}
			System.out.println("roomId: " + room + " size: " + roommap.size());
//			roommap.remove(conn);
			return true;
		} else {
			return false;
		}
	}
	
	/** * Send Message to all of User in Room * @param message */
	public static void sendMessageinroom(String room,String message) {
		Map<WebSocket, RoomInfo> roommap = roomuserconnections.get(room);
		Set<WebSocket> keySet = roommap.keySet();
		synchronized (keySet) {
			for (WebSocket conn : keySet) {
				String userid = roommap.get(conn).getUserid();
				if (userid != null) {
					conn.send(message);
				}
			}
		}
	}
	
	/** * Get Online User Name in Room * @return */
	// 先預留著,以後判斷須不須保留
	public static Collection<String> getOnlineUserinroom(String room) {
		Map<WebSocket, RoomInfo> roommap = roomuserconnections.get(room);
		List<String> setUsers = new ArrayList<String>();
		Collection<RoomInfo> setUser = roommap.values();
		for (RoomInfo u : setUser) {
			setUsers.add(u.getUsername());
		}
		return setUsers;
	}
	
	/** * Get Online count in Room * @return */
	// 先預留著,以後判斷須不須保留
	public static int getOnlineUserInRoomCount(String room) {
		return roomuserconnections.get(room).size();
	}
	
	/** * Get User Name By WebSocket Key from Room * @param session */
	// 先預留著,以後判斷須不須保留
	public static String getUserByKeyInRoom(String room, WebSocket conn) {
		Map<WebSocket, RoomInfo> roommap = roomuserconnections.get(room);
		return roommap.get(conn).getUsername();
	}

	/** * Get Online Room Count * @param */
	public static int getRoomCount() {
		return roomuserconnections.size();
	}
	
	/** * Get Process Time Count * @param */
	public static String getProcessTimeCount(String room, WebSocket conn) {
		Date nowtime = new Date();
		Date starttime = roomuserconnections.get(room).get(conn).getStarttime();
		long ProcessTime = starttime.getTime()-nowtime.getTime();
		long seconds = 0, minutes = 0, hours = 0;
	    seconds = ProcessTime / 1000;
	    hours = seconds / 3600;
	    seconds = seconds % 3600;
	    seconds = seconds / 60;
	    minutes = minutes % 60;
		return hours + ":" + minutes + ":" + seconds;
	}
	
	/** * Get Online Room * @param */
	public static Collection<String> getRooms() {
		List<String> setRooms = new ArrayList<String>();
		Collection<String> setRoom = roomuserconnections.keySet();
		for (String u : setRoom) {
			//System.out.println("u: "+u);
			setRooms.add(u);
		}
		return setRooms;
	}

	/** * Get WebSocket Key By User ID from Room * @param user */
	// 先預留著,以後判斷須不須保留
	public static WebSocket getWebSocketByUserInRoom(String room,String user) {
		Map<WebSocket, RoomInfo> roommap = roomuserconnections.get(room);
		Set<WebSocket> keySet = roommap.keySet();
		synchronized (keySet) {
			for (WebSocket conn : keySet) {
				String cuser = roommap.get(conn).getUserid();
				if (cuser.equals(user)) {
					return conn;
				}
			}
		}
		return null;
	}

	public static Map<String, Map<WebSocket, RoomInfo>> getRoomUserConnections() {
		return roomuserconnections;
	}
	
	
	
}