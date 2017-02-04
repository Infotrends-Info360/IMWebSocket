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
	private StringBuilder text;
	private JsonArray structuredtext; // gson
	
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
	public StringBuilder getText() {
		return text;
	}
	public void setText(StringBuilder text) {
		this.text = text;
	}
	public JsonArray getStructuredtext() {
		return structuredtext;
	}
	public void setStructuredtext(JsonArray structuredtext) {
		this.structuredtext = structuredtext;
	}
	public Map<WebSocket, UserInfo> getUserConns() {
		return userConns;
	}
	public void setUserConns(Map<WebSocket, UserInfo> userConns) {
		this.userConns = userConns;
	}
	
	
	
//	private String userid;
//	private String username;	
}
