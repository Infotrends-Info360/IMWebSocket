package com.Info360.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import util.Util;

import com.Info360.bean.Activitydata;
import com.Info360.bean.CaseComments;
import com.Info360.bean.CommonLink;
import com.Info360.bean.Interaction;
import com.Info360.db.DBAccess;
import com.Info360.util.IsError;

/**
 * 和Message表相關的數據庫操作
 * @author Lin
 */
public class CaseCommentsDao {
	
	
	/**
	 * Insert
	 * @param Insert_casecomments
	 */
	public int Insert_casecomments(CaseComments casecomments){
		int casecommentsInt = 0;
		SqlSession sqlSession = null;
		
		try {
			sqlSession = DBAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			casecommentsInt = sqlSession.insert("casecomments.Insert_casecomments", casecomments);
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
		return casecommentsInt;
	}
	
	
	/**
	 * 查詢
	 * "Select_IXN_casecomments"
	 * @param "Select_IXN_casecomments"
	 */
	public List<CaseComments> Select_IXN_casecomments(CaseComments casecomments){
		List<CaseComments> casecommentsList = new ArrayList<CaseComments>();
		SqlSession sqlSession = null;

		try {
			sqlSession = DBAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			casecommentsList = sqlSession.selectList("casecomments.Select_IXN_casecomments", casecomments);
			sqlSession.commit();
		} catch (IOException e) {
			// TODO Auto-generated catch block
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
		return casecommentsList;
	}
	
	
}
