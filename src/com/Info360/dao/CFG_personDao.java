package com.Info360.dao;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.ibatis.session.SqlSession;

import com.Info360.bean.CFG_person;
import com.Info360.db.DBAccess;
import com.Info360.util.IsError;

/**
 * 和Message表相關的數據庫操作
 * @author Lin
 */
public class CFG_personDao {
	

	
	/**
	 * 查詢個人資訊或所有資訊
	 * DBID Query
	 * @param CFG_person
	 */
	public List<CFG_person> query_Person_DBID(CFG_person   cfg_person){
		List<CFG_person> cfg_personList = new ArrayList<CFG_person>();
		SqlSession sqlSession = null;
		
		
		try {
			sqlSession = DBAccess.getSqlSession();
			//通過sqlSession執行SQL語句
			cfg_personList = sqlSession.selectList("cfg_person.Query_PersonInfo_DBID", cfg_person);
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
		return cfg_personList;
	}
	
	
	
}
