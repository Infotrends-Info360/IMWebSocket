package com.Info360.dao;

import java.io.IOException;

import org.apache.ibatis.session.SqlSession;

import com.Info360.bean.Rpt_Activitylog;
import com.Info360.bean.Rpt_AgentStatus;
import com.Info360.db.DBAccess;
import com.Info360.util.IsError;


public class Rpt_ActivitylogDao {
	
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
		} finally {
			if(sqlSession != null){
			   sqlSession.close();
			}
		}
		return activitylogInt;
	}
	
}
