package websocket.bean;

import java.util.Date;
import com.google.gson.JsonArray;

//此類別給WebSocketRoomPool.roomuserconnections使用
public class RoomInfo {
	private String userid;
	private String username;
	private Date starttime;
	private StringBuilder text;
	private JsonArray structuredtext; // gson
	
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Date getStarttime() {
		return starttime;
	}
	public void setStarttime(Date starttime) {
		this.starttime = starttime;
	}
	public JsonArray getStructuredtext() {
		return structuredtext;
	}
	public void setStructuredtext(JsonArray structuredtext) {
		this.structuredtext = structuredtext;
	}
	public StringBuilder getText() {
		return text;
	}
	public void setText(StringBuilder text) {
		this.text = text;
	}
	
	
}
