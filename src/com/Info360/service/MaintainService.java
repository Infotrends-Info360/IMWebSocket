package com.Info360.service;

import java.util.ArrayList;
import java.util.List;

import com.Info360.bean.Cfg_ServiceName_Setting;
import com.Info360.bean.CommonLink;
import com.Info360.bean.ContactData;
import com.Info360.bean.Interaction;
import com.Info360.bean.ServiceEntry;
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