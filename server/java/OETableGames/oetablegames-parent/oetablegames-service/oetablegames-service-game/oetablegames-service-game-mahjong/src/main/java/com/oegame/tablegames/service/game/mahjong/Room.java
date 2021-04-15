package com.oegame.tablegames.service.game.mahjong;

import com.oegame.tablegames.service.game.RoomBase;
import com.oegame.tablegames.service.game.SeatBase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.oegame.tablegames.service.game.mahjong.model.FightSet;
import com.oegame.tablegames.service.game.mahjong.model.MahjongGameStatus;
import com.oegame.tablegames.service.game.mahjong.model.Poker;
import com.oegame.tablegames.service.game.mahjong.model.PokerGroup;
import com.oegame.tablegames.service.game.mahjong.model.RoomSetting;
import com.oegame.tablegames.service.game.mahjong.model.Seat;

public class Room extends RoomBase
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 房间内的牌
	protected ArrayList<Poker> poker = new ArrayList<Poker>();

	//判断跟庄
	protected ArrayList<Poker> first = new ArrayList<Poker>();

	// 座位信息
	public HashMap<Integer, Seat> seat = new HashMap<Integer, Seat>();

	protected Poker luckPoker = null;
	
	public MahjongGameStatus gameStatus = MahjongGameStatus.ROOM_STATUS_READY;

	// 下一个要出牌的位置
	protected int nextPos = 1;

	// 上一家的位置
	public int leftSeatPos = 0;

	// 上一家的牌
	public Poker leftPoker = null;

	//总牌的数量
	protected int pokerTotal = 0;

	//第一个摇骰子的
	protected int firstDicePos = 0;
	protected int firstDiceA = 0;
	protected int firstDiceB = 0;
	//第二个摇骰子的
	protected int secondDicePos = 0;
	protected int secondDiceA = 0;
	protected int secondDiceB = 0;
	
	// 点炮人
	protected int paoshou = 0;

	// 杠上炮
	public int gangshangpao = 0;

	//fight集合，吃，碰，杠，胡
	protected transient HashMap<Integer, FightSet> fightSet = new HashMap<Integer, FightSet>();

	//房间设置
	public RoomSetting roomSetting = null;

	//当前第几手
	protected int handCount = 0;

	//补杠资料
	public PokerGroup buGang = null;

	protected boolean _isHit = false;
	protected Seat _seat = null;
	protected PokerGroup _pb_poker_group = null;
	protected int _gpos = 0;

	public Room(List<Integer> settingIds, int battleId, int roomId, long ownerId)
	{
		super(settingIds,roomId,ownerId);
		
		this.roomSetting = new RoomSetting(settingIds);

		for (int i = 1; i <= this.roomSetting.player; i++) {
			this.seat.put(i, new Seat(i));
		}

	}

	@Override
	public RoomSetting getRoomSetting()
	{
		return this.roomSetting;
	}

	@Override
	public Seat getSeatByPos(int pos)
	{
		return this.seat.get(pos);
	}

	@Override
	public Seat getSeatByPlayerId(long playerId)
	{
		if(this.seat == null) return null;
		for(Entry<Integer, Seat> entry : this.seat.entrySet())
		{
			if(entry.getValue().playerId == playerId)
			{
				return entry.getValue();
			}
		}
		return null;
	}

	@Override
	public Map<Integer, SeatBase> getSeats()
	{
		Map<Integer, SeatBase> ret = new HashMap<Integer, SeatBase>();
		for(Entry<Integer, Seat> entry : this.seat.entrySet())
		{
			ret.put(entry.getKey(), entry.getValue());
		}
		return ret;
	}
	
	public int getHanCount() {
		return this.handCount;	
	}
}
