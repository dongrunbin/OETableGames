package com.oegame.tablegames.service.game.mahjong;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import com.oegame.tablegames.service.game.mahjong.model.Poker;
import com.oegame.tablegames.service.game.mahjong.model.PokerVO;
import com.oegame.tablegames.service.game.mahjong.model.ResultVO;
import com.oegame.tablegames.service.game.mahjong.model.RoomSetting;
import com.oegame.tablegames.service.game.mahjong.model.Seat;
import com.oegame.tablegames.service.game.mahjong.model.SettleVO;
import com.oegame.tablegames.service.game.mahjong.model.Tools;

/**
 * 麻将算法类
 */
public class MahjongDiscardAi {

	private static Mahjong mahjong = new Mahjong();

	private static MahjongAlgorithm algorithm = new MahjongAlgorithm();

	public static void main(String[] args) {
		
//		int index = 0;
//		ArrayList<Poker> poker = new ArrayList<>();
//
//		// 装填万
//		for (int size = 1; size <= 9; size++) {
//			for (int c = 1; c <= 4; c++) {
//				index++;
//				poker.add(new Poker(index, 1, size));
//			}
//		}
//		// 装填筒
//		for (int size = 1; size <= 9; size++) {
//			for (int i = 1; i <= 4; i++) {
//				index++;
//				poker.add(new Poker(index, 2, size));
//			}
//		}
//		// 装填条
//		for (int size = 1; size <= 9; size++) {
//			for (int i = 1; i <= 4; i++) {
//				index++;
//				poker.add(new Poker(index, 3, size));
//			}
//		}
//		// 东南西北
//		for (int size = 1; size <= 4; size++) {
//			for (int i = 1; i <= 4; i++) {
//				index++;
//				poker.add(new Poker(index, 4, size));
//			}
//		}
//
//		for (int i = 1; i <= 4; i++) {
//			index++;
//			poker.add(new Poker(index, 5, 1));
//		}
//		
//		ArrayList<Integer> settings = new ArrayList<Integer>();
//		settings.add(6017);
//		settings.add(6021);
//
//		RoomSetting roomSetting = new RoomSetting(settings, 6);
//
//		Seat seat = new Seat(1);
//		ArrayList<Poker> sprite = new ArrayList<Poker>();
//		sprite.add(Tools.mahjong("0_红中").get(0));
//		seat.universal = sprite;
//		long begin = System.currentTimeMillis();
//		int count = 0;
//		for (int i = 0; i < 100000; i++) {
//			seat.poker.clear();
//			Collections.shuffle(poker, new Random(TimeUtil.millisecond()));
//			for (int i1 = 0; i1 < poker.size(); i1++) {
//				poker.get(i1).index = i1 + 1;
//			}
//			
//			for (int i1 = 0; i1 < 14; i1++) {
//				seat.poker.put(poker.get(i1).index, poker.get(i1));
//			}
//			
//			MahjongDiscardAi test1 = new MahjongDiscardAi();
//			test1.discardPoker(seat, roomSetting);	
//			count++;
//		}
//		System.out.println("总时ms: " + (System.currentTimeMillis() - begin)+" ,运行次数"+count);
//		System.out.println("平均ms: " + (float)(System.currentTimeMillis() - begin)/count);

		Seat seat = new Seat(1);
		ArrayList<Poker> poker = Tools.mahjong("3_万", "0_红中");

		HashMap<Integer, Poker> hand = new HashMap<Integer, Poker>();
		for (int i = 0; i < poker.size(); i++) {
			hand.put(poker.get(i).index, poker.get(i));
		}

		Poker test = Tools.mahjong("1_条").get(0);
		test.index = 20;

		ArrayList<Integer> settings = new ArrayList<Integer>();
		settings.add(6017);
		settings.add(6021);

		RoomSetting roomSetting = new RoomSetting(settings);

		long begin = System.currentTimeMillis();

		ArrayList<Poker> sprite = new ArrayList<Poker>();
		sprite.add(Tools.mahjong("0_红中").get(0));

		seat.universal = sprite;
		seat.poker = hand;

		MahjongDiscardAi test1 = new MahjongDiscardAi();
		test1.discardPoker(seat, roomSetting);

		System.out.println("ms: " + (System.currentTimeMillis() - begin));
	}

	// 计算的出牌
	public int discardPoker(Seat seat, RoomSetting roomSetting) {

		int index = -1;
		// 优先打出,出完能够听牌的牌
		index = this.ting(seat, roomSetting);
		if (index > -1) {
			return index;
		}

		index = this.operate(seat, roomSetting);
		return index;

	}

