package com.oegame.tablegames.service.mahjong.model;

import com.oegame.tablegames.service.mahjong.MahjongAlgorithm;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

public class Settle {

	protected MahjongAlgorithm helper = new MahjongAlgorithm();

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
		Mahjong mahjong = null;
		if (isSelf) {
			mahjong = seat.hitMahjong;
		} else {
			mahjong = room.leftMahjong;
		}
		String desc = "";

		int qxd = isQxd(seat.mahjong, seat.universal, mahjong,isSelf);

		if (qxd > 0) {
			score = 6;
			desc = "luxurious seven pairs";
			seat.type = HuSubType.HU_SUBTYPE_HHQXD;
			System.out.println("豪华七小对");
		} else if (qxd == 0) {
			score = 3;
			desc = "seven pairs";
			seat.type = HuSubType.HU_SUBTYPE_QXD;
			System.out.println("七小对");
		}else {
			score = 1;
			System.out.println("Win by discard");
			seat.type = HuSubType.HU_SUBTYPE_PING;
		}

		if (isSelf && room.getHanCount() == 0) {
			score *= 2;
			desc += " " + "win by beginning x2";
			System.out.println("天胡x2");
		}
		
		if (!isSelf && room.getHanCount() == 1) {
			score *= 2;
			desc += " " + "win by first draw x2";
			System.out.println("地胡x2");
		}
		
		if (room.buGang != null) {
			score *= 2;
			desc += " " + "Kong x2";
			System.out.println("抢杠x2");
		}
		
		if (isSelf) {
			score *= 2;
			System.out.println("自摸x2");
		}

		if (seat.isGang) {
			score *= 2;
			desc += " " + "win after kong x2";
			System.out.println("杠上开花x2");
		}

		if (loser > 0 && room.leftSeatPos == room.gangshangpao) {
			score *= 2;
			desc += " " + "win by kong x2";
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
					room.seat.get(x).incomesDesc = "win by self-drawn-" + String.valueOf(score);
					total += score;
				}
			}
			seat.incomesDesc = "win by self-drawn+" + String.valueOf(total) + " " + desc;
		} else {
			room.seat.get(loser).incomesDesc = "lose by discard" + String.valueOf(room.getSeatByPos(loser).huScore);
			seat.incomesDesc = "win by discard+" + String.valueOf(score) + " " + desc;
		}
		
	}

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
				seat.incomesDesc += " " + "Concealed Kong" + String.valueOf(seat.agScore);
				System.out.println("暗杠" + String.valueOf(seat.agScore));
			}
			if (seat.mgScore != 0) {
				seat.incomesDesc += " " + "Exposed Kong" + String.valueOf(seat.mgScore);
				System.out.println("明杠" + String.valueOf(seat.mgScore));
			}
			if (seat.bgScore != 0) {
				seat.incomesDesc += " " + "Supplementary Kong" + String.valueOf(seat.bgScore);
				System.out.println("补杠" + String.valueOf(seat.bgScore));
			}
		}
	}

	private boolean isUniversal(ArrayList<Mahjong> sprite, Mahjong mahjong) {
		for (int i = 0; i < sprite.size(); i++) {
			if (mahjong.size == sprite.get(i).size && mahjong.color == sprite.get(i).color) {
				return true;
			}
		}
		return false;
	}

	private int isQxd(HashMap<Integer, Mahjong> hand, ArrayList<Mahjong> sprite, Mahjong test, boolean isSelf) {

		if (hand.size() != 13) {
			return -1;
		}
		ArrayList<Mahjong> mahjong1 = new ArrayList<Mahjong>();

		int hun = 0;
		for (Entry<Integer, Mahjong> entry : hand.entrySet()) {
			if (this.isUniversal(sprite, entry.getValue())) {
				hun++;
			}else {
				mahjong1.add(entry.getValue());
			}
		}

		if (test != null) {
			if (this.isUniversal(sprite, test) && isSelf) {
				hun++;
			}else {
				mahjong1.add(test);
			}
		}

		LinkedHashMap<Integer, Mahjong> values = MahjongAlgorithm.array_count_values(mahjong1);

		int single = 0;
		int gang = 0;

		for (Entry<Integer, Mahjong> entry : values.entrySet()) {
			if (entry.getValue().amount == 1) {
				single++;
			} else if (entry.getValue().amount == 3) {
				single++;
			} else if (entry.getValue().amount == 4) {
				gang++;
			}
		}

		if (single == 0 || hun >= single) {
			return gang;
		} else {
			return -1;
		}
	}
}
