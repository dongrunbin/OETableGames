package com.oegame.tablegames.service.mahjong.model;

public class GangGroup {
	
	/**
	 * 杠的类型
	 * 1暗杠
	 * 2明杠
	 * 3补杠
	 */
	public int type = 0;

	//被杠人
	public int pos = 0;

	public GangGroup() {

	}

	public GangGroup(int typeId, int pos) {
		this.type = typeId;
		this.pos = pos;
	}
}
