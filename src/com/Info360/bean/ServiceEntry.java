package com.Info360.bean;

/**
 * 與消息表對應的實體類
 * @author Lin
 */

public class ServiceEntry {
	
	private String dbid;
	
	private String contactid;
	
	private String enterkey;
	
	private String userid;

	private String username;
	
	private String entertime;
	
	private String ipaddress;
	
	private String browserversion;
	
	private String platfrom;
	
	private String channeltype;
	
	private String language;

	public String getDbid() {
		return dbid;
	}

	public void setDbid(String dbid) {
		this.dbid = dbid;
	}

	public String getContactid() {
		return contactid;
	}

	public void setContactid(String contactid) {
		this.contactid = contactid;
	}

	public String getEnterkey() {
		return enterkey;
	}

	public void setEnterkey(String enterkey) {
		this.enterkey = enterkey;
	}

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

	public String getEntertime() {
		return entertime;
	}

	public void setEntertime(String entertime) {
		this.entertime = entertime;
	}

	public String getIpaddress() {
		return ipaddress;
	}

	public void setIpaddress(String ipaddress) {
		this.ipaddress = ipaddress;
	}

	public String getBrowserversion() {
		return browserversion;
	}

	public void setBrowserversion(String browserversion) {
		this.browserversion = browserversion;
	}

	public String getPlatfrom() {
		return platfrom;
	}

	public void setPlatfrom(String platfrom) {
		this.platfrom = platfrom;
	}

	public String getChanneltype() {
		return channeltype;
	}

	public void setChanneltype(String channeltype) {
		this.channeltype = channeltype;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
	
}
