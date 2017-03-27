package com.Info360.bean;

import java.util.ArrayList;
import java.util.List;

public class Activitydata {
	
	private int dbid;
	
	private int activitygroupsid;
	
	private String codename;
	
	private String color;
	
	private String sort;
	
	private int titleflag;
	
	private int titlegroup;
	
	private String deleteflag;
	
	private String deletedatetime;
	
	private String createdatetime;
	
	private List<Integer> ActivityData_DBID_list = new ArrayList<Integer>();
	private List<String> ActivityData_SORT_list = new ArrayList<String>();

	

	public List<String> getActivityData_SORT_list() {
		return ActivityData_SORT_list;
	}

	public void setActivityData_SORT_list(List<String> activityData_SORT_list) {
		ActivityData_SORT_list = activityData_SORT_list;
	}

	public List<Integer> getActivityData_DBID_list() {
		return ActivityData_DBID_list;
	}

	public void setActivityData_DBID_list(List<Integer> activityData_DBID_list) {
		ActivityData_DBID_list = activityData_DBID_list;
	}

	public int getTitleflag() {
		return titleflag;
	}

	public void setTitleflag(int titleflag) {
		this.titleflag = titleflag;
	}

	public int getDbid() {
		return dbid;
	}

	public void setDbid(int dbid) {
		this.dbid = dbid;
	}

	public int getActivitygroupsid() {
		return activitygroupsid;
	}

	public void setActivitygroupsid(int activitygroupsid) {
		this.activitygroupsid = activitygroupsid;
	}

	public String getCodename() {
		return codename;
	}

	public void setCodename(String codename) {
		this.codename = codename;
	}

	public String getColor() {
		return color;
	}

	public void setColor(String color) {
		this.color = color;
	}

	public String getSort() {
		return sort;
	}

	public void setSort(String sort) {
		this.sort = sort;
	}

	
	public int getTitlegroup() {
		return titlegroup;
	}

	public void setTitlegroup(int titlegroup) {
		this.titlegroup = titlegroup;
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
	
	

}
