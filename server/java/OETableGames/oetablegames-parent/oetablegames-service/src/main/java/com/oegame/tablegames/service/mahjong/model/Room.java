package com.oegame.tablegames.service.mahjong.model;

import com.oegame.tablegames.service.game.RoomBase;
import com.oegame.tablegames.service.game.SeatBase;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class Room extends RoomBase
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	// 房间内的牌
	public ArrayList<Mahjong> mahjong = new ArrayList<Mahjong>();

	// 座位信息
	public HashMap<Integer, Seat> seat = new HashMap<Integer, Seat>();
	
	public MahjongGameStatus gameStatus = MahjongGameStatus.ROOM_STATUS_READY;

	// 下一个要出牌的位置
	public int nextPos = 1;

	// 上一家的位置
	public int leftSeatPos = 0;

	// 上一家的牌
	public Mahjong leftMahjong = null;

	//总牌的数量
	public int mahjongTotal = 0;

	//第一个摇骰子的
	public int firstDicePos = 0;
	public int firstDiceA = 0;
	public int firstDiceB = 0;
	//第二个摇骰子的
	public int secondDicePos = 0;
	public int secondDiceA = 0;
	public int secondDiceB = 0;
	
	// 点炮人
	public int paoshou = 0;

	// 杠上炮
	public int gangshangpao = 0;

	//fight集合，吃，碰，杠，胡
	public transient HashMap<Integer, FightSet> fightSet = new HashMap<Integer, FightSet>();

	//房间设置
	public RoomSetting roomSetting = null;

	//当前第几手
	public int handCount = 0;

	//补杠资料
	public MahjongGroup buGang = null;

	public boolean _isHit = false;
	public Seat _seat = null;
	public MahjongGroup _pb_mahjong_group = null;
	public int _gpos = 0;

	public Room(List<Integer> settingIds, int roomId, long ownerId)
	{
		super(settingIds,roomId,ownerId);

		this.gameId = 1;
		
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
