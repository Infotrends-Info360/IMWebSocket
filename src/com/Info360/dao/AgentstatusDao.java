package com.Info360.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.Info360.bean.Agentstatus;
import com.Info360.bean.CommonLink;
import com.Info360.bean.ContactData;
import com.Info360.bean.Interaction;
import com.Info360.db.DBAccess;
import com.Info360.util.IsError;


public class AgentstatusDao {
	
	/**
	 * 
	 * Insert
	 * @param ContactData
	 */
	public int Insert_agentstatus(Agentstatus   agentstatus){
		DBAccess dbAccess = new DBAccess();
		int agentstatusInt = 0;
		SqlSession sqlSession = null;
		
		try {
			sqlSession = dbAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			agentstatusInt = sqlSession.insert("agentstatus.Insert_agentstatus", agentstatus);
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
	 * Select_agentstatus
	 * @param Select_agentstatus
	 */
	public List<Agentstatus> Select_agentstatus(Agentstatus agentstatus){
		DBAccess dbAccess = new DBAccess();
		List<Agentstatus> agentstatuslist = new ArrayList<Agentstatus>();
		SqlSession sqlSession = null;
	
		try {
			sqlSession = dbAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			agentstatuslist = sqlSession.selectList("agentstatus.Select_agentstatus", agentstatus);
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
		return agentstatuslist;
	}

	/**
	 * 更新個人資訊
	 * @param Update_agentstatus
	 */
	public int Update_agentstatus(
			Agentstatus   agentstatus	){
		DBAccess dbAccess = new DBAccess();
		int agentstatusInt = 0;
		SqlSession sqlSession = null;

		try {
			sqlSession = dbAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			agentstatusInt = sqlSession.update("agentstatus.Update_agentstatus", agentstatus);
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
	 * @param LogicDelete_agentstatus
	 */
	public int LogicDelete_agentstatus(
			Agentstatus   agentstatus	){
		DBAccess dbAccess = new DBAccess();
		int agentstatusInt = 0;
		SqlSession sqlSession = null;

		try {
			sqlSession = dbAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			agentstatusInt = sqlSession.update("agentstatus.LogicDelete_agentstatus", agentstatus);
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
