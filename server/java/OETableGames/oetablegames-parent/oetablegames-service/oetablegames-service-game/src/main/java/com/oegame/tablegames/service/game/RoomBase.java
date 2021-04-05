package com.oegame.tablegames.service.game;

import com.oegame.tablegames.service.player.Player;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.oegame.tablegames.common.util.TimeUtil;

public abstract class RoomBase implements Serializable
{
	private static final long serialVersionUID = 1L;

	public String LOCK = "";

	public long ownerId = 0;
	public int roomId = 0;
	public int gameId = 0;

	public ConcurrentHashMap<Long, Player> player = new ConcurrentHashMap<Long, Player>();
	public ArrayList<SeatBase> leavedPlayers = new ArrayList<SeatBase>();
	public HashMap<Integer, Long> costCard = new HashMap<Integer, Long>();
	public RoomStatus status = RoomStatus.ROOM_STATUS_READY;
	public int bankerPos = 1;
	public long beginTime = 0;
	public int loopCount = 1;
	public int baseScore = 1;
	public int baseTimes = 1;
	public List<Integer> settingIds = new ArrayList<Integer>();
	public long dismissTime = 0L;

	public RoomBase(List<Integer> settingIds, int roomId, long ownerId)
	{
		this.roomId = roomId;
		this.settingIds = settingIds;
		this.ownerId = ownerId;
		this.beginTime = TimeUtil.millisecond();
	}
	
	public abstract RoomSettingBase getRoomSetting();
	
	public abstract Map<Integer, SeatBase> getSeats();
	
	public int getPos(long playerId)
	{
		Map<Integer, SeatBase> seats = this.getSeats();
		if(seats == null) return 0;
		for(Entry<Integer, SeatBase> entry : seats.entrySet())
		{
			if(entry.getValue().playerId == playerId)
			{
				return entry.getValue().pos;
			}
		}
		return 0;
	}
	
	public SeatBase getSeatByPos(int pos)
	{
		Map<Integer, SeatBase> seats = this.getSeats();
		if(seats == null) return null;
		for(Entry<Integer, SeatBase> entry : seats.entrySet())
		{
			if(entry.getValue().pos == pos)
			{
				return entry.getValue();
			}
		}
		return null;
	}
	
	public SeatBase getSeatByPlayerId(long playerId)
	{
		Map<Integer, SeatBase> seats = this.getSeats();
		if(seats == null) return null;
		for(Entry<Integer, SeatBase> entry : seats.entrySet())
		{
			if(entry.getValue().playerId == playerId)
			{
				return entry.getValue();
			}
		}
		return null;
	}

	public int getPlayerCount()
	{
		return this.player.size();
	}

	public int getReadyCount()
	{
		int count = 0;
		Map<Integer, SeatBase> seats = this.getSeats();
		if(seats == null) return count;
		for(Entry<Integer, SeatBase> entry : seats.entrySet())
		{
			if(entry.getValue().status == SeatStatus.SEAT_STATUS_READY)
			{
				++count;
			}
		}
		return count;
	}

	public int getFreeSeat()
	{
		Map<Integer, SeatBase> seats = this.getSeats();
		if(seats == null) return 0;
		for(Entry<Integer, SeatBase> entry : seats.entrySet())
		{
			if(entry.getValue().playerId == 0)
			{
				return entry.getValue().pos;
			}
		}
		return 0;
	}

	public int getFreeSeatCount()
	{
		int count = 0;
		Map<Integer, SeatBase> seats = this.getSeats();
		if(seats == null) return count;
		for(Entry<Integer, SeatBase> entry : seats.entrySet())
		{
			if(entry.getValue().playerId == 0)
			{
				++count;
			}
		}
		return count;
	}

	public int playerNum() {
		int num = 0;
		Map<Integer, SeatBase> seats = this.getSeats();
		for (Entry<Integer, SeatBase> entry : seats.entrySet()) {
			if (entry.getValue().player != null && entry.getValue().playerId > 0) {
				num++;
			}
		}
		return num;
	}
	
	@Override
	public String toString()
	{
		return String.format("roomId:%d,ownerId:%d,setting:%s", roomId, ownerId,getRoomSetting().toString());
	}
}
