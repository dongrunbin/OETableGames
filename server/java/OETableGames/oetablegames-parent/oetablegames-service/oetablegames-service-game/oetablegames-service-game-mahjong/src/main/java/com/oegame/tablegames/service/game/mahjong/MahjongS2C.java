package com.oegame.tablegames.service.game.mahjong;

import java.util.ArrayList;
import java.util.Set;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.oegame.tablegames.service.player.Player;
import com.oegame.tablegames.service.game.mahjong.model.Poker;
import com.oegame.tablegames.service.game.mahjong.model.PokerGroup;
import com.oegame.tablegames.service.game.mahjong.model.Seat;
import com.oegame.tablegames.service.ServiceUtil;
import com.oegame.tablegames.protocol.gen.*;

public class MahjongS2C {

	private static final Logger logger = LoggerFactory.getLogger(MahjongS2C.class);

	private Room room;

	public MahjongS2C(Room room) {
		this.room = room;
	}

	public void sendRoomInfo(long playerId, int MAX_DISBAND_TIME) {

		if (!room.player.containsKey(playerId))
			return;
		Player player = room.player.get(playerId);
		if (player == null)
			return;
		Mahjong_S2C_RoomInfoProto s2c = new Mahjong_S2C_RoomInfoProto();
		s2c.setBaseScore(room.baseScore);
		s2c.setDiceFirst(room.firstDicePos);
		s2c.setDiceFirstA(room.firstDiceA);
		s2c.setDiceFirstB(room.firstDiceB);
		s2c.setDiceSecond(room.secondDicePos);
		s2c.setDiceSecondA(room.secondDiceA);
		s2c.setDiceSecondB(room.secondDiceB);
		s2c.setDismissMaxTime(MAX_DISBAND_TIME);
		s2c.setDismissTime(room.dismissTime);

		if (room.leftPoker != null) {
			Mahjong_S2C_RoomInfoProto.Mahjong lastHitPoker = new Mahjong_S2C_RoomInfoProto.Mahjong();
			lastHitPoker.setIndex(room.leftPoker.index);
			lastHitPoker.setColor(room.leftPoker.color);
			lastHitPoker.setNumber(room.leftPoker.size);
		}

		s2c.setLoop(room.loopCount);
		s2c.setMaxLoop(room.getRoomSetting().loop);
		s2c.setMahjongAmount(room.poker.size());
		s2c.setMahjongTotal(room.pokerTotal);
		s2c.setRoomId(room.roomId);
		s2c.setRoomStatus((byte) room.status.ordinal());
		if (room.settingIds != null && room.settingIds.size() > 0) {
			for (int i = 0; i < room.settingIds.size(); ++i) {
				s2c.addSettingIds(room.settingIds.get(i));
			}
		}

		Seat selfSeat = room.getSeatByPlayerId(playerId);
		if (selfSeat.isLeave) {
			selfSeat.isLeave = false;
			selfSeat.player.isRobot = false;
		}
		if (selfSeat != null && selfSeat.askPokerGroup != null && selfSeat.askPokerGroup.size() > 0) {
			for (int i = 0; i < selfSeat.askPokerGroup.size(); ++i) {
				Mahjong_S2C_RoomInfoProto.MahjongGroup ask = new Mahjong_S2C_RoomInfoProto.MahjongGroup();
				PokerGroup group = selfSeat.askPokerGroup.get(i);
				ask.setPlayerId((int) group.playerId);
				ask.setSubTypeId((byte) group.subTypeId);
				ask.setTypeId((byte) group.typeId.ordinal());
				if (group.poker != null && group.poker.size() > 0) {
					for (int j = 0; j < group.poker.size(); ++j) {
						if (group.poker.get(j) != null) {
							Mahjong_S2C_RoomInfoProto.Mahjong poker = new Mahjong_S2C_RoomInfoProto.Mahjong();
							poker.setIndex(group.poker.get(j).index);
							poker.setColor(group.poker.get(j).color);
							poker.setNumber(group.poker.get(j).size);
							ask.addMahjongs(poker);
						}
					}
				}
				s2c.addAskMahjongGroups(ask);
			}
		}

		for (Entry<Integer, Seat> entry : room.seat.entrySet()) {
			Seat seat = entry.getValue();
			Mahjong_S2C_RoomInfoProto.Seat pSeat = new Mahjong_S2C_RoomInfoProto.Seat();
			if (seat.player != null) {
				pSeat.setAvatar(seat.player.avatar);
				pSeat.setGender(seat.player.gender);
				pSeat.setNickname(seat.player.nickname);
				pSeat.setPlayerId((int) seat.player.playerId);
			}
			pSeat.setDisbandtatus((byte) seat.dismiss.ordinal());
			pSeat.setGold(seat.gold);
			pSeat.setHandCount(seat.handCount);

			if (seat.hitPoker != null && seat.player.playerId == playerId) {
				Mahjong_S2C_RoomInfoProto.Mahjong hitPoker = new Mahjong_S2C_RoomInfoProto.Mahjong();
				hitPoker.setIndex(seat.hitPoker.index);
				hitPoker.setColor(seat.hitPoker.color);
				hitPoker.setNumber(seat.hitPoker.size);
				pSeat.setHitMahjong(hitPoker);
			}

			pSeat.setIsBanker(seat.isBanker);
			pSeat.setMahjongAmount(13);
			pSeat.setPos(seat.pos);
			pSeat.setStatus((byte) seat.status.ordinal());

			if (seat.poker != null && seat.poker.size() > 0) {
				if (seat.player.playerId == playerId) {
					for (Entry<Integer, Poker> pokerEntry : seat.poker.entrySet()) {
						Mahjong_S2C_RoomInfoProto.Mahjong poker = new Mahjong_S2C_RoomInfoProto.Mahjong();
						poker.setIndex(pokerEntry.getValue().index);
						poker.setColor(pokerEntry.getValue().color);
						poker.setNumber(pokerEntry.getValue().size);
						pSeat.addMahjongs(poker);
					}
				} else {
					for (Entry<Integer, Poker> pokerEntry : seat.poker.entrySet()) {
						Mahjong_S2C_RoomInfoProto.Mahjong poker = new Mahjong_S2C_RoomInfoProto.Mahjong();
						poker.setIndex(pokerEntry.getValue().index);
						pSeat.addMahjongs(poker);
					}
				}
			}

			if (seat.universal != null && seat.universal.size() > 0) {
				for (int i = 0; i < seat.universal.size(); ++i) {
					Mahjong_S2C_RoomInfoProto.Mahjong universal = new Mahjong_S2C_RoomInfoProto.Mahjong();
					universal.setIndex(seat.universal.get(i).index);
					universal.setColor(seat.universal.get(i).color);
					universal.setNumber(seat.universal.get(i).size);
					pSeat.addUniversal(universal);
				}
			}

			if (seat.desktop != null && seat.desktop.size() > 0) {
				for (int i = 0; i < seat.desktop.size(); ++i) {
					Mahjong_S2C_RoomInfoProto.Mahjong desktop = new Mahjong_S2C_RoomInfoProto.Mahjong();
					desktop.setIndex(seat.desktop.get(i).index);
					desktop.setColor(seat.desktop.get(i).color);
					desktop.setNumber(seat.desktop.get(i).size);
					pSeat.addDesktop(desktop);
				}
			}

			if (seat.usePokerGroup != null && seat.usePokerGroup.size() > 0) {
				for (int i = 0; i < seat.usePokerGroup.size(); ++i) {
					Mahjong_S2C_RoomInfoProto.MahjongGroup use = new Mahjong_S2C_RoomInfoProto.MahjongGroup();
					use.setPlayerId((int) seat.usePokerGroup.get(i).playerId);
					use.setSubTypeId((byte) seat.usePokerGroup.get(i).subTypeId);
					use.setTypeId((byte) seat.usePokerGroup.get(i).typeId.ordinal());
					for (int j = 0; j < seat.usePokerGroup.get(i).poker.size(); ++j) {
						Mahjong_S2C_RoomInfoProto.Mahjong pPoker = new Mahjong_S2C_RoomInfoProto.Mahjong();
						pPoker.setIndex(seat.usePokerGroup.get(i).poker.get(j).index);
						pPoker.setColor(seat.usePokerGroup.get(i).poker.get(j).color);
						pPoker.setNumber(seat.usePokerGroup.get(i).poker.get(j).size);
						use.addMahjongs(pPoker);
					}
					pSeat.addUsedMahjongGroup(use);
				}
			}
			s2c.addSeats(pSeat);
		}

		this.refreshOne(player.playerId, s2c.toArray());

	}

