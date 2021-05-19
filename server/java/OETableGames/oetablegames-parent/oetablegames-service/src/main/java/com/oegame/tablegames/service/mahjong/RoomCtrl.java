package com.oegame.tablegames.service.mahjong;

import com.oegame.tablegames.protocol.gen.Game_S2C_ReadyProto;
import com.oegame.tablegames.service.game.GameService;
import com.oegame.tablegames.service.game.SeatStatus;
import com.oegame.tablegames.service.mahjong.model.Room;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;
import com.oegame.tablegames.service.mahjong.model.CombinationType;
import com.oegame.tablegames.service.mahjong.model.FightSet;
import com.oegame.tablegames.service.mahjong.model.KongSubType;
import com.oegame.tablegames.service.mahjong.model.Mahjong;
import com.oegame.tablegames.service.mahjong.model.MahjongGameStatus;
import com.oegame.tablegames.service.mahjong.model.MahjongGroup;
import com.oegame.tablegames.service.mahjong.model.MahjongSeatStatus;
import com.oegame.tablegames.service.mahjong.model.Seat;
import com.oegame.tablegames.service.mahjong.model.SettleVO;
import com.oegame.tablegames.service.mahjong.model.Tools;
import com.oegame.tablegames.common.log.Logger;
import com.oegame.tablegames.common.log.LoggerFactory;
import com.oegame.tablegames.common.util.TimeUtil;
import com.oegame.tablegames.service.player.Player;
import com.oegame.tablegames.service.game.RoomCtrlBase;

public class RoomCtrl extends RoomCtrlBase {
	private static final Logger logger = LoggerFactory.getLogger(RoomCtrl.class);

	protected static final int SEAT_WAIT_TIME = 15 * 1000;

	private Room room;
	protected MahjongAlgorithm helper = new MahjongAlgorithm();
	protected MahjongModel model;
	private MahjongS2C s2c;

	public RoomCtrl(Room room, GameService service) {
		super(service, new GameMahjongAI());
		this.room = room;
		this.roominfo = "mahjong房间号："+this.room.roomId+"  ";
		if (this.gameAI != null) {
			this.gameAI.setRoomCtrl(this);
		}
		s2c = new MahjongS2C(room);
		model = new MahjongModel(room);
	}

	@Override
	protected Room getRoom() {
		return this.room;
	}

	@Override
	protected void sendRoomInfo(long playerId) {
		s2c.sendRoomInfo(playerId, MAX_DISBAND_TIME);
	}

	/**
	 * 牌局开始
	 */
	@Override
	protected synchronized void onBegin() {
		super.onBegin();

		// 开局初始化房间
		model.beginInit();
		// 发牌
		model.buildMahjong();
		// 确定癞子
		model.getUniversal();
		// 房间状态变更为出牌,给所有人发送房间开局信息
		model.begin();

		Set<Entry<Long, Player>> set = this.room.player.entrySet();
		for (Entry<Long, Player> entry : set) {
			int playerId = (int) entry.getValue().playerId;
			s2c.sendBeginInfo(playerId);
		}

		// 庄家摸牌
		this.perflop(false);
		// 检测自己有没有操作
		model.myCheck(room.bankerPos);
	}

	// 摸牌逻辑体
	private void drawLogic(boolean isLast) {

		// 判断是否还有牌可以摸，否则就流局
		if (room.mahjong.size() == 0) {
			this.doSettle(0, 0, true);
			logger.info(this.roominfo+"流局");
			return;
		}

		// 判断是否摸牌
		if (room.seat.get(room.nextPos).perflop) {
			logger.info(this.roominfo+"手里有牌了：" + room.nextPos);
			return;
		}

		Player player = room.player.get(room.seat.get(room.nextPos).playerId);
		if (player == null) {
			logger.info(this.roominfo+"玩家信息错误");
			return;
		}
		// 摸牌
		this.perflop(isLast);
		// 检测自己是否有操作
		model.myCheck(room.nextPos);
	}

	/**
	 * 摸牌
	 */
	private void perflop(boolean isLast) {

		Seat seat = room.seat.get(room.nextPos);
		// 摸牌
		model.getMahjong(seat.pos, isLast);
		// 重置所有人的出牌状态都是WAIT
		model.beWait();
		// 设置为要操作
		seat.gameStatus = MahjongSeatStatus.SEAT_STATUS_DISCARD;
		// 设置这个座位的操作时间
		seat.countdown = TimeUtil.millisecond() + SEAT_WAIT_TIME;
		// 给摸牌的人发消息
		s2c.draw(seat, isLast);
		model.look(seat);
	}

