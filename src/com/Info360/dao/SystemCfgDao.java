package com.Info360.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import util.Util;

import com.Info360.bean.SystemCfg;
import com.Info360.db.DBAccess;
import com.Info360.util.IsError;

public class SystemCfgDao {
	/**
	 * Select_SystemCfg
	 * @param Select_SystemCfg
	 */
	public List<SystemCfg> selectAll_SystemCfg(){
		List<SystemCfg> systemCfgList = new ArrayList<>();
		SqlSession sqlSession = null;
	
		try {
			sqlSession = DBAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			systemCfgList = sqlSession.selectList("systemCfg.SelectAll_SystemCfg");
//			systemCfgList = sqlSession.selectList("agentreason.Select_agentreason", aSystemCfg);
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
		return systemCfgList;
	}
}
