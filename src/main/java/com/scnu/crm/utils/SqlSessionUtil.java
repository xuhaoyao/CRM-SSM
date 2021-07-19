package com.scnu.crm.utils;

import java.io.IOException;
import java.io.InputStream;

import org.apache.ibatis.io.Resources;
import org.apache.ibatis.session.SqlSession;
import org.apache.ibatis.session.SqlSessionFactory;
import org.apache.ibatis.session.SqlSessionFactoryBuilder;

public class SqlSessionUtil {
	
	private SqlSessionUtil(){}
	
	private static SqlSessionFactory factory;
	
	static{
		
		String resource = "conf/mybatis-config.xml";
		InputStream inputStream = null;
		try {
			inputStream = Resources.getResourceAsStream(resource);
		} catch (IOException e) {
			e.printStackTrace();
		}
		factory =
		 new SqlSessionFactoryBuilder().build(inputStream);
		
	}
	
	private static ThreadLocal<SqlSession> t = new ThreadLocal<SqlSession>();
	
	public static SqlSession getSqlSession(){
		
		SqlSession session = t.get();
		
		if(session==null){
			
			session = factory.openSession();
			t.set(session);
		}
		/*
			前端发送给后端的每一个请求（业务） 都是一条独立的线程,
			同一个业务下 session都是同一个引用 被存进了ThreadLocal里面保存起来
			因此dao层连接数据库的connection也是同一个引用
			因此动态代理临时开一个session是没问题的
			System.out.println(session);
			System.out.println(Thread.currentThread());
		 */
		return session;
		
	}
	
	public static void myClose(SqlSession session){
		
		if(session!=null){
			session.close();
			t.remove();
		}
		
	}
	
	
}
