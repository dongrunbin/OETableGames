package com.oegame.tablegames.service.game.mahjong;

import com.oegame.tablegames.service.game.RoomStatus;
import com.oegame.tablegames.service.game.SeatStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.oegame.tablegames.service.game.IGameAI;
import com.oegame.tablegames.service.game.RoomCtrlBase;
import com.oegame.tablegames.service.game.mahjong.model.CombinationType;
import com.oegame.tablegames.service.game.mahjong.model.MahjongGameStatus;
import com.oegame.tablegames.service.game.mahjong.model.MahjongSeatStatus;
import com.oegame.tablegames.service.game.mahjong.model.Poker;
import com.oegame.tablegames.service.game.mahjong.model.PokerGroup;
import com.oegame.tablegames.service.game.mahjong.model.Seat;

/**
 * 麻将出牌ai算法
 *
 */
public class GameMahjongAI implements IGameAI {

	private RoomCtrl ctrl;

	private MahjongDiscardAi discardAi = new MahjongDiscardAi();

	private static final Logger logger = LoggerFactory.getLogger(GameMahjongAI.class);

	@Override
	public void setRoomCtrl(RoomCtrlBase ctrl) {
		this.ctrl = (RoomCtrl) ctrl;
	}

	@Override
	public long getTimeSpan() {
		return 2500;
	}

