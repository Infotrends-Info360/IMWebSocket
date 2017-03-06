package com.Info360.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import util.Util;

import com.Info360.bean.CommonLink;
import com.Info360.bean.ContactData;
import com.Info360.bean.Interaction;
import com.Info360.db.DBAccess;
import com.Info360.util.IsError;


public class CommonlinkDao {

	
	/**
	 * @param CommonLink
	 */
	public int Delete_commonlink(
			CommonLink   commonlink)
			{
		int commonlinkInt = 0;
		SqlSession sqlSession = null;

       try {
			sqlSession = DBAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			commonlinkInt = sqlSession.insert("commonlink.Delete_commonlink", commonlink);
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
			}
		}
		return commonlinkInt;
	}
	
	
	
	public List<CommonLink> Select_commonlink(CommonLink commonlink){
		List<CommonLink> commonlinklist = new ArrayList<CommonLink>();
		SqlSession sqlSession = null;
	
		try {
			sqlSession = DBAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			commonlinklist = sqlSession.selectList("commonlink.Select_commonlink", commonlink);
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
			}
		}
		return commonlinklist;
	}

	
	
	/**
	 * 新增資訊
	 * @param CommonLink
	 */
	public int Insert_commonlink(
			CommonLink   commonlink)
			{
		int commonlinkInt = 0;
		SqlSession sqlSession = null;

       try {
			sqlSession = DBAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			commonlinkInt = sqlSession.insert("commonlink.Insert_commonlink", commonlink);
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
			}
		}
		return commonlinkInt;
	}
 	
	/**
	 * 更新個人資訊
	 * @param Update_commonlink
	 */
	public int Update_commonlink(
			CommonLink   commonlink	){
		int commonlinkInt = 0;
		SqlSession sqlSession = null;

		try {
			sqlSession = DBAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			commonlinkInt = sqlSession.update("commonlink.Update_commonlink", commonlink);
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
			}
		}
		return commonlinkInt;
	}
	
	
	
}
