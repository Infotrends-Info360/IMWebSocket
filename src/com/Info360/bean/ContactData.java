package com.Info360.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * 與消息表對應的實體類
 * @author Lin
 */

public class ContactData {
	
	private String dbid;
	
	private String contactid;
	
	private String createdate;
	
	private String modifieddate;
	
	private Map<String,String> userdata = new HashMap<String,String>();
	
	private String searchkey;
	
	private String searchvalue;
	
	private String pkey;
	
	private String pvalue;

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

	public String getCreatedate() {
		return createdate;
	}

	public void setCreatedate(String createdate) {
		this.createdate = createdate;
	}

	public String getModifieddate() {
		return modifieddate;
	}

	public void setModifieddate(String modifieddate) {
		this.modifieddate = modifieddate;
	}

	public Map<String, String> getUserdata() {
		return userdata;
	}

	public void setUserdata(Map<String, String> userdata) {
		this.userdata = userdata;
	}

	public String getSearchkey() {
		return searchkey;
	}

	public void setSearchkey(String searchkey) {
		this.searchkey = searchkey;
	}

	public String getSearchvalue() {
		return searchvalue;
	}

	public void setSearchvalue(String searchvalue) {
		this.searchvalue = searchvalue;
	}

	public String getPkey() {
		return pkey;
	}

	public void setPkey(String pkey) {
		this.pkey = pkey;
	}

	public String getPvalue() {
		return pvalue;
	}

	public void setPvalue(String pvalue) {
		this.pvalue = pvalue;
	}
	
}
