package com.oegame.tablegames.service.mahjong;

import com.oegame.tablegames.service.mahjong.model.Room;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import com.oegame.tablegames.service.mahjong.model.CombinationType;
import com.oegame.tablegames.common.log.Logger;
import com.oegame.tablegames.common.log.LoggerFactory;
import com.oegame.tablegames.service.game.GameServiceImpl;

public class MahjongServiceImpl extends GameServiceImpl implements MahjongService
{
	
	private static Logger logger = LoggerFactory.getLogger(MahjongServiceImpl.class);
	
	private static final int MAX_ROOM_COUNT = 1000;
	
	private ConcurrentHashMap<Integer, RoomCtrl> room = new ConcurrentHashMap<Integer, RoomCtrl>();
	
	public MahjongServiceImpl()
	{
		super();
	}
	
	@Override
	protected String getGameType()
	{
		return "com/oegame/tablegames/service/mahjong";
	}
	
	@Override
	protected RoomCtrl getRoom(int roomId)
	{
		return room.get(roomId);
	}
	
	@Override
	protected ArrayList<Integer> getAllRoom() {
		ArrayList<Integer> all = new ArrayList<>();
		for(Entry<Integer, RoomCtrl> vo : room.entrySet()){ 
			all.add(vo.getKey());
		}
		return all;
	}

	@Override
	protected void addRoom(int roomId, List<Integer> settingIds, long ownerId)
	{
		Room room = new Room(settingIds, roomId, ownerId);
		RoomCtrl ctrl = new RoomCtrl(room,this);
		this.room.put(roomId, ctrl);
	}

	@Override
	protected int getRoomCount()
	{
		return this.room.size();
	}

	@Override
	protected int getMaxRoomCount()
	{
		return MAX_ROOM_COUNT;
	}

	@Override
	protected void removeRoom(int roomId)
	{
		if(!this.room.containsKey(roomId)) return;
		this.room.remove(roomId);
	}
	
	//出牌
	public void discard(int roomId,long playerId, int index)
	{
		if (!this.room.containsKey(roomId))
		{
			return;
		}
		RoomCtrl room = this.getRoom(roomId);
		synchronized (room.getRoom().LOCK)
		{
			room.discard(playerId, index);
		}
	}
	
	public void pass(int roomId,long playerId)
	{
		if (!this.room.containsKey(roomId))
		{
			return;
		}
		
		RoomCtrl room = this.getRoom(roomId);
		room.pass(playerId);
	}

	@Override
	public void operate(int roomId,long playerId, byte typeId, ArrayList<Integer> index)
	{
		if (!this.room.containsKey(roomId))
		{
			return;
		}
		RoomCtrl room = this.getRoom(roomId);
		room.operate(playerId, CombinationType.values()[typeId], index);
	}
}
