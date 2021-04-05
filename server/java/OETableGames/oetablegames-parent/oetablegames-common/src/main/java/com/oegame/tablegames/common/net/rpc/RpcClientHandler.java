package com.oegame.tablegames.common.net.rpc;

import java.io.IOException;

import org.apache.mina.core.service.IoHandlerAdapter;
import org.apache.mina.core.session.IoSession;


public class RpcClientHandler extends IoHandlerAdapter
{
	
	private RpcEvent event;
	
	public RpcClientHandler(RpcEvent rpcEvent)
	{
		this.event = rpcEvent;
	}
	
	@Override
	public void messageReceived(IoSession session, Object message) throws Exception 
	{
		byte[] data = (byte[]) message;
		
		if(event != null)
		{
			event.onReceived(data);
		}
	}
	
	public interface RpcEvent
	{
		public void onReceived(byte[] data) throws IOException;
	}
}
