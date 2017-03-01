package websocket.bean;

import util.Util;

public class UpdateStatusBean {
	public UpdateStatusBean(){
		
	}
	private String type = "updateStatus";
	private String status;
	private String dbid;
	private String startORend;
	private String roomID;
	private String clientID;
	private String reason_dbid;
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getDbid() {
		return dbid;
	}
	public void setDbid(String dbid) {
		this.dbid = dbid;
	}
	public String getStartORend() {
		return startORend;
	}
	public void setStartORend(String startORend) {
		this.startORend = startORend;
	}
	public String getRoomID() {
		return roomID;
	}
	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}
	public String getClientID() {
		return clientID;
	}
	public void setClientID(String clientID) {
		this.clientID = clientID;
	}
	public String getReason_dbid() {
		return reason_dbid;
	}
	public void setReason_dbid(String reason_dbid) {
		this.reason_dbid = reason_dbid;
	}
	
	
	
	
}
