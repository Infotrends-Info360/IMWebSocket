package com.Info360.bean;

import java.util.HashMap;
import java.util.Map;

/**
 * 與消息表對應的實體類
 * @author Lin
 */

public class Interaction {
	
	private String dbid;

	private String userid;
	
	private String contactid;
	
	private String ixnid;
	
	private String agentid;

	private String startdate;
	
	private String enddate;
	
	private String status;
	
	private String typeid;
	
	private String entitytypeid;
	
	private String subtypeid;
	
	private String subject;
	
	private String text;
	
	private String structuredtext;
	
	private String structuredmimetype;
	
	private String thecomment;
	
	private int page;
	

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getThecomment() {
		return thecomment;
	}

	public void setThecomment(String thecomment) {
		this.thecomment = thecomment;
	}

	private String stoppedreason;
	
	private String activitycode;
	
	private Map<String,String> interactionlist = new HashMap<String,String>();

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

	public String getIxnid() {
		return ixnid;
	}

	public void setIxnid(String ixnid) {
		this.ixnid = ixnid;
	}

	public String getAgentid() {
		return agentid;
	}

	public void setAgentid(String agentid) {
		this.agentid = agentid;
	}

	public String getStartdate() {
		return startdate;
	}

	public void setStartdate(String startdate) {
		this.startdate = startdate;
	}

	public String getEnddate() {
		return enddate;
	}

	public void setEnddate(String enddate) {
		this.enddate = enddate;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getTypeid() {
		return typeid;
	}

	public void setTypeid(String typeid) {
		this.typeid = typeid;
	}

	public String getEntitytypeid() {
		return entitytypeid;
	}

	public void setEntitytypeid(String entitytypeid) {
		this.entitytypeid = entitytypeid;
	}

	public String getSubtypeid() {
		return subtypeid;
	}

	public void setSubtypeid(String subtypeid) {
		this.subtypeid = subtypeid;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public String getStructuredtext() {
		return structuredtext;
	}

	public void setStructuredtext(String structuredtext) {
		this.structuredtext = structuredtext;
	}

	public String getStructuredmimetype() {
		return structuredmimetype;
	}

	public void setStructuredmimetype(String structuredmimetype) {
		this.structuredmimetype = structuredmimetype;
	}

	public String getStoppedreason() {
		return stoppedreason;
	}

	public void setStoppedreason(String stoppedreason) {
		this.stoppedreason = stoppedreason;
	}

	public String getActivitycode() {
		return activitycode;
	}

	public void setActivitycode(String activitycode) {
		this.activitycode = activitycode;
	}

	public Map<String, String> getInteractionlist() {
		return interactionlist;
	}

	public void setInteractionlist(Map<String, String> interactionlist) {
		this.interactionlist = interactionlist;
	}

	public String getUserid() {
		return userid;
	}

	public void setUserid(String userid) {
		this.userid = userid;
	}
	
	
	
}
