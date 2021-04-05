package com.oegame.tablegames.service.game;

import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.oegame.tablegames.model.local.LocalDBFacade;
import com.oegame.tablegames.model.local.gen.cfg_settingEntity;
import com.oegame.tablegames.service.player.Player;
import com.oegame.tablegames.service.ServiceUtil;

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

		for(Integer id : setting)
		{
			System.out.print(id);
			cfg_settingEntity entity = LocalDBFacade.getcfg_settingEntity(id);
			if (entity.precondition != null && !entity.precondition.equals("")) {
				String[] str = entity.precondition.split(";");
				ArrayList<Integer> set = new ArrayList<Integer>();
				if (str.length > 0)
				{
					for (int i = 0; i < str.length; i++)
					{
						if (!str[i].equals("") )
						{
							set.add(Integer.valueOf(str[i]).intValue());
						}
					}
				}
				if (!setting.containsAll(set))
				{
					ServiceUtil.getProxyService().sendError(playerId, -1, "room setting error");
					return;
				}
			}
		}

		this.addRoom(roomId, setting, playerId);
		RoomCtrlBase roomCtrl = this.getRoom(roomId);
		logger.info("create room "+roomId);
		RoomSettingBase roomSetting = roomCtrl.getRoom().getRoomSetting();

		if (roomSetting.cost > ServiceUtil.getPlayerService().getPlayer(playerId).cards)
		{
			ServiceUtil.getProxyService().sendError(playerId, -1, "cards not enough");
			return;
		}

		if (roomCtrl != null)
		{
			roomCtrl.getRoom().baseScore = roomCtrl.getRoom().getRoomSetting().baseScore;
			ServiceUtil.getPlayerService().addCards(playerId, 
					-roomCtrl.getRoom().getRoomSetting().cost, 1, "create room");
			this.enter(roomId, player, 0);
		}
	}
	
	@Override
	public boolean enter(int roomId, Player player, int gold)
	{
		if(player == null) return false;
		RoomCtrlBase room = this.getRoom(roomId);
		if(room == null)
		{
			ServiceUtil.getProxyService().sendError(player.playerId, -1, "room not exists");
			return false;
		}
		return room.enter(player, gold);
	}
	
	@Override
	public boolean enterRobot(int roomId, Player player)
	{		
		if(player == null) return false;
		logger.info("add robot "+player.playerId);
		RoomCtrlBase room = this.getRoom(roomId);
		if(room == null) return false;

		player.isRobot = true;
		return room.enter(player, player.gold);
	}

	@Override
	public void leave(int roomId, long playerId)
	{
		RoomCtrlBase room = this.getRoom(roomId);
		if(room == null) return;

		room.leave(playerId, false);
	}

	@Override
	public void ready(int roomId, long playerId)
	{
		RoomCtrlBase room = this.getRoom(roomId);
		if(room == null) return;

		room.ready(playerId);
	}

	@Override
	public void unready(int roomId, long playerId)
	{
		RoomCtrlBase room = this.getRoom(roomId);
		if(room == null) return;

		room.unready(playerId);
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
	public void afk(int roomId, long playerId, boolean isAfk)
	{
		RoomCtrlBase room = this.getRoom(roomId);
		if(room == null) return;
		
		room.afk(playerId, isAfk);
	}
	
	@Override
	public void disbandApply(int roomId, long playerId, DisbandStatus status)
	{
		RoomCtrlBase room = this.getRoom(roomId);
		if(room == null) return;
		
		room.disbandApply(playerId, status);
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
