package com.oegame.tablegames.service.mahjong.model;

public enum MahjongSeatStatus {

	/** 
	 * 空闲，新一局开始等待 
	 */ 
	SEAT_STATUS_IDLE,
	
	/** 
	 * 准备，新一局开始准备
	 */ 
	SEAT_STATUS_READY,
	
	/** 
	 * 出牌，自己操作出牌 
	 */ 
	SEAT_STATUS_OPERATE,
	
	/** 
	 * 等待，牌局中，等待别人出牌 
	 */ 
	SEAT_STATUS_WAIT,
	
	/** 
	 * 是否吃，碰，杠，胡，自摸 
	 */ 
	SEAT_STATUS_FIGHT;
}
