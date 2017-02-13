package com.Info360.bean;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class CommonLink {
	
	
	private int nodeid;//
	
	private int parnetid;//

	private String nodetext;//

	private String nodeurl;//

	private String nodetype;//
	
	private int sort;//
	
	private String createdatetime;//
	
	private String createuserid;//
	
	private int count;
	
	private String color;
	
	private List<Integer> children_list = new ArrayList<Integer>();
	
	
	
	public String getColor() {
		return color;
	}
	public void setColor(String color) {
		this.color = color;
	}
	public List<Integer> getChildren_list() {
		return children_list;
	}
	public void setChildren_list(List<Integer> children_list) {
		this.children_list = children_list;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getNodeid() {
		return nodeid;
	}
	public void setNodeid(int nodeid) {
		this.nodeid = nodeid;
	}
	public int getParnetid() {
		return parnetid;
	}
	public void setParnetid(int parnetid) {
		this.parnetid = parnetid;
	}
	public int getSort() {
		return sort;
	}
	public void setSort(int sort) {
		this.sort = sort;
	}
	public String getNodetext() {
		return nodetext;
	}
	public void setNodetext(String nodetext) {
		this.nodetext = nodetext;
	}
	public String getNodeurl() {
		return nodeurl;
	}
	public void setNodeurl(String nodeurl) {
		this.nodeurl = nodeurl;
	}
	public String getNodetype() {
		return nodetype;
	}
	public void setNodetype(String nodetype) {
		this.nodetype = nodetype;
	}
	
	public String getCreatedatetime() {
		return createdatetime;
	}
	public void setCreatedatetime(String createdatetime) {
		this.createdatetime = createdatetime;
	}
	public String getCreateuserid() {
		return createuserid;
	}
	public void setCreateuserid(String createuserid) {
		this.createuserid = createuserid;
	}
	
	
	
}
