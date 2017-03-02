package com.Info360.db;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 * 訪問數據庫類
 * 
 * @author Lin
 */
public class DBAccess {
	private static SqlSessionFactory instance = null;
	public static SqlSession getSqlSession() throws IOException {
		SqlSessionFactory sqlSessionFactory = DBAccess.getFactoryInstance();
		// 通過SqlSessionFactory打開一個數據庫會話
		SqlSession sqlSession = sqlSessionFactory.openSession();
		return sqlSession;
	}
	protected DBAccess(){
		
	}
	
	public static SqlSessionFactory getFactoryInstance() {
		if (instance == null) {
			Reader reader;
			try {
				reader = Resources.getResourceAsReader("com/Info360/config/Configuration.xml");
				// 通過配置信息建構一個SqlSessionFactory
				instance = new SqlSessionFactoryBuilder().build(reader);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return instance;
	}
}
