package com.oegame.tablegames.service.mahjong.model;

import java.util.ArrayList;

public class FightSet 
{
	public CombinationType typeId = CombinationType.POKER_TYPE_NULL;
	public ArrayList<Integer> index = new ArrayList<Integer>();
	public int playerId = 0;
	public ArrayList<MahjongGroup> group;

	public FightSet(ArrayList<MahjongGroup> group) {
		this.group = group;
	}

	public MahjongGroup select() {
		for (int i = 0; i < this.group.size(); i++) {
			if (this.group.get(i).typeId == this.typeId) {
				this.group.get(i).playerId = this.playerId;
				this.group.get(i).index = this.index;
				return this.group.get(i);
			}
		}
		return null;
	}
}

