package BackendService.bean;

public class LoginBean {
	// in
	private String type;
	private String UserID; // 更名(注意)
	private String UserName;
	private String maxCount;
	private String ACtype;
	private String channel;
	
	// out
	private String Event;
	
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getUserID() {
		return UserID;
	}
	public void setUserID(String userID) {
		UserID = userID;
	}
	public String getUserName() {
		return UserName;
	}
	public void setUserName(String userName) {
		UserName = userName;
	}
	public String getMaxCount() {
		return maxCount;
	}
	public void setMaxCount(String maxCount) {
		this.maxCount = maxCount;
	}
	public String getACtype() {
		return ACtype;
	}
	public void setACtype(String aCtype) {
		ACtype = aCtype;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getEvent() {
		return Event;
	}
	public void setEvent(String event) {
		Event = event;
	}
	
	
	
		
}