	// 优先打出,出完能够听牌的牌
	public int ting(Seat seat, RoomSetting roomSetting) {

		ArrayList<Poker> poker = new ArrayList<>();
		if (seat.hitPoker != null) {
			poker.add(seat.hitPoker);
		}
		for (Entry<Integer, Poker> entry : seat.poker.entrySet()) {
			poker.add(entry.getValue());
		}

		HashMap<Poker, Integer> tingMap = new HashMap<>();
		for (int i = 0; i < poker.size(); i++) {
			HashMap<Integer, Poker> hand = new HashMap<>();
			for (int j = 0; j < poker.size(); j++) {
				if (i != j) {
					hand.put(poker.get(j).index, poker.get(j));
				}
			}
			SettleVO res = algorithm.checkHu(hand, seat.universal, false, null, roomSetting, true);
			if (res.lacks.size() > 0) {
				tingMap.put(poker.get(i), res.lacks.size());			
			}
		}

		if (tingMap.size() > 0) {
			ArrayList<Integer> lacks = new ArrayList<>();
			for (Entry<Poker, Integer> entry : tingMap.entrySet()) {
				lacks.add(entry.getValue());
			}
			Collections.sort(lacks);
			int max = lacks.get(lacks.size() - 1);
			for (Entry<Poker, Integer> entry : tingMap.entrySet()) {
				if (entry.getValue() == max && !this.isUniversal(seat.universal, entry.getKey())) {
					System.out.println("优先打出,出完能够听牌的牌" + mahjong.format(entry.getKey())+entry.getKey().index);
					return entry.getKey().index;
				}
			}
		}

		return -1;
	}
	
	

	public int operate(Seat seat, RoomSetting roomSetting) {

		ArrayList<Result> list = mahjong.hu(seat.poker, seat.universal, seat.hitPoker, roomSetting);
		System.out.println("计算结束,有结果" + list.size() + "种");

		Collections.sort(list, new Comparator<Result>() {
			public int compare(Result o1, Result o2) {
				Integer num1 = o1.lacks;
				Integer num2 = o2.lacks;
				return num1.compareTo(num2);
			};
		});

		Result result = list.get(0);

		Poker pk = null;// 优先出的牌
		Poker testPoker = null;

		int minColor = mahjong.minColor(seat.poker, seat.universal);
		System.out.println("mincolor == " + minColor);
		ArrayList<Poker> pklist = new ArrayList<Poker>();

		if (result.ones.size() > 0) {
			System.out.print(" 单牌 : ");
			for (int i = 0; i < result.ones.size(); i++) {
				testPoker = result.ones.get(i).poker.get(0);
				System.out.print(mahjong.format(testPoker) + " , ");
				pklist.add(testPoker);
			}
			System.out.println("\n");
		} else if (result.oneAndnine.size() > 0) {
			System.out.print("  19 : ");
			for (int i = 0; i < result.oneAndnine.get(0).poker.size(); i++) {
				testPoker = result.oneAndnine.get(0).poker.get(i);
				System.out.print(mahjong.format(testPoker) + " , ");
				pklist.add(testPoker);
			}
			System.out.println("\n");
		} else if (result.oneAndthree.size() > 0) {
			System.out.print("  顺子夹中间  : ");
			for (int i = 0; i < result.oneAndthree.get(0).poker.size(); i++) {
				testPoker = result.oneAndthree.get(0).poker.get(i);
				pklist.add(testPoker);
				System.out.print(mahjong.format(testPoker) + " , ");
			}
			System.out.println("\n");
		} else if (result.twos.size() > 0) {
			System.out.print("  顺子缺两边  : ");
			for (int i = 0; i < result.twos.get(0).poker.size(); i++) {
				testPoker = result.twos.get(0).poker.get(i);
				pklist.add(testPoker);
				System.out.print(mahjong.format(testPoker) + " , ");
			}
			System.out.println("\n");
		} else if (result.doubles.size() > 0) {
			System.out.print("  对子  : ");
			for (int i = 0; i < result.doubles.get(0).poker.size(); i++) {
				testPoker = result.doubles.get(0).poker.get(i);
				pklist.add(testPoker);
				System.out.print(mahjong.format(result.doubles.get(0).poker.get(i)) + " , ");
			}
			System.out.println("\n");
		} else if (result.straights.size() > 0) {
			System.out.print("  顺子  : ");
			for (int i = 0; i < result.straights.get(0).poker.size(); i++) {
				testPoker = result.straights.get(0).poker.get(i);
				pklist.add(testPoker);
				System.out.print(mahjong.format(result.straights.get(0).poker.get(i)) + " , ");
			}
			System.out.println("\n");
		}

		if (mahjong.feng(pklist) >= 0) {// 优先出风牌
			pk = seat.poker.get(mahjong.feng(pklist));
			System.out.println("-------优先出字牌的单牌---------" + mahjong.format(pk));
		}

		if (pk == null && minColor > 0) {// 优先出牌最少的花色
			for (int j = 0; j < pklist.size(); j++) {
				if (pk == null && pklist.get(j).color == minColor) {
					if (pklist.get(j).size == 1 || pklist.get(j).size == 9) {
						pk = pklist.get(j);
						System.out.println("-------优先出牌最少的花色,19优先---------" + mahjong.format(pk));
						break;
					}
				}
			}
			if (pk == null) {
				for (int j = 0; j < pklist.size(); j++) {
					if (pklist.get(j).color == minColor) {
						pk = pklist.get(j);
						System.out.println("-------优先出牌最少的花色,没有19---------" + mahjong.format(pk));
						break;
					}
				}
			}
		}

		if (pk == null) {
			for (int i = 0; i < pklist.size(); i++) {
				if (pklist.get(i).size == 1 || pklist.get(i).size == 9) {
					pk = pklist.get(i);
					System.out.println("-------19优先---------" + mahjong.format(pk));
					break;
				}
			}
			if (pk == null) {
				pk = pklist.get(0);
				System.out.println("-------没有19,随机出一张---------" + mahjong.format(pk));
			}
		}

		return pk.index;
	}

