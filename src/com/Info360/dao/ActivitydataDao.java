package com.Info360.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.Info360.bean.Activitydata;
import com.Info360.bean.Activitygroups;
import com.Info360.bean.Cfg_AgentReason;
import com.Info360.bean.CommonLink;
import com.Info360.bean.ContactData;
import com.Info360.bean.Interaction;
import com.Info360.db.DBAccess;
import com.Info360.util.IsError;


public class ActivitydataDao {
	
	/**
	 * @param Delete_activitydata
	 */
	public int Delete_activitydata(
			Activitydata   activitydata)
			{
		DBAccess dbAccess = new DBAccess();
		int activitydataInt = 0;
		SqlSession sqlSession = null;

       try {
			sqlSession = dbAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			activitydataInt = sqlSession.insert("activitydata.Delete_activitydata", activitydata);
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
		return activitydataInt;
	}
	
	
	/**
	 * 更新個人資訊
	 * @param Update_activitydata
	 */
	public int Update_activitydata(
			Activitydata   activitydata	){
		DBAccess dbAccess = new DBAccess();
		int activitydataInt = 0;
		SqlSession sqlSession = null;

		try {
			sqlSession = dbAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			activitydataInt = sqlSession.update("activitydata.Update_activitydata", activitydata);
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
		return activitydataInt;
	}
	
	/**
	 * Insert
	 * @param Insert_activitydata
	 */
	public int Insert_activitydata(Activitydata activitydata){
		DBAccess dbAccess = new DBAccess();
		int activitydataInt = 0;
		SqlSession sqlSession = null;
		
		try {
			sqlSession = dbAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			activitydataInt = sqlSession.insert("activitydata.Insert_activitydata", activitydata);
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
		return activitydataInt;
	}
	
	/**
	 * Select
	 * @param Select_activitydata
	 */
	public List<Activitydata> Select_activitydata(Activitydata activitydata){
		DBAccess dbAccess = new DBAccess();
		List<Activitydata> activitydatalist = new ArrayList<Activitydata>();
		SqlSession sqlSession = null;
	
		try {
			sqlSession = dbAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			activitydatalist = sqlSession.selectList("activitydata.Select_activitydata", activitydata);
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
		return activitydatalist;
	}
	
}
