package com.oegame.tablegames.service.game.mahjong.model;

public enum MahjongGameStatus 
{
	
	/** 
	 * 准备中
	 */ 
	ROOM_STATUS_READY,
	
	/** 
	 * 进行中
	 */ 
	ROOM_STATUS_BEGIN,
	
	/** 
	 * 结算中
	 */ 
	ROOM_STATUS_SETTLE,
	
	/** 
	 * 发牌中 
	 */ 
	ROOM_STATUS_DEAL,
}
