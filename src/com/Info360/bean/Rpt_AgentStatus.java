package com.Info360.bean;

public class Rpt_AgentStatus {
	
	private int dbid;
	
	private String person_dbid;
	
	private String status_dbid;
	
	private String reason_dbid;
	
	private String startdatetime;
	
	private String enddatetime;
	
	private String duration;

	public int getDbid() {
		return dbid;
	}

	public void setDbid(int dbid) {
		this.dbid = dbid;
	}

	public String getPerson_dbid() {
		return person_dbid;
	}

	public void setPerson_dbid(String person_dbid) {
		this.person_dbid = person_dbid;
	}

	public String getStatus_dbid() {
		return status_dbid;
	}

	public void setStatus_dbid(String status_dbid) {
		this.status_dbid = status_dbid;
	}

	public String getReason_dbid() {
		return reason_dbid;
	}

	public void setReason_dbid(String reason_dbid) {
		this.reason_dbid = reason_dbid;
	}

	public String getStartdatetime() {
		return startdatetime;
	}

	public void setStartdatetime(String startdatetime) {
		this.startdatetime = startdatetime;
	}

	public String getEnddatetime() {
		return enddatetime;
	}

	public void setEnddatetime(String enddatetime) {
		this.enddatetime = enddatetime;
	}

	public String getDuration() {
		return duration;
	}

	public void setDuration(String duration) {
		this.duration = duration;
	}
}
