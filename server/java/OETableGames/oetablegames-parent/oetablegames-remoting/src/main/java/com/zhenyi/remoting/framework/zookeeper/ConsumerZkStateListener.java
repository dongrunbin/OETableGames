package com.zhenyi.remoting.framework.zookeeper;

import org.I0Itec.zkclient.IZkStateListener;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConsumerZkStateListener implements IZkStateListener
{
	
	private static final Logger logger = LoggerFactory.getLogger(ConsumerZkStateListener.class);
	
	@Override
	public void handleStateChanged(KeeperState state) throws Exception
	{
		if(state == KeeperState.Disconnected)
		{
			logger.info("消费者与ZooKeeper失去连接");
		}
	}

	@Override
	public void handleNewSession() throws Exception
	{
		// TODO Auto-generated method stub
		
	}

}
