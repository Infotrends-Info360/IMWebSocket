package com.Info360.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import util.Util;

import com.Info360.bean.Activitymenu;
import com.Info360.bean.Cfg_AgentReason;
import com.Info360.bean.CommonLink;
import com.Info360.bean.ContactData;
import com.Info360.bean.Interaction;
import com.Info360.db.DBAccess;
import com.Info360.util.IsError;


public class ActivitymenuDao {
	
	
	/**
	 * Query_AMenu_DBID
	 * @param Query_AMenu_DBID
	 */
	public List<Activitymenu> Query_AMenu_DBID(Activitymenu activitymenu){
		List<Activitymenu> activitymenulist = new ArrayList<Activitymenu>();
		SqlSession sqlSession = null;
	
		try {
			sqlSession = DBAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			activitymenulist = sqlSession.selectList("activitymenu.Query_AMenu_DBID", activitymenu);
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
		return activitymenulist;
	}
	
	/**
	 * Query_AMenu_Sort
	 * @param Query_AMenu_Sort
	 */
	public List<Activitymenu> Query_AMenu_Sort(Activitymenu activitymenu){
		List<Activitymenu> activitymenulist = new ArrayList<Activitymenu>();
		SqlSession sqlSession = null;
	
		try {
			sqlSession = DBAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			activitymenulist = sqlSession.selectList("activitymenu.Query_AMenu_Sort", activitymenu);
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
		return activitymenulist;
	}
	
	
	/**
	 * AMenu_Sort
	 * @param AMenu_Sort
	 */
	public int AMenu_Sort(
			Activitymenu   activitymenu	){
		int activitymenuInt = 0;
		SqlSession sqlSession = null;

		try {
			sqlSession = DBAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			activitymenuInt = sqlSession.update("activitymenu.AMenu_Sort", activitymenu);
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
		return activitymenuInt;
	}
	
	
	/**
	 * LogicDelete_activitymenu
	 * @param LogicDelete_activitymenu
	 */
	public int LogicDelete_activitymenu(
			Activitymenu   activitymenu	){
		int activitymenuInt = 0;
		SqlSession sqlSession = null;

		try {
			sqlSession = DBAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			activitymenuInt = sqlSession.update("activitymenu.LogicDelete_activitymenu", activitymenu);
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
		return activitymenuInt;
	}
	
	/**
	 * Flag_activitymenu
	 * @param Flag_activitymenu
	 */
	public List<Activitymenu> Flag_activitymenu(Activitymenu activitymenu){
		List<Activitymenu> activitymenulist = new ArrayList<Activitymenu>();
		SqlSession sqlSession = null;
	
		try {
			sqlSession = DBAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			activitymenulist = sqlSession.selectList("activitymenu.Flag_activitymenu", activitymenu);
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
		return activitymenulist;
	}
	
	
	
	/**
	 * 更新個人資訊
	 * @param Update_activitymenu
	 */
	public int Update_activitymenu(
			Activitymenu   activitymenu	){
		int activitymenuInt = 0;
		SqlSession sqlSession = null;

		try {
			sqlSession = DBAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			activitymenuInt = sqlSession.update("activitymenu.Update_activitymenu", activitymenu);
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
		return activitymenuInt;
	}

	/**
	 * Select_activitymenu
	 * @param Select_activitymenu
	 */
	public List<Activitymenu> Select_activitymenu(Activitymenu activitymenu){
		List<Activitymenu> activitymenulist = new ArrayList<Activitymenu>();
		SqlSession sqlSession = null;
	
		try {
			sqlSession = DBAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			activitymenulist = sqlSession.selectList("activitymenu.Select_activitymenu", activitymenu);
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
		return activitymenulist;
	}

	
	/**
	 * Insert
	 * @param Insert_activitymenu
	 */
	public int Insert_activitymenu(Activitymenu activitymenu){
		int activitymenuInt = 0;
		SqlSession sqlSession = null;
		
		
		try {
			sqlSession = DBAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			activitymenuInt = sqlSession.insert("activitymenu.Insert_activitymenu", activitymenu);
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
		return activitymenuInt;
	}
	
}
