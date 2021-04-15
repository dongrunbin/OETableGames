package com.oegame.tablegames.service.game.mahjong.model;

import java.util.ArrayList;

public class FightSet 
{

	public int cfgId = 0;

	public CombinationType typeId = CombinationType.POKER_TYPE_NULL;

	public ArrayList<Integer> index = new ArrayList<Integer>();

	public int playerId = 0;

	public ArrayList<PokerGroup> group = new ArrayList<PokerGroup>();

	public FightSet(ArrayList<PokerGroup> group) {
		this.group = group;
	}

	public PokerGroup select() {
		for (int i = 0; i < this.group.size(); i++) {
			if (this.group.get(i).typeId == this.typeId) {
				this.group.get(i).playerId = this.playerId;
				this.group.get(i).index = this.index;
				return this.group.get(i);
			}
		}
		return null;
	}

	public boolean isHu() {
		for (int i = 0; i < this.group.size(); i++) {
			if (this.group.get(i).typeId == CombinationType.POKER_TYPE_HU_PAO || this.group.get(i).typeId == CombinationType.POKER_TYPE_HU_ZIMO) {
				return true;
			}
		}
		return false;
	}

	public boolean isGang() {
		for (int i = 0; i < this.group.size(); i++) {
			if (this.group.get(i).typeId == CombinationType.POKER_TYPE_GANG) {
				return true;
			}
		}
		return false;
	}

	public boolean isPeng() {
		for (int i = 0; i < this.group.size(); i++) {
			if (this.group.get(i).typeId == CombinationType.POKER_TYPE_PENG) {
				return true;
			}
		}
		return false;
	}

	public boolean isChi() {
		for (int i = 0; i < this.group.size(); i++) {
			if (this.group.get(i).typeId == CombinationType.POKER_TYPE_CHI) {
				return true;
			}
		}
		return false;
	}
}