	public void ready(long playerId)
	{
		Seat seat = this.getRoom().getSeatByPlayerId(playerId);
		if (seat == null)
		{
			return;
		}
		if (seat.status != SeatStatus.SEAT_STATUS_IDLE)
			return;
		seat.status = SeatStatus.SEAT_STATUS_READY;
		seat.gameStatus = MahjongSeatStatus.SEAT_STATUS_READY;

		s2c.sendReady((int)playerId);


		if (this.getRoom().getReadyCount() >= this.getRoom().getSeats().size()
			&& this.getRoom().loopCount <= this.getRoom().getRoomSetting().loop)
		{
			this.onBegin();
		}
	}

	public synchronized void discard(long playerId, int index) {

		if (!room.player.containsKey(playerId)) {
			logger.debug("玩家不存在");
			return;
		}
		// 取自己的位置
		int pos = room.getPos(playerId);
		// 就没有坐下
		if (pos == 0) {
			logger.info(this.roominfo+"你就没有坐下 ,pos为空");
			return;
		}
		// 取自己的状态
		Seat selfSeat = room.seat.get(pos);

		// 当前桌面不是出牌状态
		if (room.gameStatus != MahjongGameStatus.ROOM_STATUS_BEGIN) {
			logger.info(this.roominfo+"当前桌面不是出牌状态");
			return;
		}
		// 判断是否可以出手
		if (selfSeat.gameStatus != MahjongSeatStatus.SEAT_STATUS_DISCARD) {
			logger.info(this.roominfo+"不可以出手,你不是出牌状态： " + selfSeat.playerId);
			return;
		}
		// 判断这张牌是否存在
		if (selfSeat.hitMahjong != null) {
			if (!selfSeat.mahjong.containsKey(index) && selfSeat.hitMahjong.index != index) {
				logger.info(this.roominfo+"不可以出手 ,这张牌不存在");
				return;
			}
		}
		// 出牌
		model.discard(pos, index);

		// 广播给所有人出牌
		s2c.discard(playerId);

		// 重置所有人的出牌状态都是WAIT
		model.beWait();

		// 出牌以后 清空操作集合
		room.fightSet.clear();

		// 检测别人是否有操作
		model.otherCheck(pos, room.leftMahjong);

		// 下一个出牌的位置
		room.nextPos = model.nextPos(selfSeat.pos);

		model.look(selfSeat);
		// 下家摸牌
		if (room.fightSet.size() == 0) {
			room.seat.get(room.nextPos).perflop = false;
			this.drawLogic(false);
		}
	}

