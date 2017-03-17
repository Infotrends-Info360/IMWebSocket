package com.Info360.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import util.Util;

import com.Info360.bean.CaseComments;
import com.Info360.bean.Cfg_CaseStatus;
import com.Info360.bean.CommonLink;
import com.Info360.bean.Interaction;
import com.Info360.db.DBAccess;
import com.Info360.util.IsError;

/**
 * 和Message表相關的數據庫操作
 * @author Lin
 */
public class Cfg_CaseStatusDao {
	
	/**
	 * 查詢
	 * Cfg_CaseStatus
	 * @param Cfg_CaseStatus
	 */
	public List<Cfg_CaseStatus> Select_IXN_cfg_casestatus(Cfg_CaseStatus cfg_casestatus){
		List<Cfg_CaseStatus> cfg_casestatuslist = new ArrayList<Cfg_CaseStatus>();
		SqlSession sqlSession = null;

		try {
			sqlSession = DBAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			cfg_casestatuslist = sqlSession.selectList("cfg_casestatus.Select_IXN_cfg_casestatus", cfg_casestatus);
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
		return cfg_casestatuslist;
	}
	
	
}
