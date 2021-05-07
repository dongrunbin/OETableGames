package com.oegame.tablegames.service.player;

import java.util.List;

public interface PlayerService
{
	public Player auth(long passportId, String token, int serverId);
	
	public void logout(long playerId);
	
	public Player getPlayer(long playerId);
	
	public void addCards(long playerId, int cards, int type, String description);
	
	public void setRoomId(long playerId, int roomId, int gameId);
	
	public void addGold(long playerId, int gold);
	
	public List<Player> getPlayers(int roomId);
}