	/**
	 * 吃，碰，杠，胡
	 */
	public synchronized void operate(long playerId, CombinationType type, List<Integer> index) {

		if (!room.player.containsKey(playerId)) {
			logger.debug("玩家不存在");
			return;
		}
		int pos = room.getPos(playerId);

		if (pos == 0) {
			logger.info(this.roominfo+"你就没有坐下");
			return;
		}

		if (!room.fightSet.containsKey(pos)) {
			logger.info(this.roominfo+"你就不能出手" + pos);
			return;
		}

		Seat seat = room.seat.get(pos);
		boolean success = true;

		switch (type) {
		case POKER_TYPE_CHI:
			if (!model.isChi(seat.mahjong, seat.universal, room.leftMahjong)) {
				logger.info(this.roominfo+"你压根就不能吃,哪来回哪去 " + index.size());
				success = false;
			}
			if (index.size() != 3) {
				logger.info(this.roominfo+"长度不对,不能吃,哪来回哪去 " + index.size());
				success = false;
			}
			for (int i = 0; i < index.size(); i++) {
				if (index.get(i) != room.leftMahjong.index) {
					if (!seat.mahjong.containsKey(index.get(i))) {
						logger.info(this.roominfo+"要吃的牌不存在,哪来回哪去");
						success = false;
						break;
					}
				}
			}
			break;

		case POKER_TYPE_PENG:
			if (!model.isPeng(seat.mahjong, room.leftMahjong)) {
				logger.info(this.roominfo+"你压根就不能碰,哪来回哪去 " + index.size());
				success = false;
			}
			if (index.size() != 3) {
				logger.info(this.roominfo+"长度不对,不能碰 ,哪来回哪去" + index.size());
				success = false;
			}
			for (int i = 0; i < index.size(); i++) {
				if (index.get(i) != room.leftMahjong.index) {
					if (!room.seat.get(pos).mahjong.containsKey(index.get(i))) {
						logger.info(this.roominfo+"要碰的牌不存在,哪来回哪去");
						success = false;
						break;
					}
				}
			}
			break;
		case POKER_TYPE_GANG:
			if (index.size() == 1) {// 补杠
				if (model.isBuGang(seat.mahjong, seat.useMahjongGroup, seat.hitMahjong, false)) {
					if (seat.hitMahjong.index != index.get(0) && !seat.mahjong.containsKey(index.get(0))) {
						logger.info(this.roominfo+"你要补杠的牌不存在,哪来回哪去 " + index.size());
						success = false;
					}
				} else {
					logger.info(this.roominfo+"你压根就不能补杠,哪来回哪去 " + index.size());
					success = false;
				}
			} else if (index.size() == 4) {
				ArrayList<Integer> list = new ArrayList<Integer>();
				for (Entry<Integer, Mahjong> entry : seat.mahjong.entrySet()) {
					list.add(entry.getKey());
				}
				if (room.leftSeatPos == pos) {// 暗杠
					if (model.isGang(seat.mahjong, seat.hitMahjong, true)) {
						list.add(seat.hitMahjong.index);
						if (!list.containsAll(index)) {
							logger.info(this.roominfo+"你要暗杠的牌不存在 ,哪来回哪去" + index.size());
							success = false;
						}
					} else {
						logger.info(this.roominfo+"你不能暗杠 ,哪来回哪去" + index.size());
						success = false;
					}
				} else {// 明杠
					if (model.isGang(seat.mahjong, room.leftMahjong, false)) {
						list.add(room.leftMahjong.index);
						if (!list.containsAll(index)) {
							logger.info(this.roominfo+"你要明杠的牌不存在 ,哪来回哪去" + index.size());
							success = false;
						}
					} else {
						logger.info(this.roominfo+"你不能明杠 ,哪来回哪去" + index.size());
						success = false;
					}
				}
			} else {
				logger.info(this.roominfo+"你就不能杠 ,哪来回哪去" + index.size());
				return;
			}
			break;
		case POKER_TYPE_HU_ZIMO:
			if (room.leftSeatPos == pos) {// 自摸
				if (!model.isHu(seat, seat.hitMahjong, true, room.roomSetting)) {
					logger.info(this.roominfo+"你就不能自摸 ,哪来回哪去" + index.size());
					success = false;
				}
			}
			break;
		case POKER_TYPE_HU_PAO:
			if (!model.isHu(seat, room.leftMahjong, false, room.roomSetting)) {
				logger.info(this.roominfo+"你就不能胡 ,哪来回哪去" + index.size());
				success = false;
			}
			break;
		default:
			logger.info(this.roominfo+"未知操作 ,哪来回哪去" + index.size());
			success = false;
		}

		if (!success) {
			this.errerHandle(pos, room.leftSeatPos == seat.pos);
			return;
		}

		FightSet fightSet = room.fightSet.get(pos);

		if (fightSet.typeId != CombinationType.POKER_TYPE_NULL) {
			logger.info(this.roominfo+"你就已经操作过，请等待其他人" + pos);
			return;
		}

		model.fight(pos, type, index);
		boolean isWait = false;
		Set<Entry<Integer, FightSet>> set = room.fightSet.entrySet();
		for (Entry<Integer, FightSet> entry : set) {
			if (entry.getValue().typeId == CombinationType.POKER_TYPE_NULL) {
				if (model.isWait(fightSet.typeId, entry.getValue().group)) {
					logger.info("我要执行操作，还有人优先级比我高,等待他的操作。");
					s2c.waitOperate(room.seat.get(pos).playerId);
					isWait = true;
				}
			}
		}

		if (isWait) {
			logger.info(this.roominfo+" 等待更高优先级操作,返回");
			return;
		}
		this.inFight();
	}