	public void sendBeginInfo(int playerId) {

		Mahjong_S2C_GameBeginProto pb_room = new Mahjong_S2C_GameBeginProto();

		pb_room.setRoomId(room.roomId);
		pb_room.setGamesCount(room.loopCount);
		pb_room.addDicePos(room.firstDicePos);
		pb_room.addDicePos(room.secondDicePos);

		pb_room.addDices(room.firstDiceA);
		pb_room.addDices(room.firstDiceB);
		pb_room.addDices(room.secondDiceA);
		pb_room.addDices(room.secondDiceB);
		pb_room.setMahjongTotal(room.pokerTotal);
		pb_room.setMahjongAmount(room.poker.size());

		for (int i = 1; i <= room.roomSetting.player; i++) {
			Seat seat = room.seat.get(i);
			Mahjong_S2C_GameBeginProto.Seat pSeat = new Mahjong_S2C_GameBeginProto.Seat();
			pSeat.setGold(seat.gold);
			pSeat.setHandCount(seat.handCount);
			if (seat.hitPoker != null) {
				if (seat.playerId == playerId) {
					Mahjong_S2C_GameBeginProto.Mahjong mahjong = new Mahjong_S2C_GameBeginProto.Mahjong();
					mahjong.setColor(seat.hitPoker.color);
					mahjong.setIndex(seat.hitPoker.index);
					mahjong.setPos(seat.hitPoker.pos);
					mahjong.setNumber(seat.hitPoker.size);
					pSeat.setHitMahjong(mahjong);
				} else {
					Mahjong_S2C_GameBeginProto.Mahjong mahjong = new Mahjong_S2C_GameBeginProto.Mahjong();
					mahjong.setIndex(seat.hitPoker.index);
					pSeat.setHitMahjong(mahjong);
				}
			}

			pSeat.setIsBanker(seat.pos == room.bankerPos);
			pSeat.setPlayerId((int) seat.playerId);
			if (seat.poker != null && seat.poker.size() > 0) {
				if (seat.playerId == playerId) {
					for (Entry<Integer, Poker> entry : seat.poker.entrySet()) {
						Mahjong_S2C_GameBeginProto.Mahjong mahjong = new Mahjong_S2C_GameBeginProto.Mahjong();
						mahjong.setColor(entry.getValue().color);
						mahjong.setIndex(entry.getValue().index);
						mahjong.setPos(entry.getValue().pos);
						mahjong.setNumber(entry.getValue().size);
						pSeat.addMahjongs(mahjong);
					}
				} else {
					for (Entry<Integer, Poker> entry : seat.poker.entrySet()) {
						Mahjong_S2C_GameBeginProto.Mahjong mahjong = new Mahjong_S2C_GameBeginProto.Mahjong();
						mahjong.setIndex(entry.getValue().index);
						pSeat.addMahjongs(mahjong);
					}
				}
			}

			pSeat.setMahjongAmount(seat.poker.size());
			pSeat.setPos(seat.pos);
			if (seat.universal != null && seat.universal.size() > 0) {
				for (int j = 0; j < seat.universal.size(); ++j) {
					Mahjong_S2C_GameBeginProto.Mahjong mahjong = new Mahjong_S2C_GameBeginProto.Mahjong();
					mahjong.setColor(seat.universal.get(j).color);
					mahjong.setIndex(seat.universal.get(j).index);
					mahjong.setPos(seat.universal.get(j).pos);
					mahjong.setNumber(seat.universal.get(j).size);
					pSeat.addUniversalMahjongs(mahjong);
				}
			}
			pb_room.addSeat(pSeat);
		}

		pb_room.setStatus(room.gameStatus.ordinal());
		this.refreshOne(playerId, pb_room.toArray());
	}

