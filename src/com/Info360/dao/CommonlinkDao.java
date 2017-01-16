package com.Info360.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.Info360.bean.CommonLink;
import com.Info360.bean.ContactData;
import com.Info360.bean.Interaction;
import com.Info360.db.DBAccess;
import com.Info360.util.IsError;


public class CommonlinkDao {

	public List<CommonLink> Select_commonlink(CommonLink commonlink){
		DBAccess dbAccess = new DBAccess();
		List<CommonLink> commonlinklist = new ArrayList<CommonLink>();
		SqlSession sqlSession = null;
	
		try {
			sqlSession = dbAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			commonlinklist = sqlSession.selectList("commonlink.Select_commonlink", commonlink);
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
		return commonlinklist;
	}
//	
//	public List<Interaction> SelcetAll_interaction(Interaction interaction){
//		DBAccess dbAccess = new DBAccess();
//		List<Interaction> interactionList = new ArrayList<Interaction>();
//		SqlSession sqlSession = null;
//
//		try {
//			sqlSession = dbAccess.getSqlSession();
//			//通過sqlSession執行SQL語句
//			interactionList = sqlSession.selectList("interaction.SelcetAll_interaction", interaction);
//			sqlSession.commit();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//			IsError.GET_EXCEPTION = e.getMessage();
//		} finally {
//			if(sqlSession != null){
//			   sqlSession.close();
//			}
//		}
//		return interactionList;
//	}
//	
//	
//	/**
//	 * 新增資訊
//	 * @param Interaction
//	 */
//	public int Insert_interaction(
//			Interaction   interaction)
//			{
//		System.out.println("Dao="+interaction.getInteractionMap());
//		DBAccess dbAccess = new DBAccess();
//		int interactionInt = 0;
//		SqlSession sqlSession = null;
//
//		
//       try {
//			sqlSession = dbAccess.getSqlSession();
//			//通過sqlSession執行SQL語句
//			interactionInt = sqlSession.insert("interaction.Insert_interaction", interaction);
//			sqlSession.commit();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//			IsError.GET_EXCEPTION = e.getMessage();
//		} finally {
//			if(sqlSession != null){
//				sqlSession.close();
//			}
//		}
//		return interactionInt;
//	}
//	
//	
//	/**
//	 * 更新個人資訊
//	 * @param Update_contactdata
//	 */
//	public int Update_Interaction(
//			Interaction   interaction	){
//		DBAccess dbAccess = new DBAccess();
//		int interactionInt = 0;
//		SqlSession sqlSession = null;
//
//		try {
//			sqlSession = dbAccess.getSqlSession();
//			//通過sqlSession執行SQL語句
//			interactionInt = sqlSession.update("interaction.Update_Interaction", interaction);
//			sqlSession.commit();
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			//e.printStackTrace();
//			IsError.GET_EXCEPTION = e.getMessage();
//		} finally {
//			if(sqlSession != null){
//				sqlSession.close();
//			}
//		}
//		return interactionInt;
//	}
	
	
	
}
