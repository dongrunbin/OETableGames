package com.oegame.tablegames.service.player;

import java.io.Serializable;

public class Player implements Serializable
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public long playerId;
	public String nickname = "nickname";
	public String avatar = "avatar";
	public byte gender;
	public int gameId;
	public int roomId;
	public int gold;
	public int cards;
	public long online = 0;
	public boolean isRobot;
	
	public Player()
	{
		
	}

	public Player(long playerId, String nickname, String avatar, byte gender, int roomId, long online, int cards, int gameId, int gold)
	{
		this.playerId = playerId;
		this.nickname = nickname;
		this.avatar = avatar;
		this.gender = gender;
		this.roomId = roomId;
		this.online = online;
		this.cards = cards;
		this.gameId = gameId;
		this.gold = gold;
	}
}
