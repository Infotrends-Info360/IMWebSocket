package com.Info360.dao;

import java.io.IOException;

import org.apache.ibatis.session.SqlSession;

import util.Util;

import com.Info360.bean.Rpt_AgentStatus;
import com.Info360.db.DBAccess;
import com.Info360.util.IsError;
import com.microsoft.sqlserver.jdbc.SQLServerException;


public class Rpt_AgentStatusDao {
	
	/**
	 * 
	 * Select
	 * @param agentstatus
	 */
	public int Select_agentstatus_usetime(Rpt_AgentStatus   agentstatus){
		int agentstatusInt = 0;
		SqlSession sqlSession = null;
		
		try {
			sqlSession = DBAccess.getSqlSession();
			//通過sqlSession執行SQL語句
//			agentstatusInt = sqlSession.insert("rpt_agentstatus.Insert_agentstatus", agentstatus);
			agentstatusInt = sqlSession.selectOne("rpt_agentstatus.Select_agentstatus_usetime", agentstatus);
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
		return agentstatusInt;
	}
	
	/**
	 * 
	 * Select
	 * @param agentstatus
	 */
	public int Select_agentstatus_usetime_avg(Rpt_AgentStatus   agentstatus){
		int agentstatusInt = 0;
		SqlSession sqlSession = null;
		
		try {
			sqlSession = DBAccess.getSqlSession();
			//通過sqlSession執行SQL語句
//			agentstatusInt = sqlSession.insert("rpt_agentstatus.Insert_agentstatus", agentstatus);
			agentstatusInt = sqlSession.selectOne("rpt_agentstatus.Select_agentstatus_usetime_avg", agentstatus);
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
		return agentstatusInt;
	}
	
	
	/**
	 * 
	 * Insert
	 * @param ContactData
	 */
	public int Insert_agentstatus(Rpt_AgentStatus   agentstatus){
		int agentstatusInt = 0;
		SqlSession sqlSession = null;
		
		try {
			sqlSession = DBAccess.getSqlSession();
			//通過sqlSession執行SQL語句
//			agentstatusInt = sqlSession.insert("rpt_agentstatus.Insert_agentstatus", agentstatus);
			agentstatusInt = sqlSession.selectOne("rpt_agentstatus.Insert_agentstatus", agentstatus);
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
		return agentstatusInt;
	}

	/**
	 * 更新個人資訊
	 * @param Update_agentstatus
	 */
	public int Update_agentstatus(
			Rpt_AgentStatus   agentstatus	){
		int agentstatusInt = 0;
		SqlSession sqlSession = null;

		try {
			sqlSession = DBAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			agentstatusInt = sqlSession.update("rpt_agentstatus.Update_agentstatus", agentstatus);
			sqlSession.commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			Util.getFileLogger().debug(e.getMessage());
			Util.getFileLogger().debug(e.getMessage());
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
		return agentstatusInt;
	}
	
}
