package com.oegame.tablegames.service.game.mahjong.settle;

import java.util.HashMap;
import java.util.Map.Entry;

import com.oegame.tablegames.service.game.mahjong.MahjongAlgorithm;
import com.oegame.tablegames.service.game.mahjong.Room;
import com.oegame.tablegames.service.game.mahjong.model.HuSubType;
import com.oegame.tablegames.service.game.mahjong.model.Poker;
import com.oegame.tablegames.service.game.mahjong.model.Seat;
import com.oegame.tablegames.service.game.mahjong.model.SettleVO;

public class WangqueSettle implements ISettle {

	// 麻将的操作类
	protected MahjongAlgorithm mahjong = new MahjongAlgorithm();

	// 麻将的方法类
	protected MahjongMethod method = new MahjongMethod();

	// 正常计算胡分
	public void huSettle(int winer, int loser, boolean isSelf, Room room) {
		// 胡分
		Seat seat = room.seat.get(winer);
		if (isSelf) {
			int total = 0;
			for (int x = 1; x <= room.roomSetting.player; x++) {
				if (x != winer) {
					room.seat.get(x).settle += (seat.huScore * -1);
					total += seat.huScore;
				}
			}
			seat.settle += (total);
		} else {
			for (int i = 1; i <= room.roomSetting.player; i++) {
				if (room.seat.get(i).huScore > 0) {
					room.seat.get(loser).settle += (room.seat.get(i).huScore * -1);
					room.seat.get(i).settle += (room.seat.get(i).huScore);
				}
			}			
		}

		// 杠分
		for (int i = 1; i <= room.roomSetting.player; i++) {
			Seat seat1 = room.seat.get(i);
			if (seat1.agScore != 0) {
				seat1.settle += (seat1.agScore);
			}
			if (seat1.mgScore != 0) {
				seat1.settle += (seat1.mgScore);
			}
			if (seat1.bgScore != 0) {
				seat1.settle += (seat1.bgScore);
			}
		}
	}

