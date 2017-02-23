package websocket.bean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;

import org.java_websocket.WebSocket;

import util.Util;


//此類別給WebSocketPool.userallconnections使用
public class UserInfo {
	private String userid;
	private String username;
	private List<String> userRoom = Collections.synchronizedList(new ArrayList<String>());
	private String userinteraction;
	private String userheartbeat;
	private Timer heartbeatTimer;
	private String ACType;
	private java.util.Date startdate;
	// 以下為原本TypeInfo部分的屬性
	private String reason;
	private String status;
	private String readyTime;
	private AtomicBoolean stopRing = new AtomicBoolean(false); // 處理concurrent問題
	private AtomicBoolean timeout = new AtomicBoolean(false); // 處理concurrent問題
	
	
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public List<String> getUserRoom() {
		return userRoom;
	}
	public void setUserRoom(List<String> userroom) {
		this.userRoom = userroom;
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
	public String getACType() {
		return ACType;
	}
	public void setACType(String aACType) {
		ACType = aACType;
	}
	public java.util.Date getStartdate() {
		return startdate;
	}
	public void setStartdate(java.util.Date startdate) {
		this.startdate = startdate;
	}
	public String getReason() {
		return reason;
	}
	public void setReason(String reason) {
		this.reason = reason;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getReadyTime() {
		return readyTime;
	}
	public void setReadyTime(String readTime) {
		this.readyTime = readTime;
	}
	public boolean isStopRing() {
		return stopRing.get();
	}
	public void setStopRing(boolean stopRing) {
		this.stopRing.set(stopRing);
	}
	public boolean getTimeout() {
		return timeout.get();
	}
	public void setTimeout(boolean timeout) {
		this.timeout.set(timeout);;
	}
	
	
}



