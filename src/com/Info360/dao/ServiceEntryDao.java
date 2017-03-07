package com.Info360.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import util.Util;

import com.Info360.bean.ServiceEntry;
import com.Info360.db.DBAccess;
import com.Info360.util.IsError;

/**
 * 和Message表相關的數據庫操作
 * @author Lin
 */
public class ServiceEntryDao {
	
	/**
	 * 查詢個人資訊或所有資訊
	 * Account Query
	 * @param cfg_servicename
	 */
	public int insert_ServiceEntry(ServiceEntry   serviceentry){
		//List<ServiceEntry> serviceentrylist = new ArrayList<ServiceEntry>();
		int serviceentryInt = 0;
		SqlSession sqlSession = null;
		
		
		try {
			sqlSession = DBAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			serviceentryInt = sqlSession.insert("serviceentry.Insert_ServiceEntry", serviceentry);
			sqlSession.commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			IsError.GET_EXCEPTION = e.getMessage();
			Util.getFileLogger().error(e.getMessage());
		} catch (Exception e){
			e.printStackTrace();
			IsError.GET_EXCEPTION = e.getMessage();
			Util.getFileLogger().error(e.getMessage());
		} finally {
			if(sqlSession != null){
			   sqlSession.close();
				DBAccess.sessonCount.decrementAndGet();
				Util.getFileLogger().debug("DB session count: " + DBAccess.sessonCount.get());
			}
		}
		return serviceentryInt;
	}
	
}
