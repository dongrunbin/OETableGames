package com.oegame.tablegames.service.proxy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.mina.core.session.IoSession;

public class ClientManager
{
	private static final int HEART_TIME_OUT = 30000;
	
	private Map<Long, ClientRole> players = new ConcurrentHashMap<Long, ClientRole>();
	
	private static volatile ClientManager instance = null;
	
	public static ClientManager getInstance()
	{
		if(instance == null)
		{
			synchronized(ClientManager.class)
			{
				if(instance == null)
				{
					instance = new ClientManager();
				}
			}
		}
		return instance;
	}
	
	public void addPlayer(long playerId, IoSession session)
	{
		ClientRole client = new ClientRole(playerId, session);
		players.put(playerId, client);
	}
	
	public ClientRole getPlayer(long playerId)
	{
		return players.get(playerId);
	}
	
	public boolean containsPlayer(long playerId)
	{
		return players.containsKey(playerId);
	}
	
	public void removePlayer(long playerId)
	{
		if(!containsPlayer(playerId)) return;
		players.get(playerId).getSession().removeAttribute("TokenMark");
		players.get(playerId).getSession().removeAttribute("playerId");
		players.get(playerId).getSession().closeNow();
		players.remove(playerId);
	}
}