	private void inFight() {
		Set<Entry<Integer, FightSet>> set = room.fightSet.entrySet();

		for (Entry<Integer, FightSet> entry : set) {
			if (entry.getValue().typeId == CombinationType.POKER_TYPE_NULL
					&& (entry.getValue().typeId == CombinationType.POKER_TYPE_HU_PAO
							|| entry.getValue().typeId == CombinationType.POKER_TYPE_HU_ZIMO)) {
				logger.info(this.roominfo+"还有人没有选择胡。");
				return;
			}
		}

		ArrayList<MahjongGroup> handle = new ArrayList<MahjongGroup>();
		for (Entry<Integer, FightSet> entry : set) {
			if (entry.getValue().select() != null) {
				handle.add(entry.getValue().select());
			}
		}

		ArrayList<MahjongGroup> hu = new ArrayList<MahjongGroup>();
		ArrayList<MahjongGroup> gang = new ArrayList<MahjongGroup>();
		ArrayList<MahjongGroup> peng = new ArrayList<MahjongGroup>();
		ArrayList<MahjongGroup> chi = new ArrayList<MahjongGroup>();

		for (int i = 0; i < handle.size(); i++) {
			if (handle.get(i).typeId == CombinationType.POKER_TYPE_HU_PAO
					|| handle.get(i).typeId == CombinationType.POKER_TYPE_HU_ZIMO) {
				hu.add(handle.get(i));
			} else if (handle.get(i).typeId == CombinationType.POKER_TYPE_GANG) {
				gang.add(handle.get(i));
			} else if (handle.get(i).typeId == CombinationType.POKER_TYPE_PENG) {
				peng.add(handle.get(i));
			} else if (handle.get(i).typeId == CombinationType.POKER_TYPE_CHI) {
				chi.add(handle.get(i));
			}
		}

		if (hu.size() > 0) {
			logger.info(this.roominfo+"这里在调用...胡");
			// 是否一炮多响
			if (room.roomSetting.isManyhu) {
				logger.info(this.roominfo+"是否一炮多响");
				for (int i = 0; i < hu.size(); i++) {
					this.doFight(hu.get(i).playerId, hu.get(i), i, i == hu.size() - 1);
				}
			} else {
				logger.info(this.roominfo+"按顺序胡");

				for (int i = 0; i < hu.size(); i++) {
					logger.info(this.roominfo+"i: " + hu.get(i).playerId + " - " + room.getPos(hu.get(i).playerId));
				}

				Collections.sort(hu, new Comparator<MahjongGroup>() {
					public int compare(MahjongGroup o1, MahjongGroup o2) {
						Integer p1 = room.getPos(o1.playerId) - room.paoshou;
						if (p1 < 0) {
							p1 = room.getPos(o1.playerId) + room.paoshou;
						}
						Integer p2 = room.getPos(o2.playerId) - room.paoshou;
						if (p2 < 0) {
							p2 = room.getPos(o2.playerId) + room.paoshou;
						}
						return p1.compareTo(p2);
					};
				});

				logger.info(this.roominfo+"按顺序胡");
				for (int i = 0; i < hu.size(); i++) {
					logger.info(this.roominfo+"i: " + hu.get(i).playerId + " - " + room.getPos(hu.get(i).playerId));
				}

				this.doFight(hu.get(0).playerId, hu.get(0), 0, true);
			}
		} else if (gang.size() > 0) {
			logger.info(this.roominfo+"这里在调用...杠");
			this.doFight(gang.get(0).playerId, gang.get(0), 0, true);
		} else if (peng.size() > 0) {
			logger.info(this.roominfo+"这里在调用...碰");
			this.doFight(peng.get(0).playerId, peng.get(0), 0, true);
		} else if (chi.size() > 0) {
			logger.info(this.roominfo+"这里在调用...吃");
			this.doFight(chi.get(0).playerId, chi.get(0), 0, true);
		}
	}

