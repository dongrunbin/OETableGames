package com.oegame.tablegames.service.game;

import com.oegame.tablegames.common.log.Logger;
import com.oegame.tablegames.common.log.LoggerFactory;
import com.oegame.tablegames.protocol.gen.Game_S2C_DisbandProto;
import com.oegame.tablegames.protocol.gen.Game_S2C_EnterRoomProto;
import com.oegame.tablegames.protocol.gen.Game_S2C_InRoomProto;
import com.oegame.tablegames.protocol.gen.Game_S2C_LeaveRoomProto;
import com.oegame.tablegames.protocol.gen.Game_S2C_ReadyProto;
import com.oegame.tablegames.service.ServiceUtil;
import com.oegame.tablegames.service.mahjong.model.Seat;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import com.oegame.tablegames.common.util.TimeUtil;
import com.oegame.tablegames.service.player.Player;
import java.util.Map.Entry;

public abstract class RoomCtrlBase {

	private static Logger logger = LoggerFactory.getLogger(RoomCtrlBase.class);

	protected static final int MAX_DISBAND_TIME = 300 * 1000;

	protected static final int SEAT_READY_TIME = 15 * 1000;

	protected Timer timer = new Timer();

	protected IGameAI gameAI;

	private GameService gameService;
	
	protected String roominfo = "";

	protected abstract RoomBase getRoom();

	protected abstract void sendRoomInfo(long playerId);

	public RoomCtrlBase(GameService service, IGameAI ai)
	{
		this.gameService = service;

		if (ai != null)
		{
			this.gameAI = ai;
			// ai.setRoomCtrl(this);
			timer.schedule(new TimerTask() {
				public void run() {
					gameAI.doAI();
				}
			}, ai.getTimeSpan(), ai.getTimeSpan());
		}
	}

	protected GameService getGameService() {
		return this.gameService;
	}

	public synchronized boolean enter(Player player, int pos)
	{
		if (this.getRoom().player.containsKey(player.playerId))
		{
			return false;
		}
		if(pos != 0)
		{
			SeatBase seat = this.getRoom().getSeatByPos(pos);
			if(seat.playerId != 0)
			{
				return false;
			}
		}
		else
		{
			pos = this.getRoom().getFreeSeat();
		}
		if (pos != 0)
		{
			this.getRoom().player.put(player.playerId, player);
			if (!this.sitdown(player.playerId, pos, 0))
			{
				logger.info(this.roominfo+"坐下失败" + player.playerId);
				return false;
			}
			if (!player.isRobot)
			{
				ServiceUtil.getPlayerService().setRoomId(player.playerId, this.getRoom().roomId, this.getRoom().gameId);
			}
			Game_S2C_EnterRoomProto proto = new Game_S2C_EnterRoomProto();
			proto.setAvatar(player.avatar);
			proto.setGender(player.gender);
			proto.setGold(this.getRoom().getSeatByPos(pos).gold);
			proto.setNickname(player.nickname);
			proto.setOnline(player.online);
			proto.setPlayerId((int) player.playerId);
			proto.setPos(pos);
			proto.setOriginalGold(this.getRoom().getSeatByPos(pos).initGold);
			this.refreshAll(0, proto.toArray());

			Game_S2C_InRoomProto s2c = new Game_S2C_InRoomProto();
			s2c.setGameId(this.getRoom().gameId);
			ServiceUtil.getProxyService().sendMessage(player.playerId, s2c.toArray());
			return true;
		}
		else
		{
			ServiceUtil.getProxyService().sendError(player.playerId, -1, "房间已满");
			return false;
		}
	}

	public void leave(long playerId)
	{
		Player player = this.getRoom().player.get(playerId);
		if (player == null) return;
		int pos = this.getRoom().getPos(playerId);
		int gold = 0;
		if(this.getRoom().status == RoomStatus.ROOM_STATUS_BEGIN)
		{
			SeatBase seat = this.getRoom().getSeatByPos(pos);
			if (seat != null)
			{
				seat.player.isRobot = true;

				boolean existsPlayer = false;
				for(Entry<Integer, SeatBase> entry : this.getRoom().getSeats().entrySet())
				{
					if(entry.getValue().player != null && !entry.getValue().player.isRobot)
					{
						existsPlayer = true;
						break;
					}
				}
				if(!existsPlayer)
				{
					gameService.disband(this.getRoom().roomId, 0);
				}
			}
			ServiceUtil.getPlayerService().setRoomId(playerId, 0, 0);

			Game_S2C_LeaveRoomProto proto = new Game_S2C_LeaveRoomProto();
			proto.setPlayerId((int) playerId);
			ServiceUtil.getProxyService().sendMessage(seat.playerId, proto.toArray());
		}
		else
		{
			ServiceUtil.getPlayerService().setRoomId(playerId, 0, 0);

			Game_S2C_LeaveRoomProto proto = new Game_S2C_LeaveRoomProto();
			proto.setPlayerId((int) playerId);
			this.refreshAll(0, proto.toArray());

			SeatBase seat = this.getRoom().getSeatByPos(pos);
			if (seat != null)
			{
				gold = seat.gold - seat.initGold;
				if(gold != 0)
				{
					this.getRoom().leavedPlayers.add(seat.clone());
				}
				seat.clear();
			}

			this.getRoom().player.remove(playerId);
			if(this.getRoom().getPlayerCount() == 0)
			{
				gameService.disband(this.getRoom().roomId, 0);
			}
		}
	}

