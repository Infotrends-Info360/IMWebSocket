package com.Info360.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.Info360.bean.Cfg_ServiceName_Setting;
import com.Info360.bean.ServiceEntry;
import com.Info360.db.DBAccess;
import com.Info360.util.IsError;

/**
 * 和Message表相關的數據庫操作
 * @author Lin
 */
public class Cfg_ServiceName_SettingDao {
	
	/**
	 * 查詢個人資訊或所有資訊
	 * Account Query
	 * @param Cfg_ServiceName_Setting
	 */
	public List<Cfg_ServiceName_Setting> query_Cfg_ServiceName_Setting(Cfg_ServiceName_Setting   cfg_servicename_setting){
		List<Cfg_ServiceName_Setting> cfg_servicename_settinglist = new ArrayList<Cfg_ServiceName_Setting>();
		//int serviceentryInt = 0;
		SqlSession sqlSession = null;
		
		
		try {
			sqlSession = DBAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			cfg_servicename_settinglist = sqlSession.selectList("cfg_servicename_setting.Query_Cfg_ServiceName_SettingInfo", cfg_servicename_setting);
			sqlSession.commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			IsError.GET_EXCEPTION = e.getMessage();
		} finally {
			if(sqlSession != null){
			   sqlSession.close();
			}
		}
		return cfg_servicename_settinglist;
	}
	
}