	private void doFight(long playerId, MahjongGroup pb_mahjong_group, int huCount, boolean last) {

		int pos = room.getPos(playerId);

		Seat seat = room.seat.get(pos);

		if (seat.gameStatus != MahjongSeatStatus.SEAT_STATUS_OPERATION) {
			logger.info(this.roominfo+"你不在吃,碰,杠,胡的状态");
			return;
		}

		// 吃,碰,杠,胡
		if (pb_mahjong_group.typeId != CombinationType.POKER_TYPE_CHI
				&& pb_mahjong_group.typeId != CombinationType.POKER_TYPE_PENG
				&& pb_mahjong_group.typeId != CombinationType.POKER_TYPE_GANG
				&& pb_mahjong_group.typeId != CombinationType.POKER_TYPE_HU_PAO
				&& pb_mahjong_group.typeId != CombinationType.POKER_TYPE_HU_ZIMO) {
			logger.info(this.roominfo+"类型错误,不能执行");
			return;
		}

		seat.perflop = false;

		// 1吃，2碰，3杠，4胡
		if (room.leftSeatPos == seat.pos) {
			// 自己只能是暗杠，和自摸
			if (pb_mahjong_group.typeId == CombinationType.POKER_TYPE_GANG) { // 杠

				// 测试选择的牌，判断是否是手牌
				int usePos = -1;
				// 是否是摸的牌
				usePos = helper.checkBuGang(seat.mahjong, seat.useMahjongGroup, seat.hitMahjong, false);

				if (usePos >= 0) {
					logger.info(this.roominfo+"补杠成功！！");

					Mahjong useMahjong = null;

					boolean isHit = false;
					if (pb_mahjong_group.index.contains(seat.hitMahjong.index)) {
						useMahjong = seat.hitMahjong;
						isHit = true;
					} else {
						for (int i = 0; i < pb_mahjong_group.index.size(); i++) {
							if (seat.mahjong.containsKey(pb_mahjong_group.index.get(i))) {
								useMahjong = seat.mahjong.get(pb_mahjong_group.index.get(i));
								break;
							}
						}
					}

					seat.useMahjongGroup.get(usePos).mahjong.add(useMahjong);
					seat.useMahjongGroup.get(usePos).typeId = CombinationType.POKER_TYPE_GANG;
					seat.useMahjongGroup.get(usePos).subTypeId = KongSubType.POKER_SUBTYPE_GANG_BU.ordinal();
					MahjongGroup mahjong_group = seat.useMahjongGroup.get(usePos);
					s2c.sendOperate(mahjong_group);

					// 是否有抢杠的功能
					if (room.roomSetting.isLoot) {
						boolean success = model.isLoot(pos, useMahjong, pb_mahjong_group, isHit, usePos);
						if (!success) {
							return;
						}
					}
					this.continueBugang(isHit, seat, pb_mahjong_group, usePos);
				} else if (helper.checkGang(seat.mahjong, seat.hitMahjong, true)) {

					MahjongGroup pb_group = model.doAnGang(seat.pos, pb_mahjong_group.index);
					// 重置所有人的出牌状态都是WAIT
					model.beWait();
					// 广播暗杠
					s2c.sendOperate(pb_group);
					// 杠完自己摸牌
					room.nextPos = seat.pos;
					this.drawLogic(true);

				} else {
					logger.info(this.roominfo+"要暗杠，没暗杠成！");
				}
			} else if (pb_mahjong_group.typeId == CombinationType.POKER_TYPE_HU_ZIMO) { // 胡
																						// TODO 验证自己是否胡牌，自摸，也可能是杠上开花
				SettleVO res = helper.checkHu(seat, seat.hitMahjong, true, room.roomSetting, false);
				if (res.code > 0) {

					// 重置所有人的出牌状态都是WAIT
					for (int i = 1; i <= room.roomSetting.player; i++) {
						room.seat.get(i).gameStatus = MahjongSeatStatus.SEAT_STATUS_WAIT;
					}

					logger.info(this.roominfo+"自摸了" + last);
					MahjongGroup pb_group = new MahjongGroup();
					pb_group.playerId = playerId;
					pb_group.typeId = CombinationType.POKER_TYPE_HU_ZIMO;
					pb_group.mahjong.add(seat.hitMahjong);
					// 向客户端广播自摸
					s2c.sendOperate(pb_group);

					seat.zimoCount++;
					seat.winner = true;

					model.settle(seat.pos, 0, true, last, res);
					pb_group.subTypeId = seat.type.ordinal();

					room.bankerPos = seat.pos;
					if (last) {
						this.doSettle(seat.pos, 0, last);
					}
					return;
				} else {
					logger.info(this.roominfo+"要自摸，没自摸成！");
				}
			}
		} else {
			if (pb_mahjong_group.typeId == CombinationType.POKER_TYPE_CHI) { // 吃
				ArrayList<Mahjong[]> group = helper.checkChi(seat.mahjong, seat.universal, room.leftMahjong);
				if (group.size() > 0) {

					MahjongGroup pb_group = model.doChi(seat.pos, pb_mahjong_group.index, room.leftMahjong);
					s2c.sendOperate(pb_group);
				} else {
					logger.info(this.roominfo+"要吃，没吃成");
				}

			} else if (pb_mahjong_group.typeId == CombinationType.POKER_TYPE_PENG) { // 碰

				if (helper.checkPeng(seat.mahjong, room.leftMahjong)) {

					MahjongGroup pb_group = model.doPeng(seat, pb_mahjong_group.index, room.leftMahjong);
					logger.info(this.roominfo+"碰了");
					s2c.sendOperate(pb_group);
				} else {
					logger.info(this.roominfo+"要碰，没碰成！");
				}
			} else if (pb_mahjong_group.typeId == CombinationType.POKER_TYPE_GANG) { // 只能杠
				if (helper.checkGang(seat.mahjong, room.leftMahjong, false)) {
					MahjongGroup pb_group = model.doMingGang(seat, pb_mahjong_group.index, room.leftMahjong);
					logger.info(this.roominfo+"明杠了");
					// 杠
					s2c.sendOperate(pb_group);
					// 杠完自己摸牌
					room.nextPos = seat.pos;
					this.drawLogic(true);
				} else {
					logger.info(this.roominfo+"要杠，没杠成！");
				}
			} else if (pb_mahjong_group.typeId == CombinationType.POKER_TYPE_HU_PAO) { // 胡
																						// TODO 别人打的牌胡了，肯定是屁胡
				SettleVO res = helper.checkHu(seat, room.leftMahjong, false, room.roomSetting, false);
				if (res.code > 0) {

					logger.info(this.roominfo+"玩家" + seat.playerId + " 屁胡了: " + last);

					MahjongGroup pb_group = new MahjongGroup();
					pb_group.playerId = playerId;
					pb_group.typeId = (CombinationType.POKER_TYPE_HU_PAO);
					pb_group.mahjong.add(room.leftMahjong);

					// 向客户端广播屁胡了
					s2c.sendOperate(pb_group);

					seat.paoCount++;

					// 抢杠记录收益
					if (room.buGang != null) {

						room._seat.useMahjongGroup.get(room._gpos).mahjong.remove(0);
						room._seat.useMahjongGroup.get(room._gpos).typeId = CombinationType.POKER_TYPE_PENG;

						if (room._isHit) {
							room._seat.hitMahjong = null;
						} else {
							for (int i = 0; i < room._pb_mahjong_group.index.size(); i++) {
								if (room._seat.mahjong.containsKey(room._pb_mahjong_group.index.get(i))) {
									room._seat.mahjong.remove(room._pb_mahjong_group.index.get(i));
								}
							}

							// if (room._seat.hitMahjong != null) {
							// room._seat.com.oegame.tablegames.service.mahjong.put(room._seat.hitMahjong.index, room._seat.hitMahjong);
							// }
							// if (huCount <= 1) {
							room._seat.hitMahjong = null;
							// }
						}
					}
					seat.hitMahjong = room.leftMahjong;
					seat.winner = true;
					room.seat.get(room.leftSeatPos).loser = true;

					model.settle(seat.pos, room.leftSeatPos, false, last, res);
					pb_group.subTypeId = seat.type.ordinal();

					if (last && room.gameStatus != MahjongGameStatus.ROOM_STATUS_SETTLE) {
						if (huCount > 0) {
							room.bankerPos = room.leftSeatPos;
						} else {
							room.bankerPos = seat.pos;
							logger.info(this.roominfo+"下局庄家是" + seat.pos);
						}
						this.doSettle(seat.pos, room.leftSeatPos, last);
					}
					return;
				} else {
					logger.info(this.roominfo+"要胡，没胡成！" + Tools.mahjong(room.leftMahjong.color, room.leftMahjong.size));
				}
			}
		}
	}

