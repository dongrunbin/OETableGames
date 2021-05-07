package com.oegame.tablegames.service.mahjong;

import com.oegame.tablegames.common.log.Logger;
import com.oegame.tablegames.common.log.LoggerFactory;
import com.oegame.tablegames.service.game.SeatStatus;
import com.oegame.tablegames.service.mahjong.model.Room;
import com.oegame.tablegames.service.mahjong.model.Settle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Random;
import java.util.Set;
import com.oegame.tablegames.service.mahjong.model.CombinationType;
import com.oegame.tablegames.service.mahjong.model.FightSet;
import com.oegame.tablegames.service.mahjong.model.GangGroup;
import com.oegame.tablegames.service.mahjong.model.GangSubType;
import com.oegame.tablegames.service.mahjong.model.Mahjong;
import com.oegame.tablegames.service.mahjong.model.MahjongGameStatus;
import com.oegame.tablegames.service.mahjong.model.MahjongGroup;
import com.oegame.tablegames.service.mahjong.model.MahjongSeatStatus;
import com.oegame.tablegames.service.mahjong.model.RoomSetting;
import com.oegame.tablegames.service.mahjong.model.Seat;
import com.oegame.tablegames.service.mahjong.model.SettleVO;
import com.oegame.tablegames.service.mahjong.model.Tools;
import com.oegame.tablegames.common.util.TimeUtil;
import com.oegame.tablegames.service.player.Player;

public class MahjongModel {

	private static final Logger logger = LoggerFactory.getLogger(MahjongModel.class);

	private static final int SEAT_WAIT_TIME = 15 * 1000;

	private Room room;

	Settle settle = new Settle();

	/**
	 * 麻将的操作类
	 */
	protected MahjongAlgorithm helper = new MahjongAlgorithm();

	// 麻将S2C协议发送
	protected MahjongS2C s2c;

	public MahjongModel(Room room) {
		this.room = room;
		s2c = new MahjongS2C(room);
	}

	/**
	 * 开局
	 */
	protected void beginInit() {

		this.room.buGang = null;

		this.room.gangshangpao = 0;

		// 初始化第几手出牌
		this.room.handCount = 0;
		logger.info("本局地主是" + room.bankerPos);
		// 下一个出牌的人，就是当前这局的庄家
		this.room.nextPos = room.bankerPos;

		// 第一次摇骰子的位置
		this.room.firstDicePos = room.bankerPos;
		// 第一次摇骰子
		this.room.firstDiceA = (byte) Math.ceil(Math.random() * 6);
		this.room.firstDiceB = (byte) Math.ceil(Math.random() * 6);

		logger.info(room.firstDicePos + "第一次骰子： " + room.firstDiceA + " - " + room.firstDiceB);

		// 计算下一个摇骰子的位置
		this.room.secondDicePos = (room.firstDiceA + room.firstDiceB + room.nextPos + 1) % room.roomSetting.player;
		if (room.secondDicePos < 1) {
			room.secondDicePos = room.roomSetting.player - 1;
		}
		// 第二次摇骰子
		this.room.secondDiceA = (byte) Math.ceil(Math.random() * 6);
		this.room.secondDiceB = (byte) Math.ceil(Math.random() * 6);

		logger.info(room.secondDicePos + "第二次骰子： " + room.secondDiceA + " - " + room.secondDiceB);

		// 游戏开始
		this.room.gameStatus = MahjongGameStatus.ROOM_STATUS_DEAL;

		// 设定庄位状态
		this.room.seat.get(room.bankerPos).isBanker = true;
	}