	public void draw( Seat seat,boolean isLast) {

		Mahjong_S2C_DrawProto s2c = new Mahjong_S2C_DrawProto();
		s2c.setColor(seat.hitPoker.color);
		s2c.setNumber(seat.hitPoker.size);
		s2c.setIndex(seat.hitPoker.index);
		s2c.setPlayerId((int) seat.playerId);
		s2c.setCountdown(seat.countdown);
		s2c.setIsFromLast(isLast);
		this.refreshOne(seat.playerId, s2c.toArray());

		// 给所有的人发送谁摸牌了
		s2c = new Mahjong_S2C_DrawProto();
		s2c.setIndex(seat.hitPoker.index);
		s2c.setPlayerId((int) seat.playerId);
		s2c.setCountdown(seat.countdown);
		s2c.setIsFromLast(isLast);
		this.refreshAll(seat.playerId, s2c.toArray());
	}

	public void discard(long playerId) {
		Mahjong_S2C_DiscardProto pb_seat = new Mahjong_S2C_DiscardProto();
		pb_seat.setPlayerId((int) playerId);
		pb_seat.setColor(room.leftPoker.color);
		pb_seat.setNumber(room.leftPoker.size);
		pb_seat.setIndex(room.leftPoker.index);
		this.refreshAll(0, pb_seat.toArray());
	}

