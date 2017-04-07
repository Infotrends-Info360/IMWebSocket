package com.Info360.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;





import com.Info360.bean.Cfg_AgentStatus;
import com.Info360.dao.Cfg_AgentStatusDao;
import com.Info360.util.IsError;

import util.Util;


/**
 * 維護相關業務功能
 * 
 * @author Lin
 */
public class MaintainService {
	/**
	  * 
	  * Select_agentstatus
	  * @param Select_cfg_agentstatus
	  */
	 public List<Cfg_AgentStatus> Select_cfg_agentstatus(Cfg_AgentStatus agentstatus) {
	  
	   List<Cfg_AgentStatus> agentstatuslist = new ArrayList<Cfg_AgentStatus>();
	   try {
	    Cfg_AgentStatusDao agentstatusdao = new Cfg_AgentStatusDao();
	    agentstatuslist = agentstatusdao.Select_agentstatus(agentstatus);
	   } catch (Exception e) {
	    IsError.GET_EXCEPTION = e.getMessage();
	    e.printStackTrace();
		Util.getFileLogger().error(e.getMessage());
	   }
	   return agentstatuslist;
	 
	 }

}