	/**
	 * 洗牌
	 */
	protected void buildMahjong() {

		room.mahjong.clear();

		int index = 0;

		// 装填万
		for (int size = 1; size <= 9; size++) {
			for (int c = 1; c <= 4; c++) {
				index++;
				room.mahjong.add(new Mahjong(index, 1, size));
			}
		}
		// 装填筒
		for (int size = 1; size <= 9; size++) {
			for (int i = 1; i <= 4; i++) {
				index++;
				room.mahjong.add(new Mahjong(index, 2, size));
			}
		}
		// 装填条
		for (int size = 1; size <= 9; size++) {
			for (int i = 1; i <= 4; i++) {
				index++;
				room.mahjong.add(new Mahjong(index, 3, size));
			}
		}

//		for (int size = 2; size <= 3; size++) {
//			for (int i = 1; i <= 4; i++) {
//				index++;
//				room.com.oegame.tablegames.service.mahjong.add(new Mahjong(index, 5, size));
//			}
//		}

		if (room.roomSetting.isZhong) {
			// 装填红中
			for (int i = 1; i <= 4; i++) {
				index++;
				room.mahjong.add(new Mahjong(index, 5, 1));
			}
		}

		room.mahjongTotal = room.mahjong.size();

		Collections.shuffle(room.mahjong, new Random(TimeUtil.millisecond()));

		for (int i = 0; i < room.mahjong.size(); i++) {
			room.mahjong.get(i).index = i + 1;
		}

		boolean test = false;
		if (test) {

			HashMap<Integer, ArrayList<Mahjong>> hand = new HashMap<Integer, ArrayList<Mahjong>>();
			hand.put(1, Tools.mahjong(room.mahjong, "1_万", "1_万", "1_万", "1_万", "5_筒", "6_筒", "7_万", "8_万", "9_万", "9_筒",
					"9_筒", "9_筒", "4_筒"));
			hand.put(2, Tools.mahjong(room.mahjong, "2_筒", "2_筒", "4_筒", "7_筒", "2_条", "3_条", "5_条", "5_条", "5_条", "7_条",
					"8_条", "9_条", "3_万"));
			hand.put(3, Tools.mahjong(room.mahjong, "1_筒", "2_筒", "3_筒", "4_筒", "5_筒", "6_筒", "7_筒", "7_筒", "7_条", "7_条",
					"1_条", "2_条", "3_条"));
			hand.put(4, Tools.mahjong(room.mahjong, "1_条", "4_万", "7_万", "2_筒", "4_条", "7_条", "1_筒", "4_筒", "8_筒", "0_红中",
					"0_红中", "0_红中", "0_红中"));

			for (int i = 1; i <= room.roomSetting.player; i++) {
				Seat seat = room.seat.get(i);
				// seat.init();
				seat.gameStatus = MahjongSeatStatus.SEAT_STATUS_WAIT;

				logger.info("hand.get(i).size() : " + hand.get(i).size() + " | ");
				for (int j = 0; j < hand.get(i).size(); j++) {
					logger.info(", " + hand.get(i).get(j).index + this.format(hand.get(i).get(j)));
					hand.get(i).get(j).pos = seat.pos;
					seat.mahjong.put(hand.get(i).get(j).index, hand.get(i).get(j));
				}
				logger.info("\n--------------------------");
			}
		} else {
			// 发牌
			for (int i = 1; i <= room.roomSetting.player; i++) {
				Seat seat = room.seat.get(i);
				// seat.init();
				seat.gameStatus = MahjongSeatStatus.SEAT_STATUS_WAIT;

				logger.info("给" + seat.playerId + "发牌：");
				for (int x = 1; x <= room.roomSetting.mahjongAmount; x++) {
					Mahjong mahjong = room.mahjong.remove(0);
					mahjong.pos = seat.pos;
					logger.info(" , " + Tools.mahjong(mahjong.color, mahjong.size));

					seat.mahjong.put(mahjong.index, mahjong);
				}
				logger.info("");
			}
		}
	}

	/**
	 * 确定癞子
	 */
	public void getUniversal() {

		if (room.roomSetting.isZhong) {
			Mahjong sprite = Tools.mahjong("0_红中").get(0);
			logger.info("癞子是： " + Tools.mahjong(sprite.color, sprite.size));
			for (int i = 1; i <= room.roomSetting.player; i++) {
				room.seat.get(i).universal.add(sprite);
			}
		}
	}