	public synchronized boolean sitdown(long playerId, int pos, int gold)
	{
		Player player = getRoom().player.get(playerId);
		if (this.getRoom().status != RoomStatus.ROOM_STATUS_READY)
		{
			return false;
		}
		if (!this.getRoom().player.containsKey(playerId))
		{
			return false;
		}
		SeatBase seat = this.getRoom().getSeatByPos(pos);
		if (seat.playerId != 0)
		{
			return false;
		}
		if (seat.playerId == playerId)
		{
			return false;
		}
		int exists = this.getRoom().getPos(playerId);
		if (exists != 0)
		{
			return false;
		}
		seat.playerId = (int) playerId;
		seat.status = SeatStatus.SEAT_STATUS_IDLE;
		seat.countdown = TimeUtil.millisecond() + SEAT_READY_TIME;
		seat.player = player;
		seat.gold = gold;
		seat.initGold = gold;
		return true;
	}

	public void ready(long playerId)
	{
		SeatBase seat = this.getRoom().getSeatByPlayerId(playerId);
		if (seat == null)
		{
			return;
		}
		if (seat.status != SeatStatus.SEAT_STATUS_IDLE)
			return;
		seat.status = SeatStatus.SEAT_STATUS_READY;

		Game_S2C_ReadyProto proto = new Game_S2C_ReadyProto();
		proto.setPlayerId((int) playerId);
		this.refreshAll(0, proto.toArray());

		if (this.getRoom().getReadyCount() >= this.getRoom().getSeats().size()
				&& this.getRoom().loopCount <= this.getRoom().getRoomSetting().loop)
		{
			this.onBegin();
		}
	}

	public void disband()
	{
		if (timer != null)
		{
			timer.cancel();
			timer = null;
		}

		Game_S2C_DisbandProto disbandProto = new Game_S2C_DisbandProto();
		disbandProto.setDisbandStatus((byte)DisbandStatus.DISSMISS_AGENT.ordinal());
		this.refreshAll(0, disbandProto.toArray());

		if (this.getRoom().loopCount <= 1)
		{
			ServiceUtil.getPlayerService().addCards(this.getRoom().ownerId, this.getRoom().getRoomSetting().cost, 1,"return room cost");

			for (Entry<Long, Player> entry : this.getRoom().player.entrySet())
			{
				Player _player = entry.getValue();
				if (_player != null && !_player.isRobot)
				{
					ServiceUtil.getPlayerService().setRoomId(_player.playerId, 0, 0);
				}
			}
		}

		int cost = 0;
		if (this.getRoom().loopCount <= 1)
		{
			cost = this.getRoom().getRoomSetting().cost;
		}
		logger.info(this.getRoom().roomId + "disband");
	}

	public void message(long playerId, byte messageType, byte[] message, long toPlayerId)
	{
		RoomBase room = this.getRoom();

		Player player = room.player.get(playerId);
		if (player == null)
			return;

//		S2C_Game_SendMessageProto proto = new S2C_Game_SendMessageProto();
//		proto.setMessageType(messageType);
//		proto.setMessage(message);
//		proto.setPlayerId((int) playerId);
//		proto.setToPlayerId((int) toPlayerId);
//		refreshAll(0, proto.toArray());
	}

	// 开局
	protected void onBegin()
	{
		this.getRoom().status = RoomStatus.ROOM_STATUS_BEGIN;
		for (Entry<Integer, SeatBase> entry : this.getRoom().getSeats().entrySet())
		{
			entry.getValue().status = SeatStatus.SEAT_STATUS_GAMING;
		}
	}

	protected void settle()
	{
		this.getRoom().status = RoomStatus.ROOM_STATUS_READY;
	}

	public void refreshAll(long playerId, byte[] data)
	{
		Set<Entry<Long, Player>> set = this.getRoom().player.entrySet();
		for (Entry<Long, Player> entry : set)
		{
			if (playerId != entry.getKey())
			{
				if(entry.getValue().isRobot) continue;
				if (entry.getValue() != null && this.getRoom().getSeatByPlayerId(entry.getValue().playerId) != null)
				{
					Player toPlayer = entry.getValue();
					ServiceUtil.getProxyService().sendMessage(toPlayer.playerId, data);
				}
			}
		}
	}

	protected void onResult()
	{
		if (timer != null)
		{
			timer.cancel();
			timer = null;
		}
		for (Entry<Long, Player> entry : this.getRoom().player.entrySet())
		{
			Player _player = entry.getValue();
			if (_player != null && !_player.isRobot)
			{
				ServiceUtil.getPlayerService().setRoomId(_player.playerId, 0, 0);
			}
		}
	}
}
