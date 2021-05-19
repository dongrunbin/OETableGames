package com.oegame.tablegames.service.mahjong.model;

public class KongGroup
{
	
	/**
	 * 1 An Kong
	 * 2 Ming Kong
	 * 3 Bu Kong
	 */
	public int type;
	public int pos;

	public KongGroup(int typeId, int pos) {
		this.type = typeId;
		this.pos = pos;
	}
}
