package com.Info360.bean;

import java.util.ArrayList;
import java.util.List;

/**
 * 與消息表對應的實體類
 * @author Lin
 */

public class CFG_person {
	
	private String account;
	
	private String first_name;
	
	private String last_name;
	
	private String user_name;
	
	private String emailaddress;
	
	private String password;
	
	private    int dbid;
	
	private    int dn;
	
	private String employee_id;
	
	private   int  state;
	
	private String createdatetime;
	
	private int createuserid;
	
	private String ch_pass_on_login;
	
	private int pass_error_count;
	
	private int max_count;
	
	private List<Integer> personDBID_list = new ArrayList<Integer>();

	
	
	
	public List<Integer> getPersonDBID_list() {
		return personDBID_list;
	}

	public void setPersonDBID_list(List<Integer> personDBID_list) {
		this.personDBID_list = personDBID_list;
	}

	public int getMax_count() {
		return max_count;
	}

	public void setMax_count(int max_count) {
		this.max_count = max_count;
	}

	public String getCh_pass_on_login() {
		return ch_pass_on_login;
	}

	public void setCh_pass_on_login(String ch_pass_on_login) {
		this.ch_pass_on_login = ch_pass_on_login;
	}

	public int getCreateuserid() {
		return createuserid;
	}

	public void setCreateuserid(int createuserid) {
		this.createuserid = createuserid;
	}



	public int getPass_error_count() {
		return pass_error_count;
	}

	public void setPass_error_count(int pass_error_count) {
		this.pass_error_count = pass_error_count;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public String getFirst_name() {
		return first_name;
	}

	public void setFirst_name(String first_name) {
		this.first_name = first_name;
	}

	public String getLast_name() {
		return last_name;
	}

	public void setLast_name(String last_name) {
		this.last_name = last_name;
	}

	public String getUser_name() {
		return user_name;
	}

	public void setUser_name(String user_name) {
		this.user_name = user_name;
	}

	public String getEmailaddress() {
		return emailaddress;
	}

	public void setEmailaddress(String emailaddress) {
		this.emailaddress = emailaddress;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public int getDbid() {
		return dbid;
	}

	public void setDbid(int dbid) {
		this.dbid = dbid;
	}

	public String getEmployee_id() {
		return employee_id;
	}

	public void setEmployee_id(String employee_id) {
		this.employee_id = employee_id;
	}

	public int getState() {
		return state;
	}

	public void setState(int state) {
		this.state = state;
	}

	public String getCreatedatetime() {
		return createdatetime;
	}

	public void setCreatedatetime(String createdatetime) {
		this.createdatetime = createdatetime;
	}

	public int getDn() {
		return dn;
	}

	public void setDn(int dn) {
		this.dn = dn;
	}
	
}
