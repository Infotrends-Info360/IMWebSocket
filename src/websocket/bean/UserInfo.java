package websocket.bean;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;

//此類別給WebSocketPool.userallconnections使用
public class UserInfo {
	private String userid;
	private String username;
	private List<String> usergroup = new ArrayList();
	private String userinteraction;
	private String userheartbeat;
	private Timer heartbeatTimer;
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public List<String> getUsergroup() {
		return usergroup;
	}
	public void setUsergroup(List<String> usergroup) {
		this.usergroup = usergroup;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getUserinteraction() {
		return userinteraction;
	}
	public void setUserinteraction(String userinteraction) {
		this.userinteraction = userinteraction;
	}
	public String getUserheartbeat() {
		return userheartbeat;
	}
	public void setUserheartbeat(String userheartbeat) {
		this.userheartbeat = userheartbeat;
	}
	public Timer getHeartbeatTimer() {
		return heartbeatTimer;
	}
	public void setHeartbeatTimer(Timer heartbeatTimer) {
		this.heartbeatTimer = heartbeatTimer;
	}
	
}
