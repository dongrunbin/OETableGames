package com.oegame.tablegames.common.mq;

import java.io.UnsupportedEncodingException;

public class MQTest 
{
	
	public static void main(String[] args) throws UnsupportedEncodingException
	{
		Provider producer = new Provider();
		producer.initTopic("testTopic");
		producer.sendTopic("哈哈哈哈".getBytes("utf-8"));
		
		
//		Comsumer comsumer = new Comsumer();
//		comsumer.initTopic("testTopic");
//		for(int i = 0; i < 1; ++i)
//		{
//			byte[] data = comsumer.getTopicBytes();
//			System.out.println(new String(data,"utf-8"));
//		}

	}

}