	/**
	 * 补杠，别人能胡，但是不胡，我自己继续杠
	 */
	private void continueBugang(boolean isHit, Seat seat, MahjongGroup pb_mahjong_group, int usePos) {
		model.continueBugang(isHit, seat, pb_mahjong_group, usePos);
		// 杠完自己摸牌
		room.nextPos = seat.pos;
		this.drawLogic(true);
	}

	/**
	 * 不吃，不碰，不杠，不胡
	 */
	public synchronized void pass(long playerId) {

		if (!room.player.containsKey(playerId)) {
			logger.debug("玩家不存在");
			return;
		}
		int pos = room.getPos(playerId);

		if (pos == 0) {
			logger.info(this.roominfo+"你就没有坐下");
			return;
		}

		Seat seat = room.seat.get(pos);

		if (seat.gameStatus != MahjongSeatStatus.SEAT_STATUS_OPERATION) {
			logger.info(this.roominfo+"你不在吃,碰,杠,胡的状态");
			return;
		}

		logger.info(this.roominfo+"pass: " + room.nextPos + "  ==  " + pos);

		seat.gameStatus = MahjongSeatStatus.SEAT_STATUS_WAIT;

		// 下一个要出牌的人，和自己是同一个，改变自己的出牌状态
		if (room.seat.get(room.nextPos).gameStatus != MahjongSeatStatus.SEAT_STATUS_OPERATION) {
			room.seat.get(room.nextPos).gameStatus = MahjongSeatStatus.SEAT_STATUS_DISCARD;
		}
		room.seat.get(room.nextPos).countdown = TimeUtil.millisecond() + SEAT_WAIT_TIME;

		s2c.pass(playerId);

		// -------------------------判断抢胡，一炮多响-----------------------------------------//
		// 如果出牌集合 > 1 删除自己
		if (room.fightSet.size() > 0) {
			if (room.fightSet.containsKey(pos)) {
				room.fightSet.remove(pos);
			}
		}

		seat.askMahjongGroup.clear();

		// 如果剩下1个人，判断他是否操作过，可以直接帮对方出牌
		if (room.fightSet.size() > 0) {

			logger.info(this.roominfo+"我帮别人出牌");
			Set<Entry<Integer, FightSet>> set = room.fightSet.entrySet();
			for (Entry<Integer, FightSet> entry : set) {
				if (entry.getValue().typeId == CombinationType.POKER_TYPE_NULL) {
					logger.info(this.roominfo+"但还有人没有操作");
					seat.gameStatus = MahjongSeatStatus.SEAT_STATUS_WAIT;
					return;
				}
			}

			this.inFight();

			return;
		}

		if (room.buGang != null) {
			logger.info(this.roominfo+"我刚才补杠呢，被别人抢胡，现在别人不胡，我继续吧。");
			this.continueBugang(room._isHit, room._seat, room._pb_mahjong_group, room._gpos);
			return;
		}
		// -------------------------判断抢胡，一炮多响-----------------------------------------//

		// 如果不是自己
		this.drawLogic(false);
	}

