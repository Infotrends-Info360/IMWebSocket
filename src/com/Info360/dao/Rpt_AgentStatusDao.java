package com.Info360.dao;

import java.io.IOException;

import org.apache.ibatis.session.SqlSession;

import com.Info360.bean.Rpt_AgentStatus;
import com.Info360.db.DBAccess;
import com.Info360.util.IsError;


public class Rpt_AgentStatusDao {
	
	/**
	 * 
	 * Insert
	 * @param ContactData
	 */
	public int Insert_agentstatus(Rpt_AgentStatus   agentstatus){
		DBAccess dbAccess = new DBAccess();
		int agentstatusInt = 0;
		SqlSession sqlSession = null;
		
		try {
			sqlSession = dbAccess.getSqlSession();
			//通過sqlSession執行SQL語句
//			agentstatusInt = sqlSession.insert("rpt_agentstatus.Insert_agentstatus", agentstatus);
			agentstatusInt = sqlSession.selectOne("rpt_agentstatus.Insert_agentstatus", agentstatus);
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
		return agentstatusInt;
	}

	/**
	 * 更新個人資訊
	 * @param Update_agentstatus
	 */
	public int Update_agentstatus(
			Rpt_AgentStatus   agentstatus	){
		DBAccess dbAccess = new DBAccess();
		int agentstatusInt = 0;
		SqlSession sqlSession = null;

		try {
			sqlSession = dbAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			agentstatusInt = sqlSession.update("rpt_agentstatus.Update_agentstatus", agentstatus);
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
		return agentstatusInt;
	}
	
}