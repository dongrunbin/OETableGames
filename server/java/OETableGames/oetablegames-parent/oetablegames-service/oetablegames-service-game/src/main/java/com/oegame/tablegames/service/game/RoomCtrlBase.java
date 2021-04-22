package com.oegame.tablegames.service.game;

import com.oegame.tablegames.protocol.gen.Game_S2C_AFKProto;
import com.oegame.tablegames.protocol.gen.Game_S2C_AskDisbandProto;
import com.oegame.tablegames.protocol.gen.Game_S2C_DisbandProto;
import com.oegame.tablegames.protocol.gen.Game_S2C_EnterRoomProto;
import com.oegame.tablegames.protocol.gen.Game_S2C_LeaveRoomProto;
import com.oegame.tablegames.protocol.gen.Game_S2C_ReadyProto;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.oegame.tablegames.common.util.TimeUtil;
import com.oegame.tablegames.service.player.Player;
import com.oegame.tablegames.service.ServiceUtil;
import java.util.ArrayList;
import java.util.Map.Entry;

public abstract class RoomCtrlBase {

	private static Logger logger = LoggerFactory.getLogger(RoomCtrlBase.class);

	protected static final int MAX_DISBAND_TIME = 300 * 1000;

	protected static final int SEAT_READY_TIME = 15 * 1000;

	protected Timer timer = new Timer();

	protected TimerTask task;

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

