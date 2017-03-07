package com.Info360.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import util.Util;

import com.Info360.bean.Interaction;
import com.Info360.bean.Rpt_Activitylog;
import com.Info360.bean.Rpt_AgentStatus;
import com.Info360.db.DBAccess;
import com.Info360.util.IsError;


public class Rpt_ActivitylogDao {
	
	
	public List<Rpt_Activitylog> Selcet_activitylog(Rpt_Activitylog rpt_activitylog){
		List<Rpt_Activitylog> rpt_activitylogList = new ArrayList<Rpt_Activitylog>();
		SqlSession sqlSession = null;

		try {
			sqlSession = DBAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			rpt_activitylogList = sqlSession.selectList("rpt_activitylog.Selcet_activitylog", rpt_activitylog);
			sqlSession.commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			IsError.GET_EXCEPTION = e.getMessage();
			Util.getFileLogger().error(e.getMessage());
		} finally {
			if(sqlSession != null){
			   sqlSession.close();
			}
		}
		return rpt_activitylogList;
	}
	
	
	
	/**
	 * 
	 * Insert
	 * @param Rpt_Activitylog
	 */
	public int Insert_activitylog(Rpt_Activitylog   activitylog){
		int activitylogInt = 0;
		SqlSession sqlSession = null;
		
		try {
			sqlSession = DBAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			activitylogInt = sqlSession.insert("rpt_activitylog.Insert_activitylog", activitylog);
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
		return activitylogInt;
	}
	
}
