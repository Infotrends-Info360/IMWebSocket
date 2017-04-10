package websocket.bean;

import org.json.JSONObject;

public class ThirdPartyBean {
	
	String Event;
	String ACtype;
	String roomID;
	String fromAgentID;
	String invitedAgentID;
	String fromAgentName;
	String inviteType;
	JSONObject userdata;
	String text;
	
	String response;
	
	public String getACtype() {
		return ACtype;
	}
	public void setACtype(String aCtype) {
		ACtype = aCtype;
	}
	public String getRoomID() {
		return roomID;
	}
	public void setRoomID(String roomID) {
		this.roomID = roomID;
	}
	public String getFromAgentID() {
		return fromAgentID;
	}
	public void setFromAgentID(String fromAgentID) {
		this.fromAgentID = fromAgentID;
	}
	public String getInvitedAgentID() {
		return invitedAgentID;
	}
	public void setInvitedAgentID(String invitedAgentID) {
		this.invitedAgentID = invitedAgentID;
	}
	public String getFromAgentName() {
		return fromAgentName;
	}
	public void setFromAgentName(String fromAgentName) {
		this.fromAgentName = fromAgentName;
	}
	public String getInviteType() {
		return inviteType;
	}
	public void setInviteType(String inviteType) {
		this.inviteType = inviteType;
	}
	public JSONObject getUserdata() {
		return userdata;
	}
	public void setUserdata(JSONObject userdata) {
		this.userdata = userdata;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getEvent() {
		return Event;
	}
	public void setEvent(String event) {
		Event = event;
	}
	public String getResponse() {
		return response;
	}
	public void setResponse(String response) {
		this.response = response;
	}
	
	
	
	
	
	
	
}
