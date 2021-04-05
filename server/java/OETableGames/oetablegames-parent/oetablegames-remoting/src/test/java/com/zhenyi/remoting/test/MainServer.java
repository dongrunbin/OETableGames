package com.zhenyi.remoting.test;

import org.springframework.context.support.ClassPathXmlApplicationContext;


public class MainServer
{

	public static void main(String[] args) throws Exception
	{

		final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("test-server.xml");
		System.out.println(" 服务发布完成");
		
		
//		System.out.println(HelloService.class.getMethods()[0].getClass().equals(HelloServiceImpl.class.getMethods()[0].getClass()));
	}
}