	/**
	 * 发送开局信息
	 */
	public void sendBeginInfo() {
		this.room.gameStatus = MahjongGameStatus.ROOM_STATUS_BEGIN;
		this.beWait();

		Set<Entry<Long, Player>> set = this.room.player.entrySet();
		for (Entry<Long, Player> entry : set) {
			int playerId = (int) entry.getValue().playerId;
			s2c.sendBeginInfo(playerId);
		}
	}

	/**
	 * 摸一张牌
	 */
	public void getMahjong(int pos, boolean isLast) {

		// 获取当前出牌的人的位置
		Seat seat = room.seat.get(pos);

		room.paoshou = 0;
		// 重置当前的询问状态
		seat.askMahjongGroup.clear();

		// 摸一张牌
		seat.hitMahjong = room.mahjong.remove(0);
		seat.hitMahjong.pos = seat.pos;

		logger.info("玩家 " + seat.playerId + " 摸得牌是 " + this.format(seat.hitMahjong) + "|" + seat.hitMahjong.index);

		if (isLast) {
			room.gangshangpao = seat.pos;
		} else {
			room.gangshangpao = 0;
		}

		// 摸牌以后 清空操作集合
		room.fightSet.clear();

		// 已经摸牌
		seat.perflop = true;

		// 是否杠
		seat.isGang = isLast;

		// 设置成为上一家
		room.leftSeatPos = seat.pos;
		room.leftMahjong = seat.hitMahjong;

	}

	/**
	 * 出一张牌
	 */
	public void discard(int pos, int index) {

		// 取自己的状态
		Seat selfSeat = room.seat.get(pos);
		// 打出的牌
		Mahjong mahjong = null;

		// 如果是牌堆里面的牌，删除牌堆里面的，并加入刚摸的
		boolean returnMahjong = false;
		// 如果是牌堆里面的牌，删除牌堆里面的，并加入刚摸的
		if (selfSeat.mahjong.containsKey(index)) {
			returnMahjong = true;
			mahjong = selfSeat.mahjong.remove(index);
			if (selfSeat.hitMahjong != null) {
				selfSeat.mahjong.put(selfSeat.hitMahjong.index, selfSeat.hitMahjong);
			}
		} else {
			mahjong = selfSeat.hitMahjong;
		}

		logger.info(" pos " + selfSeat.pos + " ,玩家 " + selfSeat.playerId + " 出得牌是 " + this.format(mahjong));

		// 测试打出去的牌
		room.leftSeatPos = selfSeat.pos;
		room.leftMahjong = mahjong;

		// 出牌了，证明就不是杠了
		selfSeat.isGang = false;
		// 摸过牌了
		selfSeat.perflop = false;

		selfSeat.hitMahjong = null;

		selfSeat.desktop.add(mahjong);

		// 当前第几手
		room.handCount++;
	}

	/**
	 * 下一个出牌的位置
	 */
	public int nextPos(int pos) {
		pos = pos + 1;
		if (pos > room.roomSetting.player) {
			pos = 1;
		}
		return pos;
	}

	/**
	 * 重置所有人的出牌状态都是WAIT
	 */
	public void beWait() {
		for (int i = 1; i <= room.roomSetting.player; i++) {
			room.seat.get(i).gameStatus = MahjongSeatStatus.SEAT_STATUS_WAIT;
		}
	}

	/**
	 * 重置所有人座位状态都是fight
	 */
	public void beFight() {
		for (int i = 1; i <= room.roomSetting.player; i++) {
			room.seat.get(i).gameStatus = MahjongSeatStatus.SEAT_STATUS_FIGHT;
		}
	}

	/**
	 * 格式牌
	 */
	public String format(Mahjong mahjong) {
		return Tools.mahjong(mahjong.color, mahjong.size);
	}

	/**
	 * 两张牌是否相等
	 */
	public boolean equal(Mahjong mahjong1, Mahjong mahjong2) {
		if (mahjong1.color == mahjong2.color && mahjong1.size == mahjong2.size) {
			return true;
		}
		return false;
	}

