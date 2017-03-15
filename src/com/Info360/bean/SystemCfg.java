package com.Info360.bean;

public class SystemCfg {
	private Integer DBID; // Stirng?
	private String APP_Name;
	private String Parameter;
	private String Name;
	private String Value;
	private java.sql.Date UpdateTime;
	
	
	public Integer getDBID() {
		return DBID;
	}
	public void setDBID(Integer dBID) {
		DBID = dBID;
	}
	public String getAPP_Name() {
		return APP_Name;
	}
	public void setAPP_Name(String aPP_Name) {
		APP_Name = aPP_Name;
	}
	public String getParameter() {
		return Parameter;
	}
	public void setParameter(String parameter) {
		Parameter = parameter;
	}
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		Name = name;
	}
	public String getValue() {
		return Value;
	}
	public void setValue(String value) {
		Value = value;
	}
	public java.sql.Date getUpdateTime() {
		return UpdateTime;
	}
	public void setUpdateTime(java.sql.Date updateTime) {
		UpdateTime = updateTime;
	}
	
	
}
