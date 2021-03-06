package com.oegame.tablegames.service.game;

import com.oegame.tablegames.common.log.Logger;
import com.oegame.tablegames.common.log.LoggerFactory;
import com.oegame.tablegames.service.ServiceUtil;
import java.util.ArrayList;
import java.util.List;
import com.oegame.tablegames.service.player.Player;

public abstract class GameServiceImpl implements GameService
{
	private static final Logger logger = LoggerFactory.getLogger(GameServiceImpl.class);
	
	protected abstract String getGameType();
	
	protected abstract RoomCtrlBase getRoom(int roomId);
	
	protected abstract ArrayList<Integer> getAllRoom();
	
	protected abstract void addRoom(int roomId, List<Integer> settingIds, long ownerId);
	
	protected abstract void removeRoom(int roomId);
	
	protected abstract int getRoomCount();
	
	protected abstract int getMaxRoomCount();
	
	public RoomBase getRoomInfo(int roomId)
	{
		RoomCtrlBase ctrl = getRoom(roomId);
		if(ctrl == null)
		{
			return null;
		}
		
		return ctrl.getRoom();
	}
	
	public void create(int roomId, long playerId, List<Integer> setting)
	{
		Player player = ServiceUtil.getPlayerService().getPlayer(playerId);
		if (player == null) return;
		
		if (this.getRoomCount() >= this.getMaxRoomCount())
		{
			ServiceUtil.getProxyService().sendError(player.playerId, -1, "room count > max room count");
			return;
		}
		if (setting.size() <= 0)
		{
			ServiceUtil.getProxyService().sendError(player.playerId, -1, "Room setting error");
			return;
		}

		if (this.getAllRoom().contains(roomId))
		{
			ServiceUtil.getProxyService().sendError(playerId, -1, "already exist room" + roomId);
			return;
		}

		this.addRoom(roomId, setting, playerId);
		RoomCtrlBase roomCtrl = this.getRoom(roomId);
		logger.info("create room "+roomId);
		RoomSettingBase roomSetting = roomCtrl.getRoom().getRoomSetting();

		if (roomSetting.cost > ServiceUtil.getPlayerService().getPlayer(playerId).gold)
		{
			ServiceUtil.getProxyService().sendError(playerId, -1, "cards not enough");
			return;
		}

		if (roomCtrl != null)
		{
			roomCtrl.getRoom().baseScore = roomCtrl.getRoom().getRoomSetting().baseScore;
			ServiceUtil.getPlayerService().addGold(playerId, -roomCtrl.getRoom().getRoomSetting().cost);
			this.enter(roomId, player);
		}
	}
	
	@Override
	public boolean enter(int roomId, Player player)
	{
		if(player == null) return false;
		RoomCtrlBase room = this.getRoom(roomId);
		if(room == null)
		{
			ServiceUtil.getProxyService().sendError(player.playerId, -1, "room not exists");
			return false;
		}
		return room.enter(player, 0);
	}
	
	@Override
	public boolean enterRobot(int roomId, int pos)
	{
		RoomCtrlBase room = this.getRoom(roomId);
		if(room == null) return false;
		Player player = new Player();
		player.isRobot = true;
		player.nickname = "robot" + pos;
		player.playerId = -pos;
		return room.enter(player, pos);
	}

	@Override
	public void leave(int roomId, long playerId)
	{
		RoomCtrlBase room = this.getRoom(roomId);
		if(room == null) return;

		room.leave(playerId);
	}

	@Override
	public void ready(int roomId, long playerId)
	{
		RoomCtrlBase room = this.getRoom(roomId);
		if(room == null) return;

		room.ready(playerId);
	}
	
	@Override
	public void roomInfo(int roomId, long playerId)
	{
		RoomCtrlBase room = this.getRoom(roomId);
		if(room == null)
		{
			return;
		}
		room.sendRoomInfo(playerId);
	}

	@Override
	public void disbandApply(int roomId, long playerId, byte status)
	{

	}
	
	@Override
	public boolean disband(int roomId, long playerId)
	{
		RoomCtrlBase room = this.getRoom(roomId);
		if(room == null) return false;
		
		room.disband();
		this.removeRoom(roomId);
		
		return true;
	}
	
	@Override
	public void message(int roomId, long playerId, byte messageType ,byte[] message, long toPlayerId)
	{
		RoomCtrlBase room = this.getRoom(roomId);
		if(room == null) return;
		
		room.message(playerId, messageType, message, toPlayerId);
	}
	
	public void roomResult(int roomId)
	{
		RoomCtrlBase room = this.getRoom(roomId);
		if(room == null) return;
		room.onResult();
		this.removeRoom(roomId);
	}
}
