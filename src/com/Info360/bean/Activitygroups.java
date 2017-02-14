package com.Info360.bean;

public class Activitygroups {
	
	private int dbid;
	
	private int activitymenuid;
	
	private String groupname;
	
	private String deletedatetime;
	
	private String createdatetime;
	
	private String sort;
	
	private String deleteflag;

	
	
	public String getDeleteflag() {
		return deleteflag;
	}

	public void setDeleteflag(String deleteflag) {
		this.deleteflag = deleteflag;
	}

	public int getDbid() {
		return dbid;
	}

	public void setDbid(int dbid) {
		this.dbid = dbid;
	}

	public int getActivitymenuid() {
		return activitymenuid;
	}

	public void setActivitymenuid(int activitymenuid) {
		this.activitymenuid = activitymenuid;
	}

	public String getGroupname() {
		return groupname;
	}

	public void setGroupname(String groupname) {
		this.groupname = groupname;
	}

	public String getDeletedatetime() {
		return deletedatetime;
	}

	public void setDeletedatetime(String deletedatetime) {
		this.deletedatetime = deletedatetime;
	}

	public String getCreatedatetime() {
		return createdatetime;
	}

	public void setCreatedatetime(String createdatetime) {
		this.createdatetime = createdatetime;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}
	
	

}
