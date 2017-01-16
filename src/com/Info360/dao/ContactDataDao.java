package com.Info360.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.json.JSONArray;

import com.Info360.bean.ContactData;
import com.Info360.bean.ServiceEntry;
import com.Info360.db.DBAccess;
import com.Info360.util.IsError;

/**
 * 和Message表相關的數據庫操作
 * @author Lin
 */
public class ContactDataDao {
	
	/**
	 * 
	 * Query
	 * @param ContactData
	 */
	public String query_ContactData(ContactData   contactdata){
		DBAccess dbAccess = new DBAccess();
		//List<String> contactdatalist = new ArrayList<String>();
		String contactID = null;
		SqlSession sqlSession = null;
		
		
		try {
			sqlSession = dbAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			contactID = sqlSession.selectOne("contactdata.Query_ContactID",  contactdata);
			sqlSession.commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			IsError.GET_EXCEPTION = e.getMessage();
		} finally {
			if(sqlSession != null){
			   sqlSession.close();
			}
		}
		return contactID;
	}
	
	/**
	 * 
	 * Insert
	 * @param ContactData
	 */
	public int insert_ContactData(ContactData   contactdata){
		DBAccess dbAccess = new DBAccess();
		//List<ServiceEntry> serviceentrylist = new ArrayList<ServiceEntry>();
		int contactdataInt = 0;
		SqlSession sqlSession = null;
		
		
		try {
			sqlSession = dbAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			contactdataInt = sqlSession.insert("contactdata.Insert_ContactData", contactdata);
			sqlSession.commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			IsError.GET_EXCEPTION = e.getMessage();
		} finally {
			if(sqlSession != null){
			   sqlSession.close();
			}
		}
		return contactdataInt;
	}
	
	/**
	 * 
	 * update
	 * @param ContactData
	 */
	public int update_ContactData(ContactData   contactdata){
		DBAccess dbAccess = new DBAccess();
		//List<ServiceEntry> serviceentrylist = new ArrayList<ServiceEntry>();
		int contactdataInt = 0;
		SqlSession sqlSession = null;
		
		
		try {
			sqlSession = dbAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			contactdataInt = sqlSession.update("contactdata.Update_ContactData", contactdata);
			sqlSession.commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			//e.printStackTrace();
			IsError.GET_EXCEPTION = e.getMessage();
		} finally {
			if(sqlSession != null){
			   sqlSession.close();
			}
		}
		return contactdataInt;
	}
	
}
