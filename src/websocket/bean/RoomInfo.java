package websocket.bean;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.java_websocket.WebSocket;

import com.google.gson.JsonArray;

//此類別給WebSocketRoomPool.roomuserconnections使用
public class RoomInfo {
	private String roomID;
	private Date starttime;
	private Map<WebSocket, UserInfo> userConns = new HashMap<>();
	private WebSocket clientConn;
	private String iestablish_dbid;
	
	public String getRoomID() {
		return roomID;
	}
	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}
	public Date getStarttime() {
		return starttime;
	}
	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}
	public Map<WebSocket, UserInfo> getUserConns() {
		return userConns;
	}
	public void setUserConns(Map<WebSocket, UserInfo> userConns) {
		this.userConns = userConns;
	}
	public WebSocket getClientConn() {
		return clientConn;
	}
	public void setClientConn(WebSocket clientConn) {
		this.clientConn = clientConn;
	}
	public String getIestablish_dbid() {
		return iestablish_dbid;
	}
	public void setIestablish_dbid(String iestablish_dbid) {
		this.iestablish_dbid = iestablish_dbid;
	}
	
	
}