	public synchronized boolean enter(Player player, int gold)
	{
		if (this.getRoom().player.containsKey(player.playerId))
		{
			return false;
		}

		int pos = this.getRoom().getFreeSeat();
		if (pos != 0)
		{
			this.getRoom().player.put(player.playerId, player);
			if (!this.sitdown(player.playerId, pos, gold))
			{
				logger.info(this.roominfo+"金币场坐下失败" + player.playerId);
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
			return true;
		}
		else
		{
			ServiceUtil.getProxyService().sendError(player.playerId, -1, "房间已满");
			return false;
		}
	}

	public void leave(long playerId, boolean canLeaveInGaming)
	{
		Player player = this.getRoom().player.get(playerId);
		if (player == null) return;
		int pos = this.getRoom().getPos(playerId);
		int gold = 0;
		if (this.getRoom().ownerId == playerId)
		{
			ServiceUtil.getProxyService().sendError(playerId, -1, "owner can not leave");
			return;
		}

		if(!canLeaveInGaming)
		{
			if (this.getRoom().loopCount != 1 || this.getRoom().status == RoomStatus.ROOM_STATUS_BEGIN)
			{
				ServiceUtil.getProxyService().sendError(playerId, -1, "游戏已经开始，不能离开房间。");
				return;
			}
		}

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
		if (this.getRoom().status != RoomStatus.ROOM_STATUS_READY)
		{
			return;
		}

		SeatBase seat = this.getRoom().getSeatByPlayerId(playerId);
		if (seat == null)
		{
			return;
		}
		if (seat.status == SeatStatus.SEAT_STATUS_READY)
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

	public boolean unready(long playerId) {

		if (this.getRoom().status != RoomStatus.ROOM_STATUS_READY) {
			logger.info(this.roominfo+"当前桌面不是准备状态");
			return false;
		}

		// 取自己的位置
		int pos = this.getRoom().getPos(playerId);

		if (pos == 0) {
			logger.info(this.roominfo+"你没有坐下，无法准备");
			return false;
		}

		this.getRoom().getSeatByPos(pos).status = SeatStatus.SEAT_STATUS_IDLE;

		return true;
	}

	public void afk(long playerId, boolean isAfk)
	{
		Player player = this.getRoom().player.get(playerId);
		if (player == null)
			return;

		SeatBase seat = this.getRoom().getSeatByPlayerId(playerId);
		if (seat == null)
			return;

		seat.isAfk = isAfk;

		Game_S2C_AFKProto proto = new Game_S2C_AFKProto();
		proto.setIsAfk(isAfk);
		proto.setPlayerId((int) playerId);

		this.refreshAll(0, proto.toArray());
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

	public void disbandApply(long playerId, DisbandStatus status)
	{
		SeatBase seat = this.getRoom().getSeatByPlayerId(playerId);
		if (seat == null)
		{
			return;
		}

		if (this.getRoom().status == RoomStatus.ROOM_STATUS_READY && this.getRoom().loopCount == 1)
		{
			if (playerId != this.getRoom().ownerId)
			{
				ServiceUtil.getProxyService().sendError(playerId, -1, "just owner can disband the room");
			}
			else
			{
				this.getGameService().disband(this.getRoom().roomId, playerId);
			}
			return;
		}
		if (seat.dismiss != DisbandStatus.DISSMISS_IDLE)
		{
			return;
		}
		if (status == DisbandStatus.DISSMISS_APPLY)
		{
			if (task != null)
			{
				task.cancel();
				task = null;
			}
			task = new TimerTask()
			{
				@Override
				public void run()
				{
					gameService.disband(getRoom().roomId, playerId);
				}
			};
			timer.schedule(task, MAX_DISBAND_TIME);
		}
		seat.dismiss = status;
		this.getRoom().dismissTime = TimeUtil.millisecond() + MAX_DISBAND_TIME;

		Game_S2C_AskDisbandProto s2c = new Game_S2C_AskDisbandProto();
		s2c.setDisbandStatus((byte) status.ordinal());
		s2c.setDisbandMaxTime(MAX_DISBAND_TIME);
		s2c.setDisbandTimestamp(this.getRoom().dismissTime);
		s2c.setPlayerId((int) playerId);
		this.refreshAll(0, s2c.toArray());
		if (status == DisbandStatus.DISSMISS_AGENT || status == DisbandStatus.DISSMISS_APPLY)
		{
			int disnum = 0;
			for (Entry<Integer, SeatBase> entry : this.getRoom().getSeats().entrySet())
			{
				if (entry.getValue().playerId != 0)
				{
					if (entry.getValue().dismiss == DisbandStatus.DISSMISS_AGENT
							|| entry.getValue().dismiss == DisbandStatus.DISSMISS_APPLY)
					{
						disnum++;
					}
				}
			}

			if ((float) disnum / this.getRoom().playerNum() >= 0.66f)
			{
				this.getGameService().disband(this.getRoom().roomId, playerId);
				return;
			}

			for (Entry<Integer, SeatBase> entry : this.getRoom().getSeats().entrySet())
			{
				if (entry.getValue().dismiss == DisbandStatus.DISSMISS_IDLE && entry.getValue().playerId != 0)
				{
					return;
				}
			}

			this.getGameService().disband(this.getRoom().roomId, playerId);
		}
		else if (status == DisbandStatus.DISSMISS_REJECT)
		{
			this.getRoom().dismissTime = 0;

			for (Entry<Integer, SeatBase> entry : this.getRoom().getSeats().entrySet())
			{
				entry.getValue().dismiss = DisbandStatus.DISSMISS_IDLE;
			}

			if (task != null)
			{
				task.cancel();
				task = null;
			}
			task = new TimerTask()
			{
				@Override
				public void run()
				{
					logger.info(roominfo+"n秒之后自动开始下一局");
					onBegin();
				}
			};
			timer.schedule(task, 10);

			Game_S2C_DisbandProto disbandProto = new Game_S2C_DisbandProto();
			disbandProto.setDisbandStatus((byte)DisbandStatus.DISSMISS_REJECT.ordinal());
			this.refreshAll(0, disbandProto.toArray());
			logger.info(this.roominfo+"有人拒绝,解散失败");
		}
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

	/**
	 * 踢人
	 */
	public void kick(long playerId) {
		RoomBase room = this.getRoom();
		Player player = room.player.get(playerId);
		if (player == null)
			return;

		this.leave(playerId, true);
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
				if (entry.getValue() != null && this.getRoom().getSeatByPlayerId(entry.getValue().playerId) != null
						&& !this.getRoom().getSeatByPlayerId(entry.getValue().playerId).isLeave)
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
