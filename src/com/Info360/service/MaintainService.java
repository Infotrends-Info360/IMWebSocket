package com.Info360.service;

import java.util.ArrayList;
import java.util.List;

import com.Info360.bean.Activitydata;
import com.Info360.bean.Activitygroups;
import com.Info360.bean.Activitymenu;
import com.Info360.bean.Cfg_AgentReason;
import com.Info360.bean.Cfg_AgentStatus;
import com.Info360.bean.Cfg_ServiceName_Setting;
import com.Info360.bean.CommonLink;
import com.Info360.bean.ContactData;
import com.Info360.bean.Interaction;
import com.Info360.bean.Rpt_AgentStatus;
import com.Info360.bean.ServiceEntry;
import com.Info360.dao.ActivitydataDao;
import com.Info360.dao.ActivitygroupsDao;
import com.Info360.dao.ActivitymenuDao;
import com.Info360.dao.AgentReasonDao;
import com.Info360.dao.Cfg_AgentStatusDao;
import com.Info360.dao.Cfg_ServiceName_SettingDao;
import com.Info360.dao.CommonlinkDao;
import com.Info360.dao.ContactDataDao;
import com.Info360.dao.InteractionDao;
import com.Info360.dao.Rpt_AgentStatusDao;
import com.Info360.dao.ServiceEntryDao;
import com.Info360.util.IsError;

/**
 * 維護相關業務功能
 * 
 * @author Lin
 */
public class MaintainService {

