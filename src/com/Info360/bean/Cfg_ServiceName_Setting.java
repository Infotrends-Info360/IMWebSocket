package com.Info360.bean;

/**
 * 與消息表對應的實體類
 * @author Lin
 */

public class Cfg_ServiceName_Setting {
	
	private String dbid;
	
	private String typeid;
	
	private String uniquekey;
	
	private String searchkey;

	public String getDbid() {
		return dbid;
	}

	public void setDbid(String dbid) {
		this.dbid = dbid;
	}

	public String getTypeid() {
		return typeid;
	}

	public void setTypeid(String typeid) {
		this.typeid = typeid;
	}

	public String getUniquekey() {
		return uniquekey;
	}

	public void setUniquekey(String uniquekey) {
		this.uniquekey = uniquekey;
	}

	public String getSearchkey() {
		return searchkey;
	}

	public void setSearchkey(String searchkey) {
		this.searchkey = searchkey;
	}
	
}
