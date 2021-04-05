package com.oegame.tablegames.service.proxy;

public interface ProxyService
{
	public void sendMessage(long playerId, byte[] data);
	
	public void sendError(long playerId, int errorCode, String message);
}
