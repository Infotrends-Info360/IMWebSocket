package com.Info360.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.Info360.bean.Activitymenu;
import com.Info360.bean.Cfg_AgentReason;
import com.Info360.bean.CommonLink;
import com.Info360.bean.ContactData;
import com.Info360.bean.Interaction;
import com.Info360.db.DBAccess;
import com.Info360.util.IsError;


public class ActivitymenuDao {
	
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
		} finally {
			if(sqlSession != null){
				sqlSession.close();
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
		} finally {
			if(sqlSession != null){
			   sqlSession.close();
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
		} finally {
			if(sqlSession != null){
				sqlSession.close();
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
		} finally {
			if(sqlSession != null){
			   sqlSession.close();
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
		} finally {
			if(sqlSession != null){
			   sqlSession.close();
			}
		}
		return activitymenuInt;
	}
	
}
