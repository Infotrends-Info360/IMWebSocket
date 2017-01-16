package com.Info360.db;

import java.io.IOException;
import java.io.Reader;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

/**
 * 訪問數據庫類
 * @author Lin
 */
public class DBAccess {
	public SqlSession getSqlSession() throws IOException{
		//通過配置文件獲取數據庫連接信息
		Reader reader = Resources.getResourceAsReader("com/Info360/config/Configuration.xml");
		//通過配置信息建構一個SqlSessionFactory
		SqlSessionFactory sqlSessionFactory = new SqlSessionFactoryBuilder().build(reader);
		//通過SqlSessionFactory打開一個數據庫會話
		SqlSession sqlSession = sqlSessionFactory.openSession();
		return sqlSession;
	}
}