	// 当前手牌胡牌所需缺省数量
	public int lackCount(HashMap<Integer, Poker> hand, ArrayList<Poker> universal, Poker test,
			RoomSetting roomSetting) {

		ArrayList<Result> list = mahjong.hu(hand, universal, test, roomSetting);
		System.out.println("计算结束,list的长度是" + list.size());

		Collections.sort(list, new Comparator<Result>() {
			public int compare(Result o1, Result o2) {
				Integer num1 = o1.lacks;
				Integer num2 = o2.lacks;
				return num1.compareTo(num2);
			};
		});

		Result result = list.get(0);

		return result.lacks;
	}

	public void look(Result result) {
		Mahjong mahjong = new Mahjong();
		if (result.ones.size() > 0) {
			System.out.println("-------单牌-------");
			for (int i = 0; i < result.ones.size(); i++) {
				System.out.print(mahjong.format(result.ones.get(i).poker.get(0)) + " , ");
			}
			System.out.println("\n----------------");
		}

		if (result.oneAndnine.size() > 0) {
			System.out.println("-------19-------");
			for (int j = 0; j < result.oneAndnine.size(); j++) {
				for (int i = 0; i < result.oneAndnine.get(j).poker.size(); i++) {
					System.out.print(mahjong.format(result.oneAndnine.get(j).poker.get(i)) + " , ");
				}
			}
			System.out.println("\n----------------");
		}

		if (result.oneAndthree.size() > 0) {
			System.out.println("-------顺子夹中间-------");
			for (int j = 0; j < result.oneAndthree.size(); j++) {
				for (int i = 0; i < result.oneAndthree.get(j).poker.size(); i++) {
					System.out.print(mahjong.format(result.oneAndthree.get(j).poker.get(i)) + " , ");
				}
			}
			System.out.println("\n----------------");
		}

		if (result.twos.size() > 0) {
			System.out.println("-------顺子缺两边-------");
			for (int j = 0; j < result.twos.size(); j++) {
				for (int i = 0; i < result.twos.get(j).poker.size(); i++) {
					System.out.print(mahjong.format(result.twos.get(j).poker.get(i)) + " , ");
				}
			}
			System.out.println("\n----------------");
		}

		if (result.doubles.size() > 0) {
			System.out.println("-------对子-------");
			for (int j = 0; j < result.doubles.size(); j++) {
				for (int i = 0; i < result.doubles.get(j).poker.size(); i++) {
					System.out.print(mahjong.format(result.doubles.get(j).poker.get(i)) + " , ");
				}
			}
			System.out.println("\n----------------");
		}

		if (result.triples.size() > 0) {
			System.out.println("-------刻子-------");
			for (int j = 0; j < result.triples.size(); j++) {
				for (int i = 0; i < result.triples.get(j).poker.size(); i++) {
					System.out.print(mahjong.format(result.triples.get(j).poker.get(i)) + " , ");
				}
			}
			System.out.println("\n----------------");
		}

		if (result.straights.size() > 0) {
			System.out.println("-------顺子-------");
			for (int j = 0; j < result.straights.size(); j++) {
				for (int i = 0; i < result.straights.get(j).poker.size(); i++) {
					System.out.print(mahjong.format(result.straights.get(j).poker.get(i)) + " , ");
				}
			}
			System.out.println("\n----------------");
		}

	}
	
	public boolean isUniversal(ArrayList<Poker> sprite, Poker poker) {
		for (int i = 0; i < sprite.size(); i++) {
			if (poker.size == sprite.get(i).size && poker.color == sprite.get(i).color) {
				return true;
			}
		}
		return false;
	}
	

	enum ENUM_SUIT_TYPE {
		Double, Triple, Straight, Two, OneandThree, OneAndNine, One
	}

	static class SuitSets {
		public ENUM_SUIT_TYPE type;
		public ArrayList<Poker> lacks = new ArrayList<Poker>();
		public ArrayList<Poker> poker = new ArrayList<Poker>();

		public SuitSets(ENUM_SUIT_TYPE type) {
			this.type = type;
		}

	}

	/**
	 * @author Administrator
	 *
	 */
	/**
	 * @author Administrator
	 *
	 */
	static class Mahjong {