	public void doAI() {
		if (ctrl == null || ctrl.getRoom() == null) {
			return;
		}
		for (Map.Entry<Integer, Seat> entry : ctrl.getRoom().seat.entrySet()) {

			Seat seat = entry.getValue();

			if (seat == null || seat.playerId <= 0) {
				continue;
			}
			if(!seat.player.isRobot) return;
			if (ctrl.getRoom().status == RoomStatus.ROOM_STATUS_READY && seat.status != SeatStatus.SEAT_STATUS_READY
					&& !seat.isLeave) {
				ctrl.ready(seat.playerId);
				continue;
			}

			if (ctrl.getRoom().status != RoomStatus.ROOM_STATUS_BEGIN) {
				continue;
			}

			if (this.ctrl.getRoom().gameStatus != MahjongGameStatus.ROOM_STATUS_BEGIN) {
				logger.info("房间不在begin状态,");
				return;
			}

			if (!(seat.gameStatus == MahjongSeatStatus.SEAT_STATUS_FIGHT
					|| seat.gameStatus == MahjongSeatStatus.SEAT_STATUS_OPERATE)) {
				logger.info("你不是fight,也不是operate,下一个" + seat.playerId);
				continue;
			}

			if (seat.askPokerGroup.size() > 0 && seat.gameStatus == MahjongSeatStatus.SEAT_STATUS_FIGHT) {
				ArrayList<Integer> index = new ArrayList<Integer>();
				if (this.isHu(seat.askPokerGroup)) {// 胡牌
					if (ctrl.getRoom().leftSeatPos == seat.pos && seat.hitPoker != null) {
						index.add(seat.hitPoker.index);
						ctrl.operate(seat.playerId, CombinationType.POKER_TYPE_HU_ZIMO, index);
						continue;
					} else {
						index.add(ctrl.getRoom().leftPoker.index);
						ctrl.operate(seat.playerId, CombinationType.POKER_TYPE_HU_PAO, index);
						continue;
					}
				}
				if (this.isGang(seat.askPokerGroup) >= 0) {// 杠
					logger.info("ai执行杠");
					int useIndex = this.isGang(seat.askPokerGroup);
					if (ctrl.getRoom().leftSeatPos == seat.pos && seat.hitPoker != null) {
						if (seat.askPokerGroup.get(useIndex).subTypeId == 2) {// 补杠
							index.add(seat.hitPoker.index);
							ctrl.operate(seat.playerId, CombinationType.POKER_TYPE_GANG, index);
							continue;
						} else if (seat.askPokerGroup.get(useIndex).subTypeId == 1) {// 暗杠
							ArrayList<Poker> poker = new ArrayList<>();
							if (seat.hitPoker != null) {
								poker.add(seat.hitPoker);
							}
							for (Entry<Integer, Poker> entry1 : seat.poker.entrySet()) {
								poker.add(entry1.getValue());
							}

							LinkedHashMap<Integer, Poker> array = new LinkedHashMap<Integer, Poker>();

							// 装填到hashMap
							for (int i = 0; i < poker.size(); i++) {
								if (this.isUniversal(seat.universal, poker.get(i))) {
									continue;
								}
								int size = poker.get(i).size;
								int color = poker.get(i).color;

								int tags = (size << 8) + color;

								if (array.containsKey(tags)) {
									array.get(tags).amount = array.get(tags).amount + 1;
								} else {
									poker.get(i).amount = 1;
									array.put(tags, poker.get(i));
								}
							}

							for (Entry<Integer, Poker> entry2 : array.entrySet()) {
								if (entry2.getValue().amount >= 4) {
									for (int i = 0; i < poker.size(); i++) {
										if (entry2.getValue().color == poker.get(i).color
												&& entry2.getValue().size == poker.get(i).size) {
											index.add(poker.get(i).index);
											logger.info("index.size" + index.size());
											if (index.size() >= 4) {
												ctrl.operate(seat.playerId, CombinationType.POKER_TYPE_GANG, index);
												continue;
											}
										}
									}
								}
							}
						}
					} else {
						if (seat.askPokerGroup.get(useIndex).subTypeId == 0) {// 明杠
							logger.info("ai,明杠");
							if (ctrl.getRoom().leftPoker != null) {
								index.add(ctrl.getRoom().leftPoker.index);
							}
							for (Entry<Integer, Poker> entry1 : seat.poker.entrySet()) {
								if (ctrl.model.equal(ctrl.getRoom().leftPoker, entry1.getValue())) {
									index.add(entry1.getValue().index);
								}
							}
							if (index.size() >= 4) {
								// 杠之前胡牌所需缺省值
								int lack1 = discardAi.lackCount(seat.poker, seat.universal, null,
										ctrl.getRoom().roomSetting);

								HashMap<Integer, Poker> hand = new HashMap<>();
								for (Entry<Integer, Poker> entry1 : seat.poker.entrySet()) {
									if (!index.contains(entry1.getValue().index)) {
										hand.put(entry1.getValue().index, entry1.getValue());
									}
								}
								// 杠之后胡牌所需缺省值
								int lack2 = discardAi.lackCount(hand, seat.universal, null, ctrl.getRoom().roomSetting);
								if (lack2 <= lack1) {
									ctrl.operate(seat.playerId, CombinationType.POKER_TYPE_GANG, index);
									continue;
								} else {
									ctrl.pass(seat.playerId);
								}
							}
						}
					}
				}
				if (this.isPeng(seat.askPokerGroup)) {// 碰
					if (ctrl.getRoom().leftPoker != null) {
						index.add(ctrl.getRoom().leftPoker.index);
					}
					for (Entry<Integer, Poker> entry1 : seat.poker.entrySet()) {
						if (ctrl.model.equal(ctrl.getRoom().leftPoker, entry1.getValue())) {
							index.add(entry1.getValue().index);
							if (index.size() >= 3) {
								break;
							}
						}
					}
					if (index.size() >= 3) {
						// 碰之前胡牌所需缺省值
						int lack1 = discardAi.lackCount(seat.poker, seat.universal, null, ctrl.getRoom().roomSetting);

						HashMap<Integer, Poker> hand = new HashMap<>();
						for (Entry<Integer, Poker> entry1 : seat.poker.entrySet()) {
							if (!index.contains(entry1.getValue().index)) {
								hand.put(entry1.getValue().index, entry1.getValue());
							}
						}
						// 碰之后胡牌所需缺省值
						int lack2 = discardAi.lackCount(hand, seat.universal, null, ctrl.getRoom().roomSetting);
						if (lack2 <= lack1) {
							ctrl.operate(seat.playerId, CombinationType.POKER_TYPE_PENG, index);
							continue;
						} else {
							ctrl.pass(seat.playerId);
						}
					}
				}
				if (this.isChi(seat.askPokerGroup)) {// 吃
					Poker[] pk = ctrl.mahjong.checkChi(seat.poker, seat.universal, ctrl.getRoom().leftPoker).get(0);
					for (int i = 0; i < pk.length; i++) {
						index.add(pk[i].index);
					}
					if (ctrl.getRoom().leftPoker != null) {
						index.add(ctrl.getRoom().leftPoker.index);
					}
					if (index.size() >= 3) {
						ctrl.operate(seat.playerId, CombinationType.POKER_TYPE_CHI, index);
						continue;
					}
				} else {
					logger.info("未定义的操作,return");
				}
			}

			// 出牌
			else if (seat.gameStatus == MahjongSeatStatus.SEAT_STATUS_OPERATE) {
				int index = discardAi.discardPoker(seat, ctrl.getRoom().roomSetting);
				ctrl.discard(seat.playerId, index);
				return;
			}
		}
	}

