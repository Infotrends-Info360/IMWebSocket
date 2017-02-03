package websocket.bean;

import java.util.Date;

//此類別給WebSocketRoomPool.roomuserconnections使用
public class RoomInfo {
	private String userid;
	private String username;
	private Date starttime;
	private String text;
	private String structuredtext;
	
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
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getStructuredtext() {
		return structuredtext;
	}
	public void setStructuredtext(String structuredtext) {
		this.structuredtext = structuredtext;
	}
	
}
