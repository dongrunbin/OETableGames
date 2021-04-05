package com.zhenyi.remoting.test;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.zhenyi.remoting.framework.serialization.common.SerializeType;
import com.zhenyi.remoting.framework.serialization.engine.SerializerEngine;


public class MainClient
{

	private static final Logger logger = LoggerFactory.getLogger(MainClient.class);
	
	private static int i = 0;
	public static void main(String[] args) throws Exception
	{

//		final ClassPathXmlApplicationContext context = new ClassPathXmlApplicationContext("test-client.xml");
		
		

//			new Thread(new Runnable() 
//			{
//				public void run()
//				{
					TestEntity test = new TestEntity();
					test.test = 1;
					test.test2 = "str" + 1;
					long time = System.currentTimeMillis();
					for(;i < 20000; ++i)
					{
						
						SerializerEngine.serialize(test, "ProtoStuffSerializer");
//						final HelloService helloService = (HelloService) context.getBean("testService");
						
//						String a = helloService.sayHello(test);
//						System.out.println(a);
					}
					System.out.println(System.currentTimeMillis() - time);
//				}
//			}).start();



		
		
		
		
		
//		final HelloService helloService = (HelloService) context.getBean("remoteHelloService");
//		long count = 10;
//		
//		final Hello2Service helloService2 = (Hello2Service) context.getBean("remoteHello2Service");
//		
//		for (int i = 0; i < count; i++)
//		{
//			try
//			{
//				String result = helloService.sayHello("binge,i=" + i);
//				System.out.println(result);
//			} 
//			catch (Exception e)
//			{
//				logger.warn("--------", e);
//			}
//		}
//		
//		for (int i = 0; i < count; i++)
//		{
//			try
//			{
//				String result = helloService2.sayYeah("binge2,i=" + i);
//				System.out.println(result);
//			} 
//			catch (Exception e)
//			{
//				e.printStackTrace();
//			}
//		}

//		System.exit(0);
	}
}
