package com.oegame.tablegames.common.mq;


import javax.jms.BytesMessage;
import javax.jms.Connection;
import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.MessageConsumer;
import javax.jms.Queue;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.jms.Topic;

import org.apache.activemq.ActiveMQConnection;
import org.apache.activemq.ActiveMQConnectionFactory;

public class Consumer
{
	
	 //ActiveMq 的默认用户名
    private static final String USERNAME = ActiveMQConnection.DEFAULT_USER;
    //ActiveMq 的默认登录密码
    private static final String PASSWORD = ActiveMQConnection.DEFAULT_PASSWORD;
    //ActiveMQ 的链接地址
    private static final String BROKEN_URL = "tcp://lanserver:61616";

    private ConnectionFactory connectionFactory;

    private Connection connection;

    private Session session;

    private ThreadLocal<MessageConsumer> threadLocal = new ThreadLocal<MessageConsumer>();
    
    private Queue queue = null;
    
    private Topic topic = null;
    
    public void initQueue(String queueName)
    {
    	initQueue(USERNAME,PASSWORD,BROKEN_URL,queueName);
    }

    public void initQueue(String username, String pwd, String brokenUrl, String queueName)
    {
        try 
        {
            connectionFactory = new ActiveMQConnectionFactory(username,pwd,brokenUrl);
            connection  = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
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

    public void initTopic(String username, String pwd, String brokenUrl, String topicName)
    {
        try 
        {
            connectionFactory = new ActiveMQConnectionFactory(username,pwd,brokenUrl);
            connection  = connectionFactory.createConnection();
            connection.start();
            session = connection.createSession(false,Session.AUTO_ACKNOWLEDGE);
            topic = session.createTopic(topicName);
        } 
        catch (JMSException e) 
        {
            e.printStackTrace();
        }
    }


    public byte[] getQueueBytes()
    {
        try 
        {
            MessageConsumer consumer = null;
            if(threadLocal.get()!=null)
            {
                consumer = threadLocal.get();
            }
            else
            {
                consumer = session.createConsumer(queue);
                threadLocal.set(consumer);
            }
            BytesMessage msg = (BytesMessage)consumer.receive();
            if(msg!=null) 
            {
                msg.acknowledge();
                byte[] bytes = new byte[(int)msg.getBodyLength()];
                msg.readBytes(bytes);
                return bytes;
            }
        } 
        catch (JMSException e) 
        {
            e.printStackTrace();
        }
        return null;
    }
    
    public byte[] getTopicBytes()
    {
        try 
        {
            MessageConsumer consumer = null;
            if(threadLocal.get()!=null)
            {
                consumer = threadLocal.get();
            }
            else
            {
                consumer = session.createConsumer(topic);
                threadLocal.set(consumer);
            }
            BytesMessage msg = (BytesMessage)consumer.receive();
            if(msg!=null) 
            {
                msg.acknowledge();
                byte[] bytes = new byte[(int)msg.getBodyLength()];
                msg.readBytes(bytes);
                return bytes;
            }
        } 
        catch (JMSException e) 
        {
            e.printStackTrace();
        }
        return null;
    }
	
    public String getQueueText()
    {
        try 
        {
            MessageConsumer consumer = null;
            if(threadLocal.get()!=null)
            {
                consumer = threadLocal.get();
            }
            else
            {
                consumer = session.createConsumer(queue);
                threadLocal.set(consumer);
            }
            TextMessage msg = (TextMessage)consumer.receive();
            if(msg!=null) 
            {
                msg.acknowledge();
                return msg.getText();
            }
        } 
        catch (JMSException e) 
        {
            e.printStackTrace();
        }
        return "";
    }
    
    public String getTopicText()
    {
        try 
        {
            MessageConsumer consumer = null;
            if(threadLocal.get()!=null)
            {
                consumer = threadLocal.get();
            }
            else
            {
                consumer = session.createConsumer(topic);
                threadLocal.set(consumer);
            }
            TextMessage msg = (TextMessage)consumer.receive();
            if(msg!=null) 
            {
                msg.acknowledge();
                return msg.getText();
            }
        } 
        catch (JMSException e) 
        {
            e.printStackTrace();
        }
        return "";
    }
}