	/**
	 * 
	 * LogicDelete_activitygroups
	 * 
	 * @param LogicDelete_activitygroups
	 */
	public int LogicDelete_activitygroups(Activitygroups activitygroups) {
		int count = 0;
		try {
			ActivitygroupsDao activitygroupsdaodao = new ActivitygroupsDao();
			count = activitygroupsdaodao
					.LogicDelete_activitygroups(activitygroups);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return count;
	}

	/**
	 * 
	 * LogicDelete_ActivityData
	 * 
	 * @param LogicDelete_ActivityData
	 */
	public int LogicDelete_ActivityData(Activitydata activitydata) {
		int count = 0;
		try {
			ActivitydataDao activitydatadaodao = new ActivitydataDao();
			count = activitydatadaodao.LogicDelete_ActivityData(activitydata);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return count;
	}

	/**
	 * 
	 * Flag_activitydata
	 * 
	 * @param Flag_activitydata
	 */
	public List<Activitydata> Flag_activitydata(Activitydata activitydata) {

		List<Activitydata> activitydatalist = new ArrayList<Activitydata>();
		try {
			ActivitydataDao ActivitydataDao = new ActivitydataDao();
			activitydatalist = ActivitydataDao.Flag_activitydata(activitydata);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return activitydatalist;
	}

	/**
	 * 
	 * LogicDelete_activitymenu
	 * 
	 * @param LogicDelete_activitymenu
	 */
	public int LogicDelete_activitymenu(Activitymenu activitymenu) {
		int count = 0;
		try {
			ActivitymenuDao activitymenudaodao = new ActivitymenuDao();
			count = activitymenudaodao.LogicDelete_activitymenu(activitymenu);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return count;
	}

	/**
	 * Delete_agentreason
	 * 
	 * @param Delete_agentreason
	 */
	public int Delete_agentreason(Cfg_AgentReason agentreason) {
		int count = 0;
		try {
			AgentReasonDao agentreasondaodao = new AgentReasonDao();
			count = agentreasondaodao.Delete_agentreason(agentreason);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return count;
	}

	/**
	 * 
	 * Flag_activitymenu
	 * 
	 * @param Flag_activitymenu
	 */
	public List<Activitymenu> Flag_activitymenu(Activitymenu activitymenu) {

		List<Activitymenu> activitymenulist = new ArrayList<Activitymenu>();
		try {
			ActivitymenuDao activitymenudao = new ActivitymenuDao();
			activitymenulist = activitymenudao.Flag_activitymenu(activitymenu);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return activitymenulist;
	}

	/**
	 * update
	 * 
	 * @param Update_activitydata
	 */
	public int Update_activitydata(Activitydata activitydata) {
		int count = 0;
		try {
			ActivitydataDao activitydatadaodao = new ActivitydataDao();
			count = activitydatadaodao.Update_activitydata(activitydata);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return count;
	}

	/**
	 * update
	 * 
	 * @param Update_activitygroups
	 */
	public int Update_activitygroups(Activitygroups activitygroups) {
		int count = 0;
		try {
			ActivitygroupsDao activitygroupsdaodao = new ActivitygroupsDao();
			count = activitygroupsdaodao.Update_activitygroups(activitygroups);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return count;
	}

	/**
	 * update
	 * 
	 * @param Update_activitymenu
	 */
	public int Update_activitymenu(Activitymenu activitymenu) {
		int count = 0;
		try {
			ActivitymenuDao activitymenudaodao = new ActivitymenuDao();
			count = activitymenudaodao.Update_activitymenu(activitymenu);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return count;
	}

	/**
	 * 
	 * insert
	 * 
	 * @param Insert_activitydata
	 */

	public int Insert_activitydata(Activitydata activitydata) {
		int count = 0;
		try {
			ActivitydataDao activitydatadao = new ActivitydataDao();
			count = activitydatadao.Insert_activitydata(activitydata);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return count;
	}

	/**
	 * 
	 * insert
	 * 
	 * @param Insert_activitygroups
	 */

	public int Insert_activitygroups(Activitygroups activitygroups) {
		int count = 0;
		try {
			ActivitygroupsDao activitygroupsdao = new ActivitygroupsDao();
			count = activitygroupsdao.Insert_activitygroups(activitygroups);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return count;
	}

	/**
	 * 
	 * insert
	 * 
	 * @param Insert_activitymenu
	 */

	public int Insert_activitymenu(Activitymenu activitymenu) {
		int count = 0;
		try {
			ActivitymenuDao activitymenudao = new ActivitymenuDao();
			count = activitymenudao.Insert_activitymenu(activitymenu);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return count;
	}

	/**
	 * 
	 * insert
	 * 
	 * @param Insert_agentreason
	 */

	public int Insert_agentreason(Cfg_AgentReason agentreason) {
		int count = 0;
		try {
			AgentReasonDao agentreasondao = new AgentReasonDao();
			count = agentreasondao.Insert_agentreason(agentreason);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return count;
	}

	/**
	 * 
	 * Select_activitydata
	 * 
	 * @param Select_activitydata
	 */
	public List<Activitydata> Select_activitydata(Activitydata activitydata) {

		List<Activitydata> activitydatalist = new ArrayList<Activitydata>();
		try {
			ActivitydataDao activitydatadao = new ActivitydataDao();
			activitydatalist = activitydatadao
					.Select_activitydata(activitydata);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return activitydatalist;
	}

	/**
	 * 
	 * Select_activitygroups
	 * 
	 * @param Select_activitygroups
	 */
	public List<Activitygroups> Select_activitygroups(
			Activitygroups activitygroups) {

		List<Activitygroups> activitygroupslist = new ArrayList<Activitygroups>();
		try {
			ActivitygroupsDao activitygroupsdao = new ActivitygroupsDao();
			activitygroupslist = activitygroupsdao
					.Select_activitygroups(activitygroups);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return activitygroupslist;

	}

	/**
	 * 
	 * Select_activitymenu
	 * 
	 * @param Select_activitymenu
	 */
	public List<Activitymenu> Select_activitymenu(Activitymenu activitymenu) {

		List<Activitymenu> activitymenulist = new ArrayList<Activitymenu>();
		try {
			ActivitymenuDao activitymenudao = new ActivitymenuDao();
			activitymenulist = activitymenudao
					.Select_activitymenu(activitymenu);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return activitymenulist;
	}

	/**
	 * 
	 * LogicDelete_agentstatus
	 * 
	 * @param agentstatus
	 */
	public int LogicDelete_agentreason(Cfg_AgentReason agentreason) {
		int count = 0;
		try {
			AgentReasonDao agentreasondaodao = new AgentReasonDao();
			count = agentreasondaodao.LogicDelete_agentreason(agentreason);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return count;
	}

	/**
	 * 
	 * update
	 * 
	 * @param agentstatus
	 */
	public int Update_agentreason(Cfg_AgentReason agentreason) {
		int count = 0;
		try {
			AgentReasonDao agentreasondaodao = new AgentReasonDao();
			count = agentreasondaodao.Update_agentreason(agentreason);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return count;
	}

	/**
	 * 
	 * Delete
	 * 
	 * @param commonlink
	 */
	public int Delete_commonlink(CommonLink commonlink) {
		int count = 0;
		try {
			CommonlinkDao commonlinkdaodao = new CommonlinkDao();
			count = commonlinkdaodao.Delete_commonlink(commonlink);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return count;
	}

	/**
	 * 
	 * update
	 * 
	 * @param commonlink
	 */
	public int Update_commonlink(CommonLink commonlink) {
		int count = 0;
		try {
			CommonlinkDao commonlinkdaodao = new CommonlinkDao();
			count = commonlinkdaodao.Update_commonlink(commonlink);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return count;
	}

	/**
	 * 
	 * insert
	 * 
	 * @param Insert_commonlink
	 */

	public int Insert_commonlink(CommonLink commonlink) {
		int count = 0;
		try {
			CommonlinkDao commonlinkdao = new CommonlinkDao();
			count = commonlinkdao.Insert_commonlink(commonlink);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return count;
	}

	/**
	 * 
	 * Selcet_interaction
	 * 
	 * @param Selcet_interaction
	 */
	public List<Interaction> Selcet_interaction(Interaction interaction) {

		List<Interaction> interactionlist = new ArrayList<Interaction>();
		try {
			InteractionDao interactiondao = new InteractionDao();
			interactionlist = interactiondao.Selcet_interaction(interaction);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return interactionlist;

	}

	/**
	 * 
	 * Select_agentstatus
	 * 
	 * @param Select_agentstatus
	 */
	public List<Cfg_AgentReason> Select_agentreason(Cfg_AgentReason agentreason) {

		List<Cfg_AgentReason> agentreasonlist = new ArrayList<Cfg_AgentReason>();
		try {
			AgentReasonDao agentreasondao = new AgentReasonDao();
			agentreasonlist = agentreasondao.Select_agentreason(agentreason);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return agentreasonlist;

	}

	/**
	 * 
	 * SelcetAll_interaction
	 * 
	 * @param Selcet_interaction
	 */
	public List<Interaction> SelcetAll_interaction(Interaction interaction) {

		List<Interaction> interactionlist = new ArrayList<Interaction>();
		try {
			InteractionDao interactiondao = new InteractionDao();
			interactionlist = interactiondao.SelcetAll_interaction(interaction);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return interactionlist;

	}

	/**
	 * 
	 * Select_commonlink
	 * 
	 * @param Select_commonlink
	 */
	public List<CommonLink> Select_commonlink(CommonLink commonlink) {

		List<CommonLink> commonlinklist = new ArrayList<CommonLink>();
		try {
			CommonlinkDao commonlinkdao = new CommonlinkDao();
			commonlinklist = commonlinkdao.Select_commonlink(commonlink);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return commonlinklist;
	}

	/**
	 * 
	 * insert
	 * 
	 * @param ServiceEntry
	 */

	public int insert_ServiceEntry(ServiceEntry serviceentry) {
		int count = 0;
		try {
			ServiceEntryDao serviceentrydao = new ServiceEntryDao();
			count = serviceentrydao.insert_ServiceEntry(serviceentry);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return count;
	}

	/**
	 * 
	 * select
	 * 
	 * @param ContactData
	 */

	public String select_ContactData(ContactData contactdata) {
		String contactID = null;
		try {
			ContactDataDao contactdatadao = new ContactDataDao();
			contactID = contactdatadao.query_ContactData(contactdata);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return contactID;
	}

	/**
	 * 
	 * Insert
	 * 
	 * @param ContactData
	 */

	public int insert_ContactData(ContactData contactdata) {
		int count = 0;
		try {
			ContactDataDao contactdatadao = new ContactDataDao();
			count = contactdatadao.insert_ContactData(contactdata);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return count;
	}

	/**
	 * 
	 * update
	 * 
	 * @param ContactData
	 */

	public int update_ContactData(ContactData contactdata) {
		int count = 0;
		try {
			ContactDataDao contactdatadao = new ContactDataDao();
			count = contactdatadao.update_ContactData(contactdata);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return count;
	}

	/**
	 * 
	 * insert
	 * 
	 * @param Interaction
	 */

	public int insert_Interaction(Interaction interaction) {
		int count = 0;
		try {
			InteractionDao interactiondao = new InteractionDao();
			count = interactiondao.insert_Interaction(interaction);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return count;
	}

	/**
	 * 
	 * select
	 * 
	 * @param Cfg_ServiceName_Setting
	 */

	public List<Cfg_ServiceName_Setting> select_Cfg_ServiceName_Setting(
			Cfg_ServiceName_Setting cfg_servicename_setting) {
		List<Cfg_ServiceName_Setting> cfg_servicename_settinglist = null;
		try {
			Cfg_ServiceName_SettingDao cfg_servicename_settingdao = new Cfg_ServiceName_SettingDao();
			cfg_servicename_settinglist = cfg_servicename_settingdao
					.query_Cfg_ServiceName_Setting(cfg_servicename_setting);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return cfg_servicename_settinglist;
	}

	/**
	 * 
	 * insert
	 * 
	 * @param Insert_rpt_agentstatus
	 */

	public int Insert_rpt_agentstatus(Rpt_AgentStatus agentstatus) {
		int count = 0;
		try {
			Rpt_AgentStatusDao agentstatusdao = new Rpt_AgentStatusDao();
			count = agentstatusdao.Insert_agentstatus(agentstatus);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
			e.printStackTrace();
		}
		return count;
	}

	/**
	 * 
	 * update
	 * 
	 * @param rpt_agentstatus
	 */
	public int Update_rpt_agentstatus(Rpt_AgentStatus agentstatus) {
		int count = 0;
		try {
			Rpt_AgentStatusDao agentstatusdao = new Rpt_AgentStatusDao();
			count = agentstatusdao.Update_agentstatus(agentstatus);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return count;
	}
	
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
	   }
	   return agentstatuslist;
	 
	 }

}