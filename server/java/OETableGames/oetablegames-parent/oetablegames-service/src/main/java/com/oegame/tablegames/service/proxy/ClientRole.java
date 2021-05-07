package com.oegame.tablegames.service.proxy;

import org.apache.mina.core.session.IoSession;

import com.oegame.tablegames.common.util.TimeUtil;

public class ClientRole
{
	private IoSession session;
	
	private long playerId;
	
	private long heartTime;
	
	
	
	public ClientRole(long playerId, IoSession session)
	{
		this.session = session;
		this.playerId = playerId;
		this.heartTime = TimeUtil.millisecond();
	}
	
	public IoSession getSession()
	{
		return this.session;
	}
	
	public long getPlayerId()
	{
		return this.playerId;
	}
}