	public void operateAsk(Seat seat, ArrayList<PokerGroup> ask_poker_group) {
		Mahjong_S2C_AskOperationProto ask_seat = new Mahjong_S2C_AskOperationProto();
		for (int i = 0; i < ask_poker_group.size(); i++) {
			PokerGroup group = ask_poker_group.get(i);
			Mahjong_S2C_AskOperationProto.MahjongGroup pb_poker_group = new Mahjong_S2C_AskOperationProto.MahjongGroup();

			pb_poker_group.setTypeId((byte) group.typeId.ordinal());

			pb_poker_group.setSubTypeId((byte) group.subTypeId);

			for (int j = 0; j < group.poker.size(); j++) {
				if (group.poker.get(j) == null) {
					continue;
				}
				Mahjong_S2C_AskOperationProto.Mahjong poker = new Mahjong_S2C_AskOperationProto.Mahjong();
				poker.setColor(group.poker.get(j).color);
				poker.setIndex(group.poker.get(j).index);
				poker.setNumber(group.poker.get(j).size);
				pb_poker_group.addMahjongs(poker);
			}
			pb_poker_group.setPlayerId((int) group.playerId);
			ask_seat.addAskMahjongGroups(pb_poker_group);
		}

		this.refreshOne(seat.player.playerId, ask_seat.toArray());
	}

	public void settle() {
		Mahjong_S2C_SettleProto room_settle = new Mahjong_S2C_SettleProto();

		for (Entry<Integer, Seat> entry : room.seat.entrySet()) {
			Seat seat = entry.getValue();

			Mahjong_S2C_SettleProto.Seat pSeat = new Mahjong_S2C_SettleProto.Seat();
			pSeat.setGold(seat.gold);
			if (seat.hitPoker != null) {
				Mahjong_S2C_SettleProto.Mahjong mahjong = new Mahjong_S2C_SettleProto.Mahjong();
				mahjong.setColor(seat.hitPoker.color);
				mahjong.setIndex(seat.hitPoker.index);
				mahjong.setNumber(seat.hitPoker.size);
				mahjong.setPos(seat.hitPoker.pos);
				pSeat.setHitMahjong(mahjong);
			}
			pSeat.setIsLoser(seat.loser);
			pSeat.setPlayerId((int) seat.playerId);
			pSeat.setIncomesDesc(seat.incomesDesc);
			pSeat.setPos(seat.pos);
			pSeat.setSettle(seat.settle);
			pSeat.setStatus((byte) seat.gameStatus.ordinal());
			for (Entry<Integer, Poker> pokerEntry : seat.poker.entrySet()) {
				Mahjong_S2C_SettleProto.Mahjong mahjong = new Mahjong_S2C_SettleProto.Mahjong();
				mahjong.setColor(pokerEntry.getValue().color);
				mahjong.setIndex(pokerEntry.getValue().index);
				mahjong.setNumber(pokerEntry.getValue().size);
				mahjong.setPos(pokerEntry.getValue().pos);
				pSeat.addMahjongs(mahjong);
			}
			for (int i = 0; i < seat.usePokerGroup.size(); i++) {
				Mahjong_S2C_SettleProto.MahjongGroup group = new Mahjong_S2C_SettleProto.MahjongGroup();
				group.setTypeId((byte)seat.usePokerGroup.get(i).typeId.ordinal());
				group.setSubTypeId((byte)seat.usePokerGroup.get(i).subTypeId);
				for (int j = 0; j < seat.usePokerGroup.get(i).poker.size(); j++) {
					Mahjong_S2C_SettleProto.Mahjong mahjong = new Mahjong_S2C_SettleProto.Mahjong();
					mahjong.setColor(seat.usePokerGroup.get(i).poker.get(j).color);
					mahjong.setIndex(seat.usePokerGroup.get(i).poker.get(j).index);
					mahjong.setNumber(seat.usePokerGroup.get(i).poker.get(j).size);
					mahjong.setPos(seat.usePokerGroup.get(i).poker.get(j).pos);
					group.addMahjongs(mahjong);
				}
				pSeat.addUsedMahjongGroup(group);
			}
			for (int i = 0; i < seat.desktop.size(); ++i) {
				Mahjong_S2C_SettleProto.Mahjong mahjong = new Mahjong_S2C_SettleProto.Mahjong();
				mahjong.setColor(seat.desktop.get(i).color);
				mahjong.setIndex(seat.desktop.get(i).index);
				mahjong.setNumber(seat.desktop.get(i).size);
				mahjong.setPos(seat.desktop.get(i).pos);
				pSeat.addDesktopMahjongs(mahjong);
			}

			room_settle.addSeats(pSeat);
		}

		if (room.loopCount > room.roomSetting.loop) {
			room_settle.setIsOver(true);
		}
		// 给所有人发结算
		this.refreshAll(0,room_settle.toArray());
	}