	// 操作时间到,出牌或者过
	public void trustee(Seat seat) {

		if (ctrl.getRoom().status != RoomStatus.ROOM_STATUS_BEGIN) {
			return;
		}

		if (seat.status != SeatStatus.SEAT_STATUS_GAMING) {
			logger.info("玩家不在gaming状态," + seat.playerId);
			return;
		}

		if (seat.askPokerGroup.size() > 0 && seat.gameStatus == MahjongSeatStatus.SEAT_STATUS_FIGHT) {
			ArrayList<Integer> index = new ArrayList<Integer>();
			if (this.isHu(seat.askPokerGroup)) {// 胡牌
				if (ctrl.getRoom().leftSeatPos == seat.pos && seat.hitPoker != null) {
					index.add(seat.hitPoker.index);
					ctrl.operate(seat.playerId, CombinationType.POKER_TYPE_HU_ZIMO, index);
					return;
				} else {
					index.add(ctrl.getRoom().leftPoker.index);
					ctrl.operate(seat.playerId, CombinationType.POKER_TYPE_HU_PAO, index);
					return;
				}
			}else {
				logger.info("操作时间到,自动pass" + seat.playerId);
				ctrl.pass(seat.playerId);
				return;
			}			
		}

		if (seat.gameStatus == MahjongSeatStatus.SEAT_STATUS_OPERATE) {
			logger.info("操作时间到,自动出牌" + seat.playerId);
			if (seat.hitPoker != null) {
				ctrl.discard(seat.playerId, seat.hitPoker.index);
				return;
			}else {
				for (Entry<Integer, Poker> entry : seat.poker.entrySet()) {
					ctrl.discard(seat.playerId, entry.getValue().index);
					return;
				}
			}			
		}
	}

	public boolean isHu(ArrayList<PokerGroup> askPokerGroup) {
		for (int i = 0; i < askPokerGroup.size(); i++) {
			if (askPokerGroup.get(i).typeId == CombinationType.POKER_TYPE_HU_PAO
					|| askPokerGroup.get(i).typeId == CombinationType.POKER_TYPE_HU_ZIMO) {
				return true;
			}
		}
		return false;
	}

	public int isGang(ArrayList<PokerGroup> askPokerGroup) {
		for (int i = 0; i < askPokerGroup.size(); i++) {
			if (askPokerGroup.get(i).typeId == CombinationType.POKER_TYPE_GANG) {
				return i;
			}
		}
		return -1;
	}

	public boolean isPeng(ArrayList<PokerGroup> askPokerGroup) {
		for (int i = 0; i < askPokerGroup.size(); i++) {
			if (askPokerGroup.get(i).typeId == CombinationType.POKER_TYPE_PENG) {
				return true;
			}
		}
		return false;
	}

	public boolean isChi(ArrayList<PokerGroup> askPokerGroup) {
		for (int i = 0; i < askPokerGroup.size(); i++) {
			if (askPokerGroup.get(i).typeId == CombinationType.POKER_TYPE_CHI) {
				return true;
			}
		}
		return false;
	}

	public boolean isUniversal(ArrayList<Poker> sprite, Poker poker) {
		for (int i = 0; i < sprite.size(); i++) {
			if (poker.size == sprite.get(i).size && poker.color == sprite.get(i).color) {
				return true;
			}
		}
		return false;
	}

}
