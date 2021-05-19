package com.oegame.tablegames.service.mahjong;

import com.oegame.tablegames.common.log.Logger;
import com.oegame.tablegames.common.log.LoggerFactory;
import com.oegame.tablegames.service.game.RoomStatus;
import com.oegame.tablegames.service.game.SeatStatus;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import com.oegame.tablegames.service.mahjong.model.CombinationType;
import com.oegame.tablegames.service.mahjong.model.Mahjong;
import com.oegame.tablegames.service.mahjong.model.MahjongGameStatus;
import com.oegame.tablegames.service.mahjong.model.MahjongGroup;
import com.oegame.tablegames.service.mahjong.model.MahjongSeatStatus;
import com.oegame.tablegames.service.mahjong.model.Seat;
import com.oegame.tablegames.service.game.IGameAI;
import com.oegame.tablegames.service.game.RoomCtrlBase;

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

			if (seat == null || seat.playerId == 0) {
				continue;
			}
			if(!seat.player.isRobot) continue;
			if (seat.status == SeatStatus.SEAT_STATUS_IDLE) {
				ctrl.ready(seat.playerId);
				continue;
			}

			if (!(seat.gameStatus == MahjongSeatStatus.SEAT_STATUS_OPERATION
					|| seat.gameStatus == MahjongSeatStatus.SEAT_STATUS_DISCARD)) {
				continue;
			}

			if (seat.askMahjongGroup.size() > 0 && seat.gameStatus == MahjongSeatStatus.SEAT_STATUS_OPERATION) {
				ArrayList<Integer> index = new ArrayList<Integer>();
				if (this.isHu(seat.askMahjongGroup)) {// 胡牌
					if (ctrl.getRoom().leftSeatPos == seat.pos && seat.hitMahjong != null) {
						index.add(seat.hitMahjong.index);
						ctrl.operate(seat.playerId, CombinationType.POKER_TYPE_HU_ZIMO, index);
						continue;
					} else {
						index.add(ctrl.getRoom().leftMahjong.index);
						ctrl.operate(seat.playerId, CombinationType.POKER_TYPE_HU_PAO, index);
						continue;
					}
				}
				if (this.isGang(seat.askMahjongGroup) >= 0) {// 杠
					logger.info("ai执行杠");
					int useIndex = this.isGang(seat.askMahjongGroup);
					if (ctrl.getRoom().leftSeatPos == seat.pos && seat.hitMahjong != null) {
						if (seat.askMahjongGroup.get(useIndex).subTypeId == 2) {// 补杠
							index.add(seat.hitMahjong.index);
							ctrl.operate(seat.playerId, CombinationType.POKER_TYPE_GANG, index);
							continue;
						} else if (seat.askMahjongGroup.get(useIndex).subTypeId == 1) {// 暗杠
							ArrayList<Mahjong> mahjong = new ArrayList<>();
							if (seat.hitMahjong != null) {
								mahjong.add(seat.hitMahjong);
							}
							for (Entry<Integer, Mahjong> entry1 : seat.mahjong.entrySet()) {
								mahjong.add(entry1.getValue());
							}

							LinkedHashMap<Integer, Mahjong> array = new LinkedHashMap<Integer, Mahjong>();

							// 装填到hashMap
							for (int i = 0; i < mahjong.size(); i++) {
								if (this.isUniversal(seat.universal, mahjong.get(i))) {
									continue;
								}
								int size = mahjong.get(i).size;
								int color = mahjong.get(i).color;

								int tags = (size << 8) + color;

								if (array.containsKey(tags)) {
									array.get(tags).amount = array.get(tags).amount + 1;
								} else {
									mahjong.get(i).amount = 1;
									array.put(tags, mahjong.get(i));
								}
							}

							for (Entry<Integer, Mahjong> entry2 : array.entrySet()) {
								if (entry2.getValue().amount >= 4) {
									for (int i = 0; i < mahjong.size(); i++) {
										if (entry2.getValue().color == mahjong.get(i).color
												&& entry2.getValue().size == mahjong.get(i).size) {
											index.add(mahjong.get(i).index);
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
						if (seat.askMahjongGroup.get(useIndex).subTypeId == 0) {// 明杠
							logger.info("ai,明杠");
							if (ctrl.getRoom().leftMahjong != null) {
								index.add(ctrl.getRoom().leftMahjong.index);
							}
							for (Entry<Integer, Mahjong> entry1 : seat.mahjong.entrySet()) {
								if (ctrl.model.equal(ctrl.getRoom().leftMahjong, entry1.getValue())) {
									index.add(entry1.getValue().index);
								}
							}
							if (index.size() >= 4) {
								// 杠之前胡牌所需缺省值
								int lack1 = discardAi.lackCount(seat.mahjong, seat.universal, null,
										ctrl.getRoom().roomSetting);

								HashMap<Integer, Mahjong> hand = new HashMap<>();
								for (Entry<Integer, Mahjong> entry1 : seat.mahjong.entrySet()) {
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
				if (this.isPeng(seat.askMahjongGroup)) {// 碰
					if (ctrl.getRoom().leftMahjong != null) {
						index.add(ctrl.getRoom().leftMahjong.index);
					}
					for (Entry<Integer, Mahjong> entry1 : seat.mahjong.entrySet()) {
						if (ctrl.model.equal(ctrl.getRoom().leftMahjong, entry1.getValue())) {
							index.add(entry1.getValue().index);
							if (index.size() >= 3) {
								break;
							}
						}
					}
					if (index.size() >= 3) {
						// 碰之前胡牌所需缺省值
						int lack1 = discardAi.lackCount(seat.mahjong, seat.universal, null, ctrl.getRoom().roomSetting);

						HashMap<Integer, Mahjong> hand = new HashMap<>();
						for (Entry<Integer, Mahjong> entry1 : seat.mahjong.entrySet()) {
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
				if (this.isChi(seat.askMahjongGroup)) {// 吃
					Mahjong[] pk = ctrl.helper.checkChi(seat.mahjong, seat.universal, ctrl.getRoom().leftMahjong).get(0);
					for (int i = 0; i < pk.length; i++) {
						index.add(pk[i].index);
					}
					if (ctrl.getRoom().leftMahjong != null) {
						index.add(ctrl.getRoom().leftMahjong.index);
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
			else if (seat.gameStatus == MahjongSeatStatus.SEAT_STATUS_DISCARD) {
				int index = discardAi.discardMahjong(seat, ctrl.getRoom().roomSetting);
				ctrl.discard(seat.playerId, index);
				return;
			}
		}
	}

	public boolean isHu(ArrayList<MahjongGroup> askMahjongGroup) {
		for (int i = 0; i < askMahjongGroup.size(); i++) {
			if (askMahjongGroup.get(i).typeId == CombinationType.POKER_TYPE_HU_PAO
					|| askMahjongGroup.get(i).typeId == CombinationType.POKER_TYPE_HU_ZIMO) {
				return true;
			}
		}
		return false;
	}

	public int isGang(ArrayList<MahjongGroup> askMahjongGroup) {
		for (int i = 0; i < askMahjongGroup.size(); i++) {
			if (askMahjongGroup.get(i).typeId == CombinationType.POKER_TYPE_GANG) {
				return i;
			}
		}
		return -1;
	}

	public boolean isPeng(ArrayList<MahjongGroup> askMahjongGroup) {
		for (int i = 0; i < askMahjongGroup.size(); i++) {
			if (askMahjongGroup.get(i).typeId == CombinationType.POKER_TYPE_PENG) {
				return true;
			}
		}
		return false;
	}

	public boolean isChi(ArrayList<MahjongGroup> askMahjongGroup) {
		for (int i = 0; i < askMahjongGroup.size(); i++) {
			if (askMahjongGroup.get(i).typeId == CombinationType.POKER_TYPE_CHI) {
				return true;
			}
		}
		return false;
	}

	public boolean isUniversal(ArrayList<Mahjong> sprite, Mahjong mahjong) {
		for (int i = 0; i < sprite.size(); i++) {
			if (mahjong.size == sprite.get(i).size && mahjong.color == sprite.get(i).color) {
				return true;
			}
		}
		return false;
	}

}