	// 计算胡分
	public void huScore(int winer, int loser, boolean isSelf, SettleVO res, Room room) {

		Seat seat = room.getSeatByPos(winer);
		int score = 0;
		Poker poker = null;
		if (isSelf) {
			poker = seat.hitPoker;
		} else {
			poker = room.leftPoker;
		}
		String desc = "";

		int qxd = method.isQxd(seat.poker, seat.universal, seat.usePokerGroup, poker,isSelf);
		boolean isQys = method.isQys(seat.poker, seat.universal, seat.usePokerGroup, poker,isSelf);
		boolean isYtl = method.isYtl(seat.poker, poker, seat.universal, room.roomSetting,isSelf);
		boolean isDdh = method.isDdH(res, seat.poker, seat.usePokerGroup);

		if (isQys && qxd > 0) {
			score = 12;
			// seat.incomesDesc += " "+"清一色豪华七小对";
			desc = "清一色豪华七小对";
			seat.type = HuSubType.HU_SUBTYPE_QYSHHQXD;
			System.out.println("清一色豪华七小对");
		} else if (isQys && isYtl) {
			score = 6;
			// seat.incomesDesc += " "+"清一色一条龙";
			desc = "清一色一条龙";
			seat.type = HuSubType.HU_SUBTYPE_QYSYTL;
			System.out.println("清一色一条龙");
		} else if (isQys && qxd == 0) {
			score = 6;
			// seat.incomesDesc += " "+"清一色七小对";
			desc = "清一色七小对";
			seat.type = HuSubType.HU_SUBTYPE_QYSQXD;
			System.out.println("清一色七小对");
		} else if (isQys && isDdh) {
			score = 6;
			// seat.incomesDesc += " "+"清一色大对胡";
			desc = "清一色大对胡";
			seat.type = HuSubType.HU_SUBTYPE_QYSDDH;
			System.out.println("清一色大对胡");
		} else if (qxd > 0) {
			score = 6;
			// seat.incomesDesc += " "+"豪华七小对";
			desc = "豪华七小对";
			seat.type = HuSubType.HU_SUBTYPE_HHQXD;
			System.out.println("豪华七小对");
		} else if (isQys) {
			score = 3;
			// seat.incomesDesc += " "+"清一色";
			desc = "清一色";
			seat.type = HuSubType.HU_SUBTYPE_QYS;
			System.out.println("清一色");
		} else if (isYtl) {
			score = 3;
			// seat.incomesDesc += " "+"一条龙";
			desc = "一条龙";
			seat.type = HuSubType.HU_SUBTYPE_YTL;
			System.out.println("一条龙");
		}else if (qxd == 0) {
			score = 3;
			// seat.incomesDesc += " "+"七小对";
			desc = "七小对";
			seat.type = HuSubType.HU_SUBTYPE_QXD;
			System.out.println("七小对");
		}else if (isDdh) {
			score = 2;
			// seat.incomesDesc += " "+"大对胡";
			desc = "大对胡";
			System.out.println("大对胡");
			seat.type = HuSubType.HU_SUBTYPE_DDH;
		}  else {
			score = 1;
			System.out.println("平胡");
			seat.type = HuSubType.HU_SUBTYPE_PING;
		}

		if (isSelf && room.getHanCount() == 0) {
			score *= 2;
			desc += " " + "天胡x2";
			System.out.println("天胡x2");
		}
		
		if (!isSelf && room.getHanCount() == 1) {
			score *= 2;
			desc += " " + "地胡x2";
			System.out.println("地胡x2");
		}
		
		if (room.buGang != null) {
			score *= 2;
			desc += " " + "抢杠x2";
			System.out.println("抢杠x2");
		}
		
		if (isSelf) {
			score *= 2;
			System.out.println("自摸x2");
		}

		if (seat.isGang) {
			score *= 2;
			desc += " " + "杠上开花x2";
			System.out.println("杠上开花x2");
		}

		if (loser > 0 && room.leftSeatPos == room.gangshangpao) {
			score *= 2;
			desc += " " + "杠上炮x2";
			System.out.println("杠上炮x2");
		}

		seat.huScore = score;
		if (loser > 0) {
			room.getSeatByPos(loser).huScore += score * -1;
		}
		
		if (isSelf) {
			int total = 0;
			for (int x = 1; x <= room.roomSetting.player; x++) {
				if (x != winer) {
					room.seat.get(x).incomesDesc = "自摸-" + String.valueOf(score);
					total += score;
				}
			}
			seat.incomesDesc = "自摸+" + String.valueOf(total) + " " + desc;
		} else {
			room.seat.get(loser).incomesDesc = "点炮" + String.valueOf(room.getSeatByPos(loser).huScore);
			seat.incomesDesc = "胡+" + String.valueOf(score) + " " + desc;
		}
		
	}

	// 计算杠分
	public void gangScore(Room room) {
		for (int i = 1; i <= room.roomSetting.player; i++) {
			Seat seat = room.seat.get(i);
			for (int j = 0; j < seat.gangIncomes.size(); j++) {
				if (seat.gangIncomes.get(j).type == 1) {//暗杠
					for (int x = 1; x <= room.roomSetting.player; x++) {
						if (x != i) {
							room.seat.get(x).agScore -= 2;
							seat.agScore += 2;
						}
					}
				} else if (seat.gangIncomes.get(j).type == 2) {//明杠
					room.seat.get(seat.gangIncomes.get(j).pos).mgScore -= 3;
					seat.mgScore += 3;
				} else if (seat.gangIncomes.get(j).type == 3) {//补杠
					for (int x = 1; x <= room.roomSetting.player; x++) {
						if (x != i) {
							room.seat.get(x).bgScore -= 1;
							seat.bgScore += 1;
						}
					}
				}
			}
		}

		for (int i = 1; i <= room.roomSetting.player; i++) {
			Seat seat = room.seat.get(i);
			if (seat.agScore != 0) {
				seat.incomesDesc += " " + "暗杠" + String.valueOf(seat.agScore);
				System.out.println("暗杠" + String.valueOf(seat.agScore));
			}
			if (seat.mgScore != 0) {
				seat.incomesDesc += " " + "明杠" + String.valueOf(seat.mgScore);
				System.out.println("明杠" + String.valueOf(seat.mgScore));
			}
			if (seat.bgScore != 0) {
				seat.incomesDesc += " " + "补杠" + String.valueOf(seat.bgScore);
				System.out.println("补杠" + String.valueOf(seat.bgScore));
			}
		}
	}
}