		int minLacks = -1;// 最少缺省数

		public ArrayList<Result> hu(HashMap<Integer, Poker> hand, ArrayList<Poker> sprite, Poker test,
				RoomSetting roomSetting) {

			this.minLacks = -1;

			ArrayList<Poker> poker = new ArrayList<Poker>();

			for (Entry<Integer, Poker> entry : hand.entrySet()) {
				if (this.isUniversal(sprite, entry.getValue())) {

				} else {
					poker.add(entry.getValue());
				}
			}

			if (test != null) {
				if (this.isUniversal(sprite, test)) {

				} else {
					poker.add(test);
				}
			}

			Collections.sort(poker, new Comparator<Poker>() {
				public int compare(Poker o1, Poker o2) {
					Integer color1 = o1.color;
					Integer color2 = o2.color;
					Integer size1 = o1.size;
					Integer size2 = o2.size;
					if (color1 != color2) {
						return color1.compareTo(color2);
					}
					return size1.compareTo(size2);
				};
			});
			System.out.print("排序之后的手牌 : ");
			for (int i = 0; i < poker.size(); i++) {
				System.out.print("," + Tools.mahjong(poker.get(i).color, poker.get(i).size));
			}
			System.out.print("\n");

			Result result = new Result(poker);
			ArrayList<Result> list = new ArrayList<Result>();

			this.CheckHu(result, list);

			return list;
		}

		public void CheckHu(Result result, ArrayList<Result> list) {

			if (this.minLacks >= 0 && result.lacks > this.minLacks) {
				return;
			}

			if (result.poker.size() <= 0) {

				if (this.minLacks < 0) {
					this.minLacks = result.lacks;
				} else if (this.minLacks > result.lacks) {
					this.minLacks = result.lacks;
				}

				list.add(result);
				return;
			}

			CalculateStraight(result, list);// 顺子

			CalculateTriple(result, list);// 刻子

			CalculateDouble(result, list);// 对子

			CalculateStraightLR(result, list);// 两张连着的

			CalculateStraightMD(result, list);// 中间隔着的

			CalculateSigle(result, list);//

		}

		/**
		 * 顺子
		 * 
		 * @param res
		 */
		public void CalculateStraight(Result res, ArrayList<Result> list) {
			if (res.poker.size() < 3) {
				return;
			}

			Result result = new Result(res);

			for (int x = 0; x < result.poker.size(); x++) {
				Poker poker = result.poker.get(x);
				if (poker.color > 3) {
					continue;
				}

				int pos1 = -1;
				int pos2 = -1;

				for (int i = 0; i < result.poker.size(); i++) {
					if (pos1 == -1 && result.poker.get(i).color == poker.color
							&& poker.size + 1 == result.poker.get(i).size) {
						pos1 = i;
					} else if (pos2 == -1 && result.poker.get(i).color == poker.color
							&& poker.size + 2 == result.poker.get(i).size) {
						pos2 = i;
					}

					if (pos1 > -1 && pos2 > -1) {
						SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Straight);
						sets.poker.add(poker);
						Poker poker2 = result.poker.remove(pos2);
						Poker poker1 = result.poker.remove(pos1);
						sets.poker.add(poker1);
						sets.poker.add(poker2);
						result.poker.remove(x);

						result.straights.add(sets);

						this.CheckHu(result, list);
						return;
					}
				}
			}
		}