	public void result() {
		Mahjong_S2C_ResultProto proto = new Mahjong_S2C_ResultProto();

		for (Entry<Integer, Seat> entry : room.seat.entrySet()) {
			Seat seat = entry.getValue();
			Mahjong_S2C_ResultProto.Seat pSeat = new Mahjong_S2C_ResultProto.Seat();
			pSeat.setGold(seat.gold);
			pSeat.setIsWinner(false);
			pSeat.setPlayerId((int) seat.playerId);
			pSeat.setPos(seat.pos);
			proto.addSeats(pSeat);
		}

		this.refreshAll(0, proto.toArray());
	}

	public void sendOperate( PokerGroup group) {
		Mahjong_S2C_OperateProto proto = new Mahjong_S2C_OperateProto();
//		proto.setCountdown(TimeUtil.millisecond() + RoomCtrl.SEAT_WAIT_TIME);
		proto.setPlayerId((int) group.playerId);
		proto.setSubTypeId((byte) group.subTypeId);
		proto.setTypeId((byte) group.typeId.ordinal());
		for (int i = 0; i < group.poker.size(); ++i) {
			Poker poker = group.poker.get(i);
			Mahjong_S2C_OperateProto.Mahjong pMahjong = new Mahjong_S2C_OperateProto.Mahjong();
			pMahjong.setColor(poker.color);
			pMahjong.setNumber(poker.size);
			pMahjong.setIndex(poker.index);
			pMahjong.setPos(poker.pos);
			proto.addMahjongs(pMahjong);
		}
		this.refreshAll(0, proto.toArray());
	}

	public void pass(long playerId) {
		this.refreshOne(playerId, new Mahjong_S2C_PassProto().toArray());
	}

	public void waitOperate(long playerId) {
		this.refreshOne(playerId,	new Mahjong_S2C_OperationWaitProto().toArray());
	}
	
	public void refreshAll(long playerId, byte[] data)
	{
		Set<Entry<Long, Player>> set = room.player.entrySet();
		for (Entry<Long, Player> entry : set)
		{
			if (playerId != entry.getKey())
			{
				if (entry.getValue() != null 
						&& room.getSeatByPlayerId(entry.getValue().playerId) != null
						&& !room.getSeatByPlayerId(entry.getValue().playerId).isLeave)
				{
					Player toPlayer = entry.getValue();
					ServiceUtil.getProxyService().sendMessage(toPlayer.playerId, data);
				} 
				else
				{
					logger.info("玩家不存在 ");
				}
			}
		}
	}
	
	public void refreshOne(long playerId, byte[] data) {
		if (!room.getSeatByPlayerId(playerId).isLeave) {
			ServiceUtil.getProxyService().sendMessage(playerId, data);
		}
	}
	
}