	/**
	 * 打印座位信息
	 */
	public void look(Seat seat) {

		Set<Entry<Integer, Mahjong>> set = seat.mahjong.entrySet();
		System.out.print(seat.playerId + "的手牌: ");
		for (Entry<Integer, Mahjong> entry : set) {
			System.out.print(", " + this.format(entry.getValue()));
		}
		System.out.println("\n-------------");

		System.out.print(seat.playerId + "使用的牌: ");
		for (int i = 0; i < seat.useMahjongGroup.size(); i++) {
			System.out.print("TypesId: " + seat.useMahjongGroup.get(i).typeId);
			for (int j = 0; j < seat.useMahjongGroup.get(i).mahjong.size(); j++) {
				Mahjong pk = seat.useMahjongGroup.get(i).mahjong.get(j);
				System.out.print(" , " + this.format(pk) + "|" + pk.index);
			}
			System.out.println("");
		}
		System.out.println("\n-------------");

		System.out.print(seat.playerId + "桌面上的牌: ");
		for (int i = 0; i < seat.desktop.size(); i++) {
			System.out.print(" , " + this.format(seat.desktop.get(i)) + "|" + seat.desktop.get(i).index);
		}
		System.out.println("\n-------------");
	}

	/**
	 * 摸牌之后,检测自己是否能杠胡
	 */
	public void myCheck(int pos) {

		// 获取当前出牌的人的位置
		Seat seat = room.seat.get(pos);

		// 判断是否有胡，暗杠的状态
		ArrayList<MahjongGroup> ask_mahjong_group = new ArrayList<MahjongGroup>();
		// 这里是自己摸的牌胡牌了，自摸了，可能是杠上开花
		if (this.isHu(seat, seat.hitMahjong, true, room.roomSetting)) {
			logger.info(" pos " + seat.pos + " 可以自摸,胡的牌是" + this.format(seat.hitMahjong));
			ask_mahjong_group.add(new MahjongGroup(CombinationType.POKER_TYPE_HU_ZIMO, seat.hitMahjong));
		}

		// 判断杠
		if (room.roomSetting.isKong) {
			if (this.isGang(seat.mahjong, seat.hitMahjong, true)) {
				logger.info(" pos " + seat.pos + " 可以暗杠,暗杠的牌是" + this.format(seat.hitMahjong));
				ask_mahjong_group.add(new MahjongGroup(CombinationType.POKER_TYPE_GANG,
						GangSubType.POKER_SUBTYPE_GANG_AN.ordinal(), null));
			}
		}

		// 判断补杠
		if (room.roomSetting.isKong) {
			if (this.isBuGang(seat.mahjong, seat.useMahjongGroup, seat.hitMahjong, false)) {
				logger.info(" pos " + seat.pos + " 可以补杠,补杠的牌是" + this.format(seat.hitMahjong));
				ask_mahjong_group.add(new MahjongGroup(CombinationType.POKER_TYPE_GANG,
						GangSubType.POKER_SUBTYPE_GANG_BU.ordinal(), null));
			}
		}

		// 如果自己能杠，胡，询问是否要这么操作
		if (ask_mahjong_group.size() > 0) {
			logger.info("我自己摸的牌,增加了杠,胡");
			askOperate(seat.pos, ask_mahjong_group);
		}
	}

