package com.oegame.tablegames.service.game;

import java.util.List;
import com.oegame.tablegames.service.player.Player;

public interface GameService
{
	public RoomBase getRoomInfo(int roomId);
	
	public void create(int roomId, long playerId, List<Integer> setting);
	
	public boolean enter(int roomId, Player player);
	
	public boolean enterRobot(int roomId, int pos);
	
	public void leave(int roomId, long playerId);
	
	public void ready(int roomId, long playerId);
	
	public void roomInfo(int roomId, long playerId);
	
	public void message(int roomId, long playerId, byte messageType, byte[] message, long toPlayerId);
	
	public void disbandApply(int roomId, long playerId, byte status);
	
	public boolean disband(int roomId, long playerId);
	
	public void roomResult(int roomId);
}
