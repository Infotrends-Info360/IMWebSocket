package com.Info360.bean;

import java.util.ArrayList;
import java.util.List;

public class Activitymenu {

	private int dbid;
	
	private String menuname;
	
	private String deleteflag;
	
	private String deletedatetime;
	
	private String createdatetime;
	
	private String sort;
	
	private List<Integer> ActivityMenu_DBID_list = new ArrayList<Integer>();
	
	private List<String> ActivityMenu_SORT_list = new ArrayList<String>();

	


	public List<String> getActivityMenu_SORT_list() {
		return ActivityMenu_SORT_list;
	}

	public void setActivityMenu_SORT_list(List<String> activityMenu_SORT_list) {
		ActivityMenu_SORT_list = activityMenu_SORT_list;
	}

	public List<Integer> getActivityMenu_DBID_list() {
		return ActivityMenu_DBID_list;
	}

	public void setActivityMenu_DBID_list(List<Integer> activityMenu_DBID_list) {
		ActivityMenu_DBID_list = activityMenu_DBID_list;
	}

	public int getDbid() {
		return dbid;
	}

	public void setDbid(int dbid) {
		this.dbid = dbid;
	}

	public String getMenuname() {
		return menuname;
	}

	public void setMenuname(String menuname) {
		this.menuname = menuname;
	}

	public String getDeleteflag() {
		return deleteflag;
	}

	public void setDeleteflag(String deleteflag) {
		this.deleteflag = deleteflag;
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