	private void doSettle(int winer, int loser, boolean last) {

		room.gameStatus = MahjongGameStatus.ROOM_STATUS_SETTLE;

		if (last) {
			room.loopCount++;
		}
		s2c.settle();

		// 重置桌面状态
		room.gameStatus = MahjongGameStatus.ROOM_STATUS_READY;

		logger.info(this.roominfo+room.loopCount + " > " + room.roomSetting.loop);

		if (room.loopCount > room.roomSetting.loop) {
			logger.info(this.roominfo+"牌局结束啦？");

			this.getGameService().roomResult(this.room.roomId);
		}
		// 初始化
		logger.info(this.roominfo+"初始化座位");
		for (int x = 1; x <= room.roomSetting.player; x++) {
			room.seat.get(x).init();
		}
	}

	/**
	 * 发送最终结算
	 */
	@Override
	protected void onResult() {
		super.onResult();
		s2c.result();
	}

	// 错误执行的处理
	private void errerHandle(int pos, boolean isSelf) {
		room.fightSet.remove(pos);
		room.seat.get(pos).askMahjongGroup.clear();
		room.fightSet.remove(pos);
		room.seat.get(pos).askMahjongGroup.clear();
		if (isSelf) {
			room.seat.get(pos).gameStatus = MahjongSeatStatus.SEAT_STATUS_DISCARD;
		} else {
			if (room.fightSet.size() == 0) {
				this.drawLogic(false);
			}
		}
	}

}
