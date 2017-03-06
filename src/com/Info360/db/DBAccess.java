package com.Info360.db;

import java.io.IOException;
import java.io.Reader;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

import util.Util;

/**
 * 訪問數據庫類
 * 
 * @author Lin
 */
public class DBAccess {
	private static SqlSessionFactory instance = null;
	public static AtomicInteger sessonCount = new AtomicInteger(0);
	public static SqlSession getSqlSession() throws IOException {
		SqlSessionFactory sqlSessionFactory = DBAccess.getFactoryInstance();
		// 通過SqlSessionFactory打開一個數據庫會話
		SqlSession sqlSession = sqlSessionFactory.openSession();
		DBAccess.sessonCount.incrementAndGet();
		Util.getFileLogger().debug("DB session count: " + DBAccess.sessonCount.get());
		
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
				Util.getFileLogger().info("SqlSessionFactoryBuilder created.");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return instance;
	}
}
