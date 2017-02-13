package com.Info360.service;

import java.util.ArrayList;
import java.util.List;

import com.Info360.bean.Activitydata;
import com.Info360.bean.Activitygroups;
import com.Info360.bean.Activitymenu;
import com.Info360.bean.Agentstatus;
import com.Info360.bean.Cfg_ServiceName_Setting;
import com.Info360.bean.CommonLink;
import com.Info360.bean.ContactData;
import com.Info360.bean.Interaction;
import com.Info360.bean.ServiceEntry;
import com.Info360.dao.ActivitydataDao;
import com.Info360.dao.ActivitygroupsDao;
import com.Info360.dao.ActivitymenuDao;
import com.Info360.dao.AgentstatusDao;
import com.Info360.dao.Cfg_ServiceName_SettingDao;
import com.Info360.dao.CommonlinkDao;
import com.Info360.dao.ContactDataDao;
import com.Info360.dao.InteractionDao;
import com.Info360.dao.ServiceEntryDao;
import com.Info360.util.IsError;

/**
 * 維護相關業務功能
 * 
 * @author Lin
 */
public class MaintainService {
	
	/**
	 * Delete
	 * @param Delete_activitygroups
	 */
	public int Delete_activitydata(Activitydata activitydata) {
		int count = 0;
		try {
			ActivitydataDao activitydatadaodao = new ActivitydataDao();
			count = activitydatadaodao.Delete_activitydata(activitydata);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return count;
	}
	
	/**
	 * Delete
	 * @param Delete_activitygroups
	 */
	public int Delete_activitygroups(Activitygroups activitygroups) {
		int count = 0;
		try {
			ActivitygroupsDao activitygroupsdaodao = new ActivitygroupsDao();
			count = activitygroupsdaodao.Delete_activitygroups(activitygroups);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return count;
	}
	
	/**
	 * Delete
	 * @param Delete_activitymenu
	 */
	public int Delete_activitymenu(Activitymenu activitymenu) {
		int count = 0;
		try {
			ActivitymenuDao activitymenudaoodao = new ActivitymenuDao();
			count = activitymenudaoodao.Delete_activitymenu(activitymenu);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return count;
	}
	
	/**
	 * update
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
	 * @param Insert_agentstatus
	 */

	public int Insert_agentstatus(Agentstatus agentstatus) {
		int count = 0;
		try {
			AgentstatusDao agentstatusdao = new AgentstatusDao();
			count = agentstatusdao.Insert_agentstatus(agentstatus);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return count;
	}
	
	/**
	 * 
	 * Select_activitydata
	 * @param Select_activitydata
	 */
	public List<Activitydata> Select_activitydata(Activitydata activitydata) {
	
			List<Activitydata> activitydatalist = new ArrayList<Activitydata>();
			try {
				ActivitydataDao activitydatadao = new ActivitydataDao();
				activitydatalist = activitydatadao.Select_activitydata(activitydata);
			} catch (Exception e) {
				IsError.GET_EXCEPTION = e.getMessage();
			}
			return activitydatalist;
	}
	
	/**
	 * 
	 * Select_activitymenu
	 * @param Select_activitymenu
	 */
	public List<Activitygroups> Select_activitygroups(Activitygroups activitygroups) {
	
			List<Activitygroups> activitygroupslist = new ArrayList<Activitygroups>();
			try {
				ActivitygroupsDao activitygroupsdao = new ActivitygroupsDao();
				activitygroupslist = activitygroupsdao.Select_activitygroups(activitygroups);
			} catch (Exception e) {
				IsError.GET_EXCEPTION = e.getMessage();
			}
			return activitygroupslist;
		
	}
	
	/**
	 * 
	 * Select_activitymenu
	 * @param Select_activitymenu
	 */
	public List<Activitymenu> Select_activitymenu(Activitymenu activitymenu) {
	
			List<Activitymenu> activitymenulist = new ArrayList<Activitymenu>();
			try {
				ActivitymenuDao activitymenudao = new ActivitymenuDao();
				activitymenulist = activitymenudao.Select_activitymenu(activitymenu);
			} catch (Exception e) {
				IsError.GET_EXCEPTION = e.getMessage();
			}
			return activitymenulist;
	}
	
	/**
	 * 
	 * LogicDelete_agentstatus
	 * @param agentstatus
	 */
	public int LogicDelete_agentstatus(Agentstatus agentstatus) {
		int count = 0;
		try {
			AgentstatusDao agentstatusdaodao = new AgentstatusDao();
			count = agentstatusdaodao.LogicDelete_agentstatus(agentstatus);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return count;
	}
	
	/**
	 * 
	 * update
	 * @param agentstatus
	 */
	public int Update_agentstatus(Agentstatus agentstatus) {
		int count = 0;
		try {
			AgentstatusDao agentstatusdaodao = new AgentstatusDao();
			count = agentstatusdaodao.Update_agentstatus(agentstatus);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return count;
	}
	
	/**
	 * 
	 * Delete
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
	 * @param Select_agentstatus
	 */
	public List<Agentstatus> Select_agentstatus(Agentstatus agentstatus) {
		
			List<Agentstatus> agentstatuslist = new ArrayList<Agentstatus>();
			try {
				AgentstatusDao agentstatusdao = new AgentstatusDao();
				agentstatuslist = agentstatusdao.Select_agentstatus(agentstatus);
			} catch (Exception e) {
				IsError.GET_EXCEPTION = e.getMessage();
			}
			return agentstatuslist;
	
	}
	
	/**
	 * 
	 * SelcetAll_interaction
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
	 * @param Cfg_ServiceName_Setting
	 */

	public List<Cfg_ServiceName_Setting> select_Cfg_ServiceName_Setting(Cfg_ServiceName_Setting cfg_servicename_setting) {
		List<Cfg_ServiceName_Setting> cfg_servicename_settinglist = null;
		try {
			Cfg_ServiceName_SettingDao cfg_servicename_settingdao = new Cfg_ServiceName_SettingDao();
			cfg_servicename_settinglist = cfg_servicename_settingdao.query_Cfg_ServiceName_Setting(cfg_servicename_setting);
		} catch (Exception e) {
			IsError.GET_EXCEPTION = e.getMessage();
		}
		return cfg_servicename_settinglist;
	}
	
}