	/**
	 * 出牌之后,检测其他人是否有操作
	 */
	public void otherCheck(int pos, Mahjong mahjong) {

		// 遍历所有人，是否有吃，碰，杠，胡的情况

		for (int i = 1; i <= room.roomSetting.player; i++) {
			if (pos != i) {
				ArrayList<MahjongGroup> ask_mahjong_group = new ArrayList<MahjongGroup>();

				Seat seat = room.seat.get(i);
				// 重置询问状态
				seat.askMahjongGroup.clear();

				// 吃
				if (room.roomSetting.isChow) {
					int nextPos = this.nextPos(pos);
					if (nextPos == i && this.isChi(seat.mahjong, seat.universal, mahjong)) {
						logger.info(" pos " + seat.pos + " 可以吃,吃的牌是" + this.format(mahjong));
						ask_mahjong_group.add(new MahjongGroup(CombinationType.POKER_TYPE_CHI, mahjong));
					}
				}

				// 碰
				if (room.roomSetting.isPong && this.isPeng(seat.mahjong, mahjong)) {
					logger.info(" pos " + seat.pos + " 可以碰,碰的牌是" + this.format(mahjong));
					ask_mahjong_group.add(new MahjongGroup(CombinationType.POKER_TYPE_PENG, mahjong));
				}

				// 明杠
				if (room.roomSetting.isKong && this.isGang(seat.mahjong, mahjong, false)) {
					logger.info(" pos " + seat.pos + " 可以明杠,明杠的牌是" + this.format(mahjong));
					ask_mahjong_group.add(new MahjongGroup(CombinationType.POKER_TYPE_GANG,
							GangSubType.POKER_SUBTYPE_GANG_MING.ordinal(), mahjong));
				}

				if (room.roomSetting.isWBD) {
					if (this.isHu(seat, mahjong, false, room.roomSetting)) {
						logger.info(" pos " + seat.pos + " 可以屁胡,胡的牌是" + this.format(mahjong));
						room.paoshou = pos;
						ask_mahjong_group.add(new MahjongGroup(CombinationType.POKER_TYPE_HU_PAO, mahjong));
					}
				}

				if (ask_mahjong_group.size() > 0) {
					this.askOperate(i, ask_mahjong_group);
				}
			}
		}
	}

	/**
	 * 
	 * @param pos
	 * @param type
	 * @param index
	 * @return
	 */
	public boolean fight(int pos, CombinationType type, List<Integer> index) {

		FightSet fightSet = room.fightSet.get(pos);

		fightSet.index = (ArrayList<Integer>) index;
		fightSet.typeId = type;
		fightSet.playerId = (int) room.seat.get(pos).playerId;

		logger.info("我要操作的：pos " + pos + " typeId: " + fightSet.typeId);

		Set<Entry<Integer, FightSet>> set = room.fightSet.entrySet();

		for (Entry<Integer, FightSet> entry : set) {
			if (entry.getValue().typeId == CombinationType.POKER_TYPE_NULL) {
				if (this.isWait(fightSet.typeId, entry.getValue().group)) {
					logger.info("我要执行操作，还有人优先级比我高,等待他的操作。");
					s2c.waitOperate(room.seat.get(pos).playerId);
					return false;
				}
			}
		}
		return true;
	}