		// 刻子
		public void CalculateTriple(Result res, ArrayList<Result> list) {

			if (res.poker.size() < 3) {
				return;
			}

			Result result = new Result(res);

			for (int x = 0; x < result.poker.size(); x++) {
				Poker poker = result.poker.get(x);

				if (x + 2 < result.poker.size()) {
					if (this.isEqual(poker, result.poker.get(x + 1)) && this.isEqual(poker, result.poker.get(x + 2))) {

						SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Triple);

						sets.poker.add(result.poker.remove(x + 2));
						sets.poker.add(result.poker.remove(x + 1));
						sets.poker.add(result.poker.remove(x));

						result.triples.add(sets);

						this.CheckHu(result, list);
						return;

					}
				}
			}
		}

		// 对子
		public void CalculateDouble(Result res, ArrayList<Result> list) {

			if (res.poker.size() < 2) {
				return;
			}

			Result result = new Result(res);

			for (int x = 0; x < result.poker.size(); x++) {
				Poker poker = result.poker.get(x);
				if (x + 1 < result.poker.size()) {
					if (this.isEqual(poker, result.poker.get(x + 1))) {
						if (result.doubles.size() == 0) {
							SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Double);
							sets.poker.add(result.poker.remove(x + 1));
							sets.poker.add(result.poker.remove(x));

							result.doubles.add(sets);
							this.CheckHu(result, list);
						} else {
							SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Triple);
							sets.poker.add(result.poker.remove(x + 1));
							sets.poker.add(result.poker.remove(x));

							result.lacks++;
							result.triples.add(sets);

							this.CheckHu(result, list);
						}
						return;
					}
				}
			}
		}

		// 顺子缺两边
		public void CalculateStraightLR(Result res, ArrayList<Result> list) {

			if (res.poker.size() < 2) {
				return;
			}

			Result result = new Result(res);

			for (int x = 0; x < result.poker.size(); x++) {
				Poker poker = result.poker.get(x);
				if (poker.color > 3) {
					continue;
				}
				int pos1 = -1;
				int pos2 = -1;

				for (int i = 0; i < result.poker.size(); i++) {
					if (pos1 == -1 && result.poker.get(i).color == poker.color
							&& poker.size + 1 == result.poker.get(i).size) {
						pos1 = i;
					} else if (pos2 == -1 && result.poker.get(i).color == poker.color
							&& poker.size + 2 == result.poker.get(i).size) {
						pos2 = i;
					}

					if (pos1 > -1 && pos2 > -1) {
						SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Straight);

						sets.poker.add(poker);
						Poker poker2 = result.poker.remove(pos2);
						Poker poker1 = result.poker.remove(pos1);
						sets.poker.add(poker1);
						sets.poker.add(poker2);
						result.poker.remove(x);

						result.straights.add(sets);

						this.CheckHu(result, list);
						return;
					}
				}
			}

			for (int x = 0; x < result.poker.size(); x++) {
				Poker poker = result.poker.get(x);
				if (x + 1 < result.poker.size()) {
					if (this.isEqual(poker, result.poker.get(x + 1))) {
						if (result.doubles.size() == 0) {
							SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Double);
							sets.poker.add(result.poker.remove(x + 1));
							sets.poker.add(result.poker.remove(x));

							result.doubles.add(sets);

							this.CheckHu(result, list);
						} else {
							SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Triple);
							sets.poker.add(result.poker.remove(x + 1));
							sets.poker.add(result.poker.remove(x));

							result.lacks++;
							result.triples.add(sets);

							this.CheckHu(result, list);
						}
						return;
					}
				}
			}

			for (int x = 0; x < result.poker.size(); x++) {
				Poker poker = result.poker.get(x);
				if (poker.color > 3) {
					continue;
				}
				for (int i = 0; i < result.poker.size(); i++) {
					if (poker.size + 1 == result.poker.get(i).size && poker.color == result.poker.get(i).color) {

						if (poker.size == 1 || poker.size == 8) {
							SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.OneAndNine);

							sets.poker.add(poker);
							sets.poker.add(result.poker.remove(i));
							result.poker.remove(x);

							result.oneAndnine.add(sets);
							result.lacks++;

							this.CheckHu(result, list);
							return;
						} else {
							SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Two);

							sets.poker.add(poker);
							sets.poker.add(result.poker.remove(i));
							result.poker.remove(x);

							result.twos.add(sets);
							result.lacks++;

							this.CheckHu(result, list);
							return;
						}
					}
				}
			}
		}

		// 顺子夹中间
		public void CalculateStraightMD(Result res, ArrayList<Result> list) {

			if (res.poker.size() < 2) {
				return;
			}

			Result result = new Result(res);

			for (int x = 0; x < result.poker.size(); x++) {
				Poker poker = result.poker.get(x);
				if (poker.color > 3) {
					continue;
				}
				int pos1 = -1;
				int pos2 = -1;

				for (int i = 0; i < result.poker.size(); i++) {
					if (pos1 == -1 && result.poker.get(i).color == poker.color
							&& poker.size + 1 == result.poker.get(i).size) {
						pos1 = i;
					} else if (pos2 == -1 && result.poker.get(i).color == poker.color
							&& poker.size + 2 == result.poker.get(i).size) {
						pos2 = i;
					}

					if (pos1 > -1 && pos2 > -1) {
						SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Straight);

						sets.poker.add(poker);
						Poker poker2 = result.poker.remove(pos2);
						Poker poker1 = result.poker.remove(pos1);
						sets.poker.add(poker1);
						sets.poker.add(poker2);
						result.poker.remove(x);

						result.straights.add(sets);

						this.CheckHu(result, list);
						return;
					}
				}
			}

			for (int x = 0; x < result.poker.size(); x++) {
				Poker poker = result.poker.get(x);
				if (x + 1 < result.poker.size()) {
					if (this.isEqual(poker, result.poker.get(x + 1))) {
						if (result.doubles.size() == 0) {
							SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Double);
							sets.poker.add(result.poker.remove(x + 1));
							sets.poker.add(result.poker.remove(x));

							result.doubles.add(sets);

							this.CheckHu(result, list);
						} else {
							SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Triple);
							sets.poker.add(result.poker.remove(x + 1));
							sets.poker.add(result.poker.remove(x));

							result.lacks++;
							result.triples.add(sets);

							this.CheckHu(result, list);
						}
						return;
					}
				}
			}

			for (int x = 0; x < result.poker.size(); x++) {
				Poker poker = result.poker.get(x);
				if (poker.color > 3) {
					continue;
				}
				for (int i = 0; i < result.poker.size(); i++) {
					if (poker.size + 2 == result.poker.get(i).size && poker.color == result.poker.get(i).color) {

						if (poker.size == 1 || poker.size + 2 == 9) {

							SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.OneAndNine);

							sets.poker.add(poker);
							sets.poker.add(result.poker.remove(i));
							result.poker.remove(x);
							result.lacks++;

							result.oneAndnine.add(sets);

							this.CheckHu(result, list);
							return;
						} else {
							SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.OneandThree);
							sets.poker.add(poker);
							sets.poker.add(result.poker.remove(i));
							result.poker.remove(x);
							result.lacks++;

							result.oneAndthree.add(sets);

							this.CheckHu(result, list);
							return;
						}
					}
				}
			}
		}

		// 单牌
		public void CalculateSigle(Result res, ArrayList<Result> list) {

			if (res.poker.size() < 1) {
				return;
			}

			Result result = new Result(res);

			for (int x = 0; x < result.poker.size(); x++) {
				Poker poker = result.poker.get(x);
				if (poker.color > 3) {
					continue;
				}
				int pos1 = -1;
				int pos2 = -1;

				for (int i = 0; i < result.poker.size(); i++) {
					if (pos1 == -1 && result.poker.get(i).color == poker.color
							&& poker.size + 1 == result.poker.get(i).size) {
						pos1 = i;
					} else if (pos2 == -1 && result.poker.get(i).color == poker.color
							&& poker.size + 2 == result.poker.get(i).size) {
						pos2 = i;
					}

					if (pos1 > -1 && pos2 > -1) {
						SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Straight);

						sets.poker.add(poker);
						Poker poker2 = result.poker.remove(pos2);
						Poker poker1 = result.poker.remove(pos1);
						sets.poker.add(poker1);
						sets.poker.add(poker2);
						result.poker.remove(x);

						result.straights.add(sets);

						this.CheckHu(result, list);
						return;
					}
				}
			}

			for (int x = 0; x < result.poker.size(); x++) {
				Poker poker = result.poker.get(x);
				if (x + 1 < result.poker.size()) {
					if (this.isEqual(poker, result.poker.get(x + 1))) {
						if (result.doubles.size() == 0) {
							SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Double);
							sets.poker.add(result.poker.remove(x + 1));
							sets.poker.add(result.poker.remove(x));

							result.doubles.add(sets);

							this.CheckHu(result, list);
						} else {
							SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Triple);
							sets.poker.add(result.poker.remove(x + 1));
							sets.poker.add(result.poker.remove(x));

							result.lacks++;
							result.triples.add(sets);

							this.CheckHu(result, list);
						}
						return;
					}
				}
			}

			for (int x = 0; x < result.poker.size(); x++) {
				Poker poker = result.poker.get(x);
				if (poker.color > 3) {
					continue;
				}
				for (int i = 0; i < result.poker.size(); i++) {
					if (poker.size + 1 == result.poker.get(i).size && poker.color == result.poker.get(i).color) {

						if (poker.size == 1 || poker.size == 8) {
							SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.OneAndNine);

							sets.poker.add(poker);
							sets.poker.add(result.poker.remove(i));
							result.poker.remove(x);

							result.oneAndnine.add(sets);
							result.lacks++;

							this.CheckHu(result, list);
							return;
						} else {
							SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Two);

							sets.poker.add(poker);
							sets.poker.add(result.poker.remove(i));
							result.poker.remove(x);

							result.twos.add(sets);
							result.lacks++;

							this.CheckHu(result, list);
							return;
						}
					}
				}
			}

			for (int x = 0; x < result.poker.size(); x++) {
				Poker poker = result.poker.get(x);
				if (poker.color > 3) {
					continue;
				}
				for (int i = 0; i < result.poker.size(); i++) {
					if (poker.size + 2 == result.poker.get(i).size && poker.color == result.poker.get(i).color) {

						if (poker.size == 1 || poker.size + 2 == 9) {

							SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.OneAndNine);

							sets.poker.add(poker);
							sets.poker.add(result.poker.remove(i));
							result.poker.remove(x);
							result.lacks++;

							result.oneAndnine.add(sets);

							this.CheckHu(result, list);
							return;
						} else {
							SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.OneandThree);
							sets.poker.add(poker);
							sets.poker.add(result.poker.remove(i));
							result.poker.remove(x);
							result.lacks++;

							result.oneAndthree.add(sets);

							this.CheckHu(result, list);
							return;
						}
					}
				}
			}

			if (result.doubles.size() == 0) {
				SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.One);
				sets.poker.add(result.poker.remove(0));
				result.ones.add(sets);
				result.lacks++;

				this.CheckHu(result, list);
				return;
			} else {
				SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.One);
				sets.poker.add(result.poker.remove(0));

				result.ones.add(sets);
				result.lacks += 2;

				this.CheckHu(result, list);
				return;
			}
		}

		// 判断七小对
		public boolean qxd(HashMap<Integer, Poker> hand, ArrayList<Poker> universal, Poker test, boolean isWuhun) {

			if (hand.size() != 13) {
				return false;
			}

			ArrayList<Poker> sprite = universal;
			if (isWuhun) {
				sprite.clear();
			}

			ArrayList<Poker> poker = new ArrayList<Poker>();

			for (Entry<Integer, Poker> entry : hand.entrySet()) {
				poker.add(entry.getValue());
			}

			if (test != null) {
				poker.add(test);
			}

			LinkedHashMap<Integer, Poker> values = this.array_count_values(poker);

			int single = 0;
			int hun = 0;
			for (Entry<Integer, Poker> entry : values.entrySet()) {
				if (this.isUniversal(sprite, entry.getValue())) {
					hun++;
				} else {
					if (entry.getValue().amount == 1) {
						single++;
					} else if (entry.getValue().amount == 3) {
						single++;
					}
				}
			}

			if (test == null) {
				if (single == 1 || hun > single) {
					return true;
				}
			} else {
				if (single == 0 || hun >= single) {
					return true;
				}
			}

			return false;
		}

		public boolean isUniversal(ArrayList<Poker> sprite, Poker test) {
			for (int i = 0; i < sprite.size(); i++) {
				if (test.size == sprite.get(i).size && test.color == sprite.get(i).color) {
					return true;
				}
			}
			return false;
		}

		public String format(Poker poker) {
			return Tools.mahjong(poker.color, poker.size);
		}

		public boolean isEqual(Poker poker1, Poker poker2) {
			if (poker1.color == poker2.color && poker1.size == poker2.size) {
				return true;
			}
			return false;
		}

		/**
		 * @param poker
		 * @param sprite
		 * @return 牌数量最少的花色
		 */
		public int minColor(HashMap<Integer, Poker> poker, ArrayList<Poker> sprite) {

			ArrayList<Integer> colors = new ArrayList<>();
			int wan = 0;
			int tong = 0;
			int tiao = 0;
			for (Entry<Integer, Poker> entry : poker.entrySet()) {
				if (!this.isUniversal(sprite, entry.getValue())) {
					if (entry.getValue().color == 1) {
						wan++;
					} else if (entry.getValue().color == 2) {
						tong++;
					} else if (entry.getValue().color == 3) {
						tiao++;
					}
				}
			}

			colors.add(wan);
			colors.add(tong);
			colors.add(tiao);

			for (int i = 0; i < colors.size(); i++) {
				if (colors.get(i) > 0) {
					boolean min = true;
					for (int j = 0; j < colors.size(); j++) {
						if (i != j && colors.get(j) > 0 && colors.get(i) > colors.get(j)) {
							min = false;
							break;
						}
					}
					if (min) {
						return i + 1;
					}
				}
			}

			return -1;
		}

		// 优先出风牌的单牌
		public int feng(ArrayList<Poker> poker) {
			for (int i = 0; i < poker.size(); i++) {
				if (poker.get(i).color > 3) {
					return poker.get(i).index;
				}
			}
			return -1;
		}

		/**
		 * 同样牌出现的数量
		 * 
		 * @param poker
		 * @return LinkedHashMap<Integer,Poker>
		 */
		private LinkedHashMap<Integer, Poker> array_count_values(ArrayList<Poker> poker) {
			LinkedHashMap<Integer, Poker> array = new LinkedHashMap<Integer, Poker>();

			// 装填到hashMap
			for (int i = 0; i < poker.size(); i++) {
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

			return array;
		}

	}

	static class Result {

		ArrayList<Poker> poker = new ArrayList<Poker>();
		int lacks = 0;
		ArrayList<SuitSets> straights = new ArrayList<SuitSets>();// 顺子
		ArrayList<SuitSets> triples = new ArrayList<SuitSets>();// 刻子
		ArrayList<SuitSets> doubles = new ArrayList<SuitSets>();// 对子
		ArrayList<SuitSets> twos = new ArrayList<SuitSets>();// 顺子缺两边
		ArrayList<SuitSets> oneAndthree = new ArrayList<SuitSets>();// 顺子缺中间
		ArrayList<SuitSets> oneAndnine = new ArrayList<SuitSets>();// 顺子缺中间
		ArrayList<SuitSets> ones = new ArrayList<SuitSets>();// 单牌

		public Result(ArrayList<Poker> poker) {
			this.poker = poker;
		}

		public String format(Poker poker) {
			return Tools.mahjong(poker.color, poker.size);
		}

		public Result(Result result) {
			for (int i = 0; i < result.poker.size(); i++) {
				this.poker.add(result.poker.get(i));
			}
			this.lacks = result.lacks;
			for (int i = 0; i < result.straights.size(); i++) {
				this.straights.add(result.straights.get(i));
			}
			for (int i = 0; i < result.triples.size(); i++) {
				this.triples.add(result.triples.get(i));
			}
			for (int i = 0; i < result.doubles.size(); i++) {
				this.doubles.add(result.doubles.get(i));
			}
			for (int i = 0; i < result.twos.size(); i++) {
				this.twos.add(result.twos.get(i));
			}
			for (int i = 0; i < result.oneAndthree.size(); i++) {
				this.oneAndthree.add(result.oneAndthree.get(i));
			}
			for (int i = 0; i < result.oneAndnine.size(); i++) {
				this.oneAndnine.add(result.oneAndnine.get(i));
			}
			for (int i = 0; i < result.ones.size(); i++) {
				this.ones.add(result.ones.get(i));
			}
		}

		public void debug() {
			for (int i = 0; i < this.straights.size(); i++) {
				for (int j = 0; j < this.straights.get(i).poker.size(); j++) {
					if (j == 0) {
						System.out.print(this.format(this.straights.get(i).poker.get(j)));
					} else {
						System.out.print(", " + this.format(this.straights.get(i).poker.get(j)));
					}
				}
				if (this.straights.get(i).lacks.size() > 0) {
					System.out.print(" -- ");
					for (int j = 0; j < this.straights.get(i).lacks.size(); j++) {
						if (j == 0) {
							System.out.print(this.format(this.straights.get(i).lacks.get(j)));
						} else {
							System.out.print(", " + this.format(this.straights.get(i).lacks.get(j)));
						}
					}
				}
				System.out.println("");
			}
			for (int i = 0; i < this.triples.size(); i++) {
				for (int j = 0; j < this.triples.get(i).poker.size(); j++) {
					if (j == 0) {
						System.out.print(this.format(this.triples.get(i).poker.get(j)));
					} else {
						System.out.print(", " + this.format(this.triples.get(i).poker.get(j)));
					}
				}
				if (this.triples.get(i).lacks.size() > 0) {
					System.out.print(" -- ");
					for (int j = 0; j < this.triples.get(i).lacks.size(); j++) {
						if (j == 0) {
							System.out.print(this.format(this.triples.get(i).lacks.get(j)));
						} else {
							System.out.print(", " + this.format(this.triples.get(i).lacks.get(j)));
						}
					}
				}
				System.out.println("");
			}
			for (int i = 0; i < this.doubles.size(); i++) {
				for (int j = 0; j < this.doubles.get(i).poker.size(); j++) {
					if (j == 0) {
						System.out.print(this.format(this.doubles.get(i).poker.get(j)));
					} else {
						System.out.print(", " + this.format(this.doubles.get(i).poker.get(j)));
					}
				}
				if (this.doubles.get(i).lacks.size() > 0) {
					System.out.print(" -- ");
					for (int j = 0; j < this.doubles.get(i).lacks.size(); j++) {
						if (j == 0) {
							System.out.print(this.format(this.doubles.get(i).lacks.get(j)));
						} else {
							System.out.print(", " + this.format(this.doubles.get(i).lacks.get(j)));
						}
					}
				}
				System.out.println("");
			}
			System.out.println("");
		}

		public void add() {

			ResultVO resultVO = new ResultVO();

			for (int i = 0; i < this.straights.size(); i++) {

				PokerVO pokerVO = new PokerVO();
				pokerVO.cardType = 4;

				for (int j = 0; j < this.straights.get(i).poker.size(); j++) {
					pokerVO.current.add(this.straights.get(i).poker.get(j));
				}
				if (this.straights.get(i).lacks.size() > 0) {
					for (int j = 0; j < this.straights.get(i).lacks.size(); j++) {
						pokerVO.lacks.add(this.straights.get(i).lacks.get(j));
					}
				}

				resultVO.pokers.add(pokerVO);

			}
			for (int i = 0; i < this.triples.size(); i++) {

				PokerVO pokerVO = new PokerVO();
				pokerVO.cardType = 2;

				for (int j = 0; j < this.triples.get(i).poker.size(); j++) {
					pokerVO.current.add(this.triples.get(i).poker.get(j));
				}
				if (this.triples.get(i).lacks.size() > 0) {

					for (int j = 0; j < this.triples.get(i).lacks.size(); j++) {
						pokerVO.lacks.add(this.triples.get(i).lacks.get(j));
					}
				}

				resultVO.pokers.add(pokerVO);

			}
			for (int i = 0; i < this.doubles.size(); i++) {

				PokerVO pokerVO = new PokerVO();
				pokerVO.cardType = 1;

				for (int j = 0; j < this.doubles.get(i).poker.size(); j++) {
					pokerVO.current.add(this.doubles.get(i).poker.get(j));
				}
				if (this.doubles.get(i).lacks.size() > 0) {

					for (int j = 0; j < this.doubles.get(i).lacks.size(); j++) {
						pokerVO.lacks.add(this.doubles.get(i).lacks.get(j));
					}
				}
				resultVO.pokers.add(pokerVO);
			}
		}

	}

}
