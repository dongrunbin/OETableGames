package com.oegame.tablegames.service.game.mahjong.model;

import java.util.ArrayList;


public class PokerGroup {

	public CombinationType typeId = CombinationType.POKER_TYPE_NULL;

	public int subTypeId = 0;
	
	public ArrayList<Integer> index = new ArrayList<Integer>();

	public ArrayList<Poker> poker = new ArrayList<Poker>();

	public long playerId = 0;

	public int times = 0;

	public PokerGroup() {

	}

	public PokerGroup(CombinationType typeId, Poker poker) {
		this.typeId = typeId;
		this.poker.add(poker);
	}

	public PokerGroup(CombinationType typeId, int subTypeId, Poker poker) {
		this.typeId = typeId;
		this.subTypeId = subTypeId;
		this.poker.add(poker);
	}

	public PokerGroup( int times, Poker poker) {
		this.times = times;
		if (poker != null) {
			this.poker.add(poker);
		}
	}

	final public PokerGroup clone() {
		PokerGroup clone = new PokerGroup();
		clone.playerId = this.playerId;
		clone.times = this.times;
		for (int i = 0; i < this.poker.size(); i++) {
			clone.poker.add(this.poker.get(i));
		}
		return clone;
	}
}

