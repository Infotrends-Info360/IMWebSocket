package com.Info360.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import util.Util;

import com.Info360.bean.Activitygroups;
import com.Info360.bean.Activitymenu;
import com.Info360.bean.Cfg_AgentReason;
import com.Info360.bean.CommonLink;
import com.Info360.bean.ContactData;
import com.Info360.bean.Interaction;
import com.Info360.db.DBAccess;
import com.Info360.util.IsError;


public class ActivitygroupsDao {
	
	/**
	 * 更新個人資訊
	 * @param LogicDelete_activitygroups
	 */
	public int LogicDelete_activitygroups(
			Activitygroups   activitygroups	){
		int activitygroupsInt = 0;
		SqlSession sqlSession = null;

		try {
			sqlSession = DBAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			activitygroupsInt = sqlSession.update("activitygroups.LogicDelete_activitygroups", activitygroups);
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
		return activitygroupsInt;
	}
	
	/**
	 * 更新個人資訊
	 * @param Update_activitygroups
	 */
	public int Update_activitygroups(
			Activitygroups   activitygroups	){
		int activitygroupsInt = 0;
		SqlSession sqlSession = null;

		try {
			sqlSession = DBAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			activitygroupsInt = sqlSession.update("activitygroups.Update_activitygroups", activitygroups);
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
		return activitygroupsInt;
	}
	/**
	 * Insert
	 * @param Insert_activitygroups
	 */
	public int Insert_activitygroups(Activitygroups activitygroups){
		int activitygroupsInt = 0;
		SqlSession sqlSession = null;
		
		try {
			sqlSession = DBAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			activitygroupsInt = sqlSession.insert("activitygroups.Insert_activitygroups", activitygroups);
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
		return activitygroupsInt;
	}
	
	/**
	 * Select
	 * @param Select_activitygroups
	 */
	public List<Activitygroups> Select_activitygroups(Activitygroups activitygroups){
		List<Activitygroups> activitygroupslist = new ArrayList<Activitygroups>();
		SqlSession sqlSession = null;
	
		try {
			sqlSession = DBAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			activitygroupslist = sqlSession.selectList("activitygroups.Select_activitygroups", activitygroups);
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
		return activitygroupslist;
	}

}
