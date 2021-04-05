package com.oegame.tablegames.common.mq;


import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageProducer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Provider 
{
	 //ActiveMq 的默认用户名
    private static final String USERNAME = ActiveMQConnection.DEFAULT_USER;
    //ActiveMq 的默认登录密码
    private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
    //ActiveMQ 的链接地址
    private static final String BROKEN_URL = "tcp://lanserver:61616";
    //链接工厂
    private ConnectionFactory connectionFactory;
    //链接对象
    private Connection connection;
    //事务管理
    private Session session;
    private ThreadLocal<MessageProducer> threadLocal = new ThreadLocal<MessageProducer>();
    
    private Queue queue = null;
    
    private Topic topic = null;
    
    public void initQueue(String queueName)
    {
    	initQueue(USERNAME,PASSWORD,BROKEN_URL,queueName);
    }

    
    public void initQueue(String username, String password, String url, String queueName)
    {
        try 
        {
            //创建一个链接工厂
            connectionFactory = new ActiveMQConnectionFactory(username,password,url);
            //从工厂中创建一个链接
            connection  = connectionFactory.createConnection();
            //开启链接
            connection.start();
            //创建一个事务（这里通过参数可以设置事务的级别）
            session = connection.createSession(true,Session.SESSION_TRANSACTED);
            //创建一个消息队列
            queue = session.createQueue(queueName);
        } 
        catch (JMSException e) 
        {
            e.printStackTrace();
        }
    }
    
    public void initTopic(String topicName)
    {
    	initTopic(USERNAME,PASSWORD,BROKEN_URL,topicName);
    }
    
    public void initTopic(String username, String password, String url, String topicName)
    {
        try 
        {
            //创建一个链接工厂
            connectionFactory = new ActiveMQConnectionFactory(username,password,url);
            //从工厂中创建一个链接
            connection  = connectionFactory.createConnection();
            //开启链接
            connection.start();
            //创建一个事务（这里通过参数可以设置事务的级别）
            session = connection.createSession(true,Session.SESSION_TRANSACTED);
            //创建一个消息队列
            topic = session.createTopic(topicName);
        } 
        catch (JMSException e) 
        {
            e.printStackTrace();
        }
    }

    public void sendQueue(String message)
    {
        try 
        {
            MessageProducer messageProducer = null;
            if(threadLocal.get()!=null)
            {
                messageProducer = threadLocal.get();
            }
            else
            {
                messageProducer = session.createProducer(queue);
                threadLocal.set(messageProducer);
            }
            TextMessage msg = session.createTextMessage(message);
            messageProducer.send(msg);
            session.commit();
        } 
        catch (JMSException e) 
        {
            e.printStackTrace();
        }
    }
	
    public void sendQueue(byte[] data)
    {
    	if(data == null) return;
        try 
        {
            MessageProducer messageProducer = null;
            if(threadLocal.get()!=null)
            {
                messageProducer = threadLocal.get();
            }
            else
            {
                messageProducer = session.createProducer(queue);
                threadLocal.set(messageProducer);
            }
            BytesMessage msg = session.createBytesMessage();
            msg.writeBytes(data);
            messageProducer.send(msg);
            session.commit();
        } 
        catch (JMSException e) 
        {
            e.printStackTrace();
        }
    }
	
	public void sendTopic(String message)
	{
        try 
        {
            MessageProducer messageProducer = null;
            if(threadLocal.get()!=null)
            {
                messageProducer = threadLocal.get();
            }
            else
            {
                messageProducer = session.createProducer(topic);
                threadLocal.set(messageProducer);
            }
            TextMessage msg = session.createTextMessage(message);
            messageProducer.send(msg);
            session.commit();
        } 
        catch (JMSException e) 
        {
            e.printStackTrace();
        }
	}
	
    public void sendTopic(byte[] data)
    {
    	if(data == null) return;
        try 
        {
            MessageProducer messageProducer = null;
            if(threadLocal.get()!=null)
            {
                messageProducer = threadLocal.get();
            }
            else
            {
                messageProducer = session.createProducer(topic);
                threadLocal.set(messageProducer);
            }
            BytesMessage msg = session.createBytesMessage();
            msg.writeBytes(data);
            messageProducer.send(msg);
            session.commit();
        } 
        catch (JMSException e) 
        {
            e.printStackTrace();
        }
    }
}
