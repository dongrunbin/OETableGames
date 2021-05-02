package com.oegame.tablegames.service.game.mahjong.model;

import java.util.ArrayList;


public class MahjongGroup {

	public CombinationType typeId = CombinationType.POKER_TYPE_NULL;

	public int subTypeId = 0;
	
	public ArrayList<Integer> index = new ArrayList<Integer>();

	public ArrayList<Mahjong> mahjong = new ArrayList<Mahjong>();

	public long playerId = 0;

	public int times = 0;

	public MahjongGroup() {

	}

	public MahjongGroup(CombinationType typeId, Mahjong mahjong) {
		this.typeId = typeId;
		this.mahjong.add(mahjong);
	}

	public MahjongGroup(CombinationType typeId, int subTypeId, Mahjong mahjong) {
		this.typeId = typeId;
		this.subTypeId = subTypeId;
		this.mahjong.add(mahjong);
	}

	public MahjongGroup( int times, Mahjong mahjong) {
		this.times = times;
		if (mahjong != null) {
			this.mahjong.add(mahjong);
		}
	}

	final public MahjongGroup clone() {
		MahjongGroup clone = new MahjongGroup();
		clone.playerId = this.playerId;
		clone.times = this.times;
		for (int i = 0; i < this.mahjong.size(); i++) {
			clone.mahjong.add(this.mahjong.get(i));
		}
		return clone;
	}
}