	/**
	 * 检测胡
	 */
	public boolean isHu(Seat seat, Mahjong test, boolean isSelf, RoomSetting roomSetting) {

		if (this.helper.checkHu(seat, test, isSelf, roomSetting, false).code > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 检测杠
	 */
	public boolean isGang(HashMap<Integer, Mahjong> hand, Mahjong test, boolean isSelf) {

		if (helper.checkGang(hand, test, isSelf)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 检测补杠
	 */
	public boolean isBuGang(HashMap<Integer, Mahjong> mahjong, ArrayList<MahjongGroup> useMahjongGroup, Mahjong test,
			boolean isSelf) {

		if (helper.checkBuGang(mahjong, useMahjongGroup, test, isSelf) >= 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 检测吃
	 */
	public boolean isChi(HashMap<Integer, Mahjong> mahjong, ArrayList<Mahjong> sprite, Mahjong test) {

		if (helper.checkChi(mahjong, sprite, test).size() > 0) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 检测碰
	 */
	public boolean isPeng(HashMap<Integer, Mahjong> mahjong, Mahjong test) {
		if (helper.checkPeng(mahjong, test)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 操作的优先级
	 */
	public int weight(CombinationType typeId) {

		if (typeId == CombinationType.POKER_TYPE_HU_PAO || typeId == CombinationType.POKER_TYPE_HU_ZIMO) {
			return 10;
		} else if (typeId == CombinationType.POKER_TYPE_GANG) {
			return 8;
		} else if (typeId == CombinationType.POKER_TYPE_PENG) {
			return 6;
		} else if (typeId == CombinationType.POKER_TYPE_CHI) {
			return 4;
		}

		return 0;
	}

	/**
	 * 优先级,是否需要等待优先级高的人操作
	 */
	public boolean isWait(CombinationType typeId, ArrayList<MahjongGroup> group) {

		int weight = this.weight(typeId);

		for (int i = 0; i < group.size(); i++) {
			if (this.weight(group.get(i).typeId) >= weight) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 是否能够抢杠
	 */
	public boolean isLoot(int pos, Mahjong useMahjong, MahjongGroup pb_mahjong_group, boolean isHit, int usePos) {

		// 补杠成功 清空操作集合
		room.fightSet.clear();

		for (int i = 1; i <= room.roomSetting.player; i++) {
			if (pos != i) {
				ArrayList<MahjongGroup> ask_mahjong_group = new ArrayList<MahjongGroup>();

				Seat askSeat = room.seat.get(i);
				// 重置询问状态
				askSeat.askMahjongGroup.clear();
				// 有人出牌，问是否要胡牌，肯定是屁胡
				if (room.roomSetting.isWBD
					&& helper.checkHu(askSeat, useMahjong, false, room.roomSetting, false).code > 0) {
					logger.info(" pos " + askSeat.pos + " 可以抢杠胡,胡的牌是" + this.format(useMahjong));
					room.paoshou = pos;
					ask_mahjong_group.add(new MahjongGroup(CombinationType.POKER_TYPE_HU_PAO, useMahjong));
				}
				if (ask_mahjong_group.size() > 0) {
					this.askOperate(askSeat.pos, ask_mahjong_group);
				}
			}
		}

		if (room.fightSet.size() > 0) {
			room.leftMahjong = useMahjong;
			room.buGang = pb_mahjong_group;
			room._isHit = isHit;
			room._seat = room.seat.get(pos);
			room._pb_mahjong_group = pb_mahjong_group;
			room._gpos = usePos;
			return false;
		}
		return true;
	}

	/**
	 * 执行暗杠
	 */
	public MahjongGroup doAnGang(int pos, ArrayList<Integer> index) {

		Seat seat = room.seat.get(pos);

		MahjongGroup pb_group = new MahjongGroup();
		pb_group.playerId = seat.playerId;
		pb_group.typeId = CombinationType.POKER_TYPE_GANG;
		pb_group.subTypeId = GangSubType.POKER_SUBTYPE_GANG_AN.ordinal();

		if (seat.hitMahjong != null) {
			seat.mahjong.put(seat.hitMahjong.index, seat.hitMahjong);
		}

		// 删除手里的牌
		for (int i = 0; i < index.size(); i++) {
			Mahjong useMahjong = seat.mahjong.remove(index.get(i));
			pb_group.mahjong.add(useMahjong);
		}

		seat.useMahjongGroup.add(pb_group);

		seat.isGang = true;

		seat.agangCount++;

		// 暗杠记录收益
		seat.gangIncomes.add(new GangGroup(1, 0));

		return pb_group;

	}

	/**
	 * 执行吃牌
	 */
	public MahjongGroup doChi(int pos, ArrayList<Integer> index, Mahjong leftMahjong) {

		Seat seat = room.seat.get(pos);
		MahjongGroup pb_group = new MahjongGroup();
		pb_group.playerId = seat.playerId;
		pb_group.typeId = (CombinationType.POKER_TYPE_CHI);

		ArrayList<Integer> list = new ArrayList<Integer>();
		HashMap<Integer, Integer> chi = new HashMap<Integer, Integer>();
		list.add(leftMahjong.size);
		chi.put(leftMahjong.size, leftMahjong.index);
		for (int i = 0; i < index.size(); i++) {
			if (index.get(i) != leftMahjong.index) {
				if (seat.mahjong.get(index.get(i)).color == leftMahjong.color) {
					list.add(seat.mahjong.get(index.get(i)).size);
					chi.put(seat.mahjong.get(index.get(i)).size, seat.mahjong.get(index.get(i)).index);
				}
			}
		}

		Collections.sort(list);
		if (list.get(0) + 1 == list.get(1) && list.get(0) + 2 == list.get(2)) {

		} else {
			logger.info("不是顺子,不能吃 " + index.size());
			return pb_group;
		}

		for (int i = 0; i < list.size(); i++) {
			if (chi.get(list.get(i)) == leftMahjong.index) {
				pb_group.mahjong.add(leftMahjong);
			} else {
				Mahjong useMahjong = seat.mahjong.remove(chi.get(list.get(i)));
				pb_group.mahjong.add(useMahjong);
			}
		}

		seat.useMahjongGroup.add(pb_group);

		// 重置所有人的出牌状态都是WAIT
		this.beWait();

		seat.gameStatus = MahjongSeatStatus.SEAT_STATUS_OPERATE;

		seat.countdown = TimeUtil.millisecond() + SEAT_WAIT_TIME;

		seat.hitMahjong = null;

		room.nextPos = seat.pos;

		this.look(seat);

		// 删除上一家的桌面牌
		room.seat.get(room.leftSeatPos).desktop.remove(room.seat.get(room.leftSeatPos).desktop.size() - 1);
		return pb_group;

	}

	/**
	 * 执行碰牌
	 */
	public MahjongGroup doPeng(Seat seat, ArrayList<Integer> index, Mahjong leftMahjong) {

		MahjongGroup pb_group = new MahjongGroup();
		pb_group.playerId = seat.playerId;
		pb_group.typeId = (CombinationType.POKER_TYPE_PENG);

		for (int i = 0; i < index.size(); i++) {
			if (seat.mahjong.containsKey(index.get(i))) {
				Mahjong useMahjong = seat.mahjong.remove(index.get(i));
				pb_group.mahjong.add(useMahjong);
			}
		}

		pb_group.mahjong.add(leftMahjong);

		seat.useMahjongGroup.add(pb_group);

		// 重置所有人的出牌状态都是WAIT
		this.beWait();
		seat.gameStatus = MahjongSeatStatus.SEAT_STATUS_OPERATE;
		seat.countdown = TimeUtil.millisecond() + SEAT_WAIT_TIME;
		seat.hitMahjong = null;
		room.nextPos = seat.pos;
		this.look(seat);
		// 删除上一家的桌面牌
		room.seat.get(room.leftSeatPos).desktop.remove(room.seat.get(room.leftSeatPos).desktop.size() - 1);
		return pb_group;
	}

	/**
	 * 执行明杠
	 */
	public MahjongGroup doMingGang(Seat seat, ArrayList<Integer> index, Mahjong leftMahjong) {

		MahjongGroup pb_group = new MahjongGroup();
		pb_group.playerId = seat.playerId;
		pb_group.typeId = CombinationType.POKER_TYPE_GANG;
		pb_group.subTypeId = GangSubType.POKER_SUBTYPE_GANG_MING.ordinal();

		pb_group.mahjong.add(leftMahjong);

		for (int i = 0; i < index.size(); i++) {
			if (seat.mahjong.containsKey(index.get(i))) {
				Mahjong useMahjong = seat.mahjong.remove(index.get(i));
				pb_group.mahjong.add(useMahjong);
			}
		}

		seat.useMahjongGroup.add(pb_group);

		// 重置所有人的出牌状态都是WAIT
		this.beWait();
		seat.isGang = true;
		seat.mgangCount++;
		// 明杠记录收益
		seat.gangIncomes.add(new GangGroup(2, room.leftSeatPos));
		// 删除上一家的桌面牌
		room.seat.get(room.leftSeatPos).desktop.remove(room.seat.get(room.leftSeatPos).desktop.size() - 1);
		return pb_group;
	}

	/**
	 * 继续补杠
	 */
	public void continueBugang(boolean isHit, Seat seat, MahjongGroup pb_mahjong_group, int usePos) {
		// 重置所有人的出牌状态都是WAIT
		for (int i = 1; i <= room.roomSetting.player; i++) {
			room.seat.get(i).gameStatus = MahjongSeatStatus.SEAT_STATUS_WAIT;
		}

		if (usePos >= 0) {
			seat.mahjong.put(seat.hitMahjong.index, seat.hitMahjong);
			for (int i = 0; i < pb_mahjong_group.index.size(); i++) {
				if (seat.mahjong.containsKey(pb_mahjong_group.index.get(i))) {
					seat.mahjong.remove(pb_mahjong_group.index.get(i));
				}
			}
		}

		room.buGang = null;

		seat.perflop = false;

		seat.isGang = true;

		seat.bgangCount++;

		seat.gangIncomes.add(new GangGroup(3, 0));
	}

	// 询问操作
	protected void askOperate(int pos, ArrayList<MahjongGroup> ask_mahjong_group) {

		if (ask_mahjong_group == null || ask_mahjong_group.size() == 0)
			return;
		Seat seat = this.room.getSeatByPos(pos);
		if (seat == null || seat.player == null)
			return;
		seat.gameStatus = MahjongSeatStatus.SEAT_STATUS_FIGHT;
		seat.askMahjongGroup = ask_mahjong_group;
		seat.countdown = TimeUtil.millisecond() + SEAT_WAIT_TIME;

		s2c.operateAsk(seat, ask_mahjong_group);
		room.fightSet.put(seat.pos, new FightSet(ask_mahjong_group));
	}

	/**
	 * 扣除房卡
	 * 
	 * @param winer
	 *            void
	 */
	public String costCard(int winer) {

		// HashMap<Long, Integer> playerIds = new HashMap<Long, Integer>();

		String players = "";
		String nickname = "";
		String avatar = "";

		for (int i = 1; i <= room.roomSetting.player; i++) {
			if (room.player.get(room.seat.get(i).playerId) != null) {
				nickname = room.player.get(room.seat.get(i).playerId).nickname;
				avatar = room.player.get(room.seat.get(i).playerId).avatar;
			} else {
				nickname = "";
				avatar = "";
			}

			String mahjongs = "";

			for (Entry<Integer, Mahjong> entry : room.seat.get(i).mahjong.entrySet()) {
				mahjongs += entry.getValue().color + "|" + entry.getValue().size + ",";
			}

			for (int p = 0; p < room.seat.get(i).useMahjongGroup.size(); p++) {
				for (int g = 0; g < room.seat.get(i).useMahjongGroup.get(p).mahjong.size(); g++) {
					mahjongs += room.seat.get(i).useMahjongGroup.get(p).mahjong.get(g).color + "|"
							+ room.seat.get(i).useMahjongGroup.get(p).mahjong.get(g).size + ",";
				}
			}
			
			players += nickname + "|-|" + room.seat.get(i).playerId + "|-|" + room.seat.get(i).settle + "|-|" + mahjongs
					+ "|-|" + avatar + ";";

		}
		return players;
	}

	// 结算
	public void settle(int winer, int loser, boolean isSelf, boolean last, SettleVO res) {

		settle.huScore(winer, loser, isSelf, res, room);

		if (last) {
			settle.gangScore(room);
			settle.huSettle(winer, loser, isSelf, room);

			for (int i = 1; i <= room.roomSetting.player; i++) {
				room.seat.get(i).settle *= room.baseScore;
				room.seat.get(i).gold += room.seat.get(i).settle;
				room.seat.get(i).status = SeatStatus.SEAT_STATUS_IDLE;
			}
			
			for (int i = 1; i <= room.roomSetting.player; i++) {
				Seat seat = room.seat.get(i);
				seat.incomesDesc += " " + " score = " + seat.settle;
			}
		}
	}

	// 获得当前房间人数
	public int playerCount() {
		int count = 0;
		for (int i = 1; i <= this.room.roomSetting.player; i++) {
			if (room.seat.get(i).player != null && room.seat.get(i).playerId > 0) {
				count++;
			}
		}
		return count;
	}
}
