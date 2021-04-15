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
import com.oegame.tablegames.service.game.mahjong.settle.MahjongMethod;

/**
 * 麻将胡牌算法 
 */
public class MahjongAlgorithm {

	public static void main(String[] args) {

		Seat seat = new Seat(1);
		ArrayList<Poker> poker = Tools.mahjong("5_万", "5_万", "5_万", "7_筒", "7_筒", "7_筒", "3_筒", 
				"4_条", "5_条","8_条", "9_条", "0_红中", "0_红中");

		HashMap<Integer, Poker> hand = new HashMap<Integer, Poker>();
		for (int i = 0; i < poker.size(); i++) {
			hand.put(poker.get(i).index, poker.get(i));
		}

		Poker test = Tools.mahjong("0_红中").get(0);
//		test = null;

		RoomSetting roomSetting = null;

		long begin = System.currentTimeMillis();

		ArrayList<Poker> sprite = new ArrayList<Poker>();
		sprite.add(Tools.mahjong("0_红中").get(0));

		seat.universal = sprite;
		seat.poker = hand;

		boolean isSelf = false;
		MahjongAlgorithm asd = new MahjongAlgorithm();
		SettleVO res = asd.checkHu(hand, sprite,isSelf, test, roomSetting,false);
		

		 MahjongMethod method = new MahjongMethod();
		 int qxd = method.isQxd(hand, sprite, seat.usePokerGroup, test,isSelf);
		 boolean isDdh = method.isDdH(res, hand, seat.usePokerGroup);
		 boolean isQys = method.isQys(hand, sprite, seat.usePokerGroup, test,isSelf);
		 boolean isYtl = method.isYtl(hand, test, sprite, roomSetting,isSelf);
		
		 System.out.println("qxd"+qxd);
		 System.out.println("isDdh"+isDdh);
		 System.out.println("isQys"+isQys);
		 System.out.println("isYtl"+isYtl);

		System.out.println("ms: " + (System.currentTimeMillis() - begin));
	}

	// 通用胡牌检测
	public SettleVO checkHu(HashMap<Integer, Poker> hand, ArrayList<Poker> universal,boolean isSelf, Poker test,
			RoomSetting roomSetting, boolean isTing) {

		Mahjong mahjong = new Mahjong();

		SettleVO res = new SettleVO();
		
		//四个癞子直接胡
		if (roomSetting != null && roomSetting.isLaizihu) {
			if (mahjong.laizihu(hand, universal, test)) {
				res.code = 1;
				return res;
			}
		}

		// 判断七小对
		if (roomSetting != null && roomSetting.isQxd) {
			if (mahjong.qxd(hand, universal, test, isSelf)) {
				if (test == null) {
					res.lacks.add(new Poker());
				} else {
					res.code = 1;
					return res;
				}
			}
		}		

		res = mahjong.hu(hand, universal,isSelf, test, roomSetting,isTing);

		return res;

	}

	enum ENUM_SUIT_TYPE {
		Double, Triple, Straight
	}

	static class SuitSets {
		public ENUM_SUIT_TYPE type;
		public ArrayList<Poker> lacks = new ArrayList<Poker>();
		public ArrayList<Poker> poker = new ArrayList<Poker>();

		public SuitSets(ENUM_SUIT_TYPE type) {
			this.type = type;
		}

	}

	static class Mahjong {

		int count = 0;// 胡牌组合个数

		int lackCount = 0;// 癞子个数

		public SettleVO hu(HashMap<Integer, Poker> hand, ArrayList<Poker> sprite,boolean isSelf, Poker test, RoomSetting roomSetting
				,boolean isTing) {

			this.count = 0;

			this.lackCount = 0;

			SettleVO res = new SettleVO();

			ArrayList<Poker> poker = new ArrayList<Poker>();

			for (Entry<Integer, Poker> entry : hand.entrySet()) {
				if (this.isUniversal(sprite, entry.getValue())) {
					lackCount++;
				} else {
					poker.add(entry.getValue());
				}
			}

			if (test != null) {
				if (this.isUniversal(sprite, test) && isSelf) {
					lackCount++;
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

//			 for (int i = 0; i < poker.size(); i++) {
//			 System.out.print(","+Tools.mahjong(poker.get(i).color,poker.get(i).size));
//			 }
//			 System.out.print("\n");

			Result result = new Result(poker);

			this.CheckHu(result, res,isTing);

			return res;
		}

		public void CheckHu(Result result, SettleVO settleVO,boolean isTing) {

			if (isTing && result.lacks > lackCount + 1) {
				return;
			}
			
			if (!isTing && result.lacks > lackCount) {
				return;
			}

			if (result.poker.size() <= 0) {

				if (isTing) {
					result.add(settleVO);					
					if (lackCount-result.lacks > 0) {
						for (int i = 0; i < 27; i++) {
							settleVO.lacks.add(new Poker());
						}
					}
//					System.out.println("---------可以听牌------------");
					return;
				}

				settleVO.code = 1;
				result.add(settleVO);

				count++;
				System.out.println(" - : " + count);
				return;
			}

			CalculateStraight(result, settleVO,isTing);

			CalculateTriple(result, settleVO,isTing);

			CalculateDouble(result, settleVO,isTing);
			
			CalculateDoubletoStree(result, settleVO,isTing);//对子配刻子

			CalculateStraightLR(result, settleVO,isTing);

			CalculateStraightMD(result, settleVO,isTing);

			CalculateSigle(result, settleVO,isTing);

		}

		/**
		 * 顺子
		 * 
		 * @param res
		 */
		public void CalculateStraight(Result res, SettleVO settleVO,boolean isTing) {
			if (res.poker.size() < 3) {
				return;
			}

			Result result = new Result(res);

			Poker poker = result.poker.remove(0);
			if (poker.color > 3) {
				return;
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

//					System.out.println("---------顺子-----------");
//					System.out.println(this.format(poker) + "," + this.format(poker1) + "," + this.format(poker2));

					result.straights.add(sets);

//					for (int j = 0; j < result.poker.size(); j++) {
//						System.out.print("," + this.format(result.poker.get(j)));
//					}
//					System.out.println("\n-------------");

					this.CheckHu(result, settleVO,isTing);
					return;
				}
			}

		}

		// 刻子
		public void CalculateTriple(Result res, SettleVO settleVO,boolean isTing) {

			if (res.poker.size() < 3) {
				return;
			}

			Result result = new Result(res);

			Poker poker = result.poker.remove(0);

			if (this.isEqual(poker, result.poker.get(0)) && this.isEqual(poker, result.poker.get(1))) {

				SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Triple);

				sets.poker.add(poker);
				sets.poker.add(result.poker.remove(0));
				sets.poker.add(result.poker.remove(0));

//				System.out.println("---------刻子-----------");
//				System.out.println(this.format(poker) + "," + this.format(sets.poker.get(1)) + ","
//						+ this.format(sets.poker.get(2)));

				result.triples.add(sets);

//				for (int j = 0; j < result.poker.size(); j++) {
//					System.out.print("," + this.format(result.poker.get(j)));
//				}
//				System.out.println("\n-------------");

				this.CheckHu(result, settleVO,isTing);
				return;

			}

		}

		// 对子
		public void CalculateDouble(Result res, SettleVO settleVO,boolean isTing) {

			if (res.poker.size() < 2) {
				return;
			}

			Result result = new Result(res);

			Poker poker = result.poker.remove(0);

			if (this.isEqual(poker, result.poker.get(0))) {
				Poker twins = result.poker.remove(0);

				{
					if (result.doubles.size() == 0) {
						SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Double);

						sets.poker.add(poker);
						sets.poker.add(twins);

//						System.out.println("---------对子-----------");
//						System.out.println(this.format(poker) + "," + this.format(twins));

						result.doubles.add(sets);

//						for (int j = 0; j < result.poker.size(); j++) {
//							System.out.print("," + this.format(result.poker.get(j)));
//						}
//						System.out.println("\n-------------");

						this.CheckHu(result, settleVO,isTing);

					}
				}

//				{
//					SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Triple);
//
//					sets.poker.add(poker);
//					sets.poker.add(twins);
//					sets.lacks.add(new Poker(0, poker.color, poker.size));
//
//					System.out.println("---------对子-----配刻------");
//					System.out.println(
//							this.format(poker) + "," + this.format(twins) + ",--" + this.format(sets.lacks.get(0)));
//
//					result.lacks++;
//
//					// result.doubles.clear();
//					result.triples.add(sets);
//
//					for (int j = 0; j < result.poker.size(); j++) {
//						System.out.print("," + this.format(result.poker.get(j)));
//					}
//					System.out.println("\n-------------");
//
//					this.CheckHu(result, settleVO);
//				}
				return;
			}
		}

		// 对子配刻子
		public void CalculateDoubletoStree(Result res, SettleVO settleVO,boolean isTing) {

			if (res.poker.size() < 2) {
				return;
			}

			Result result = new Result(res);

			Poker poker = result.poker.remove(0);

			if (this.isEqual(poker, result.poker.get(0))) {
				Poker twins = result.poker.remove(0);

				SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Triple);

				sets.poker.add(poker);
				sets.poker.add(twins);
				sets.lacks.add(new Poker(0, poker.color, poker.size));

//				System.out.println("---------对子-----配刻------");
//				System.out.println(
//						this.format(poker) + "," + this.format(twins) + ",--" + this.format(sets.lacks.get(0)));

				result.lacks++;

				// result.doubles.clear();
				result.triples.add(sets);

//				for (int j = 0; j < result.poker.size(); j++) {
//					System.out.print("," + this.format(result.poker.get(j)));
//				}
//				System.out.println("\n-------------");

				this.CheckHu(result, settleVO,isTing);
			

				return;
			}

		}

		// 顺子缺两边
		public void CalculateStraightLR(Result res, SettleVO settleVO,boolean isTing) {

			if (res.poker.size() < 2) {
				return;
			}

			Result result = new Result(res);

			Poker poker = result.poker.remove(0);
			if (poker.color > 3) {
				return;
			}

			for (int i = 0; i < result.poker.size(); i++) {
				if (poker.size == 1 && poker.size + 1 == result.poker.get(i).size
						&& poker.color == result.poker.get(i).color) {

					SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Straight);

					sets.poker.add(poker);
					sets.poker.add(result.poker.remove(i));
					sets.lacks.add(new Poker(0, poker.color, poker.size + 2));

//					System.out.println("---------顺子缺两边-----------");
//					System.out.println(this.format(poker) + "," + this.format(sets.poker.get(1)) + ","
//							+ this.format(sets.lacks.get(0)));

					result.lacks++;

					result.straights.add(sets);

//					for (int j = 0; j < result.poker.size(); j++) {
//						System.out.print("," + this.format(result.poker.get(j)));
//					}
//					System.out.println("\n-------------");

					this.CheckHu(result, settleVO,isTing);

					return;
				} else if (poker.size == 8 && poker.size + 1 == result.poker.get(i).size
						&& poker.color == result.poker.get(i).color) {

					SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Straight);

					sets.poker.add(poker);
					sets.poker.add(result.poker.remove(i));
					sets.lacks.add(new Poker(0, poker.color, poker.size - 1));

//					System.out.println("---------顺子缺两边-----------");
//					System.out.println(this.format(poker) + "," + this.format(sets.poker.get(1)) + ","
//							+ this.format(sets.lacks.get(0)));

					result.lacks++;

					result.straights.add(sets);

//					for (int j = 0; j < result.poker.size(); j++) {
//						System.out.print("," + this.format(result.poker.get(j)));
//					}
//					System.out.println("\n-------------");

					this.CheckHu(result, settleVO,isTing);

					return;
				} else if (poker.size + 1 == result.poker.get(i).size && poker.color == result.poker.get(i).color) {

					SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Straight);

					sets.poker.add(poker);
					sets.poker.add(result.poker.remove(i));
					sets.lacks.add(new Poker(0, poker.color, poker.size - 1));
					sets.lacks.add(new Poker(0, poker.color, poker.size + 2));

//					System.out.println("---------顺子缺两边-----------");
//					System.out.println(this.format(poker) + "," + this.format(sets.poker.get(1)) + ","
//							+ this.format(sets.lacks.get(0)) + "|" + this.format(sets.lacks.get(1)));

					result.lacks++;

					result.straights.add(sets);

//					for (int j = 0; j < result.poker.size(); j++) {
//						System.out.print("," + this.format(result.poker.get(j)));
//					}
//					System.out.println("\n-------------");

					this.CheckHu(result, settleVO,isTing);

					return;
				}
			}

		}

		// 顺子夹中间
		public void CalculateStraightMD(Result res, SettleVO settleVO,boolean isTing) {

			if (res.poker.size() < 2) {
				return;
			}

			Result result = new Result(res);

			Poker poker = result.poker.remove(0);
			if (poker.color > 3) {
				return;
			}
			for (int i = 0; i < result.poker.size(); i++) {
				if (poker.size + 2 == result.poker.get(i).size && poker.color == result.poker.get(i).color) {

					SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Straight);

					sets.poker.add(poker);
					sets.poker.add(result.poker.remove(i));
					sets.lacks.add(new Poker(0, poker.color, poker.size + 1));

//					System.out.println("---------顺子夹中间-----------");
//					System.out.println(this.format(poker) + "," + this.format(sets.poker.get(1)) + ","
//							+ this.format(sets.lacks.get(0)));

					result.lacks++;

					result.straights.add(sets);

//					for (int j = 0; j < result.poker.size(); j++) {
//						System.out.print("," + this.format(result.poker.get(j)));
//					}
//					System.out.println("\n-------------");

					this.CheckHu(result, settleVO,isTing);

					return;
				}
			}

		}

		// 单牌
		public void CalculateSigle(Result res, SettleVO settleVO,boolean isTing) {

			if (res.poker.size() < 1) {
				return;
			}

			Result result = new Result(res);

			Poker poker = result.poker.remove(0);

			{
				if (result.doubles.size() == 0) {

					SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Double);

					sets.poker.add(poker);
					sets.lacks.add(new Poker(0, poker.color, poker.size));

//					System.out.println("---------单牌------配对-----");
//					System.out.println(this.format(poker) + "," + this.format(sets.lacks.get(0)));

					result.lacks++;

					result.doubles.add(sets);

//					for (int j = 0; j < result.poker.size(); j++) {
//						System.out.print("," + this.format(result.poker.get(j)));
//					}
//					System.out.println("\n-------------");

					this.CheckHu(result, settleVO,isTing);

				}
			}

			/** -=====================================- **/

			{
				SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Triple);

				sets.poker.add(poker);
				sets.lacks.add(new Poker(0, poker.color, poker.size));
				sets.lacks.add(new Poker(0, poker.color, poker.size));

//				System.out.println("---------单牌------刻子-----");
//				System.out.println(this.format(poker) + "," + this.format(sets.lacks.get(0)) + ","
//						+ this.format(sets.lacks.get(1)));

				result.lacks += 2;

				// result.doubles.clear();
				result.triples.add(sets);

//				for (int j = 0; j < result.poker.size(); j++) {
//					System.out.print("," + this.format(result.poker.get(j)));
//				}
//				System.out.println("\n-------------");

				this.CheckHu(result, settleVO,isTing);
			}

			/** -=====================================- **/

			{
				SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Straight);

				sets.poker.add(poker);
				if (poker.size > 2) {
					sets.lacks.add(new Poker(0, poker.color, poker.size - 2));
				}
				if (poker.size > 1) {
					sets.lacks.add(new Poker(0, poker.color, poker.size - 1));
				}
				if (poker.size < 9) {
					sets.lacks.add(new Poker(0, poker.color, poker.size + 1));
				}
				if (poker.size < 8) {
					sets.lacks.add(new Poker(0, poker.color, poker.size + 2));
				}
//				System.out.println("---------单牌-----顺子------");
//				System.out.print("," + this.format(poker));
//				for (int j = 0; j < sets.lacks.size(); j++) {
//					System.out.print("," + this.format(sets.lacks.get(j)));
//				}
//				System.out.println("\n-------------");

				result.lacks += 2;

				result.straights.add(sets);

//				for (int j = 0; j < result.poker.size(); j++) {
//					System.out.print("," + this.format(result.poker.get(j)));
//				}
//				System.out.println("\n-------------");

				this.CheckHu(result, settleVO,isTing);
			}

		}

		// 判断七小对
		public boolean qxd(HashMap<Integer, Poker> hand, ArrayList<Poker> universal, Poker test, boolean isSelf) {

			if (hand.size() != 13) {
				return false;
			}

			ArrayList<Poker> poker = new ArrayList<Poker>();

			int hun = 0;
			for (Entry<Integer, Poker> entry : hand.entrySet()) {
				if (this.isUniversal(universal, entry.getValue())) {
					hun++;
				} else {
					poker.add(entry.getValue());
				}
			}

			if (test != null) {
				if (this.isUniversal(universal, test) && isSelf) {
					hun++;
				} else {
					poker.add(test);
				}
			}

			LinkedHashMap<Integer, Poker> values = this.array_count_values(poker);

			int single = 0;

			for (Entry<Integer, Poker> entry : values.entrySet()) {
				if (entry.getValue().amount == 1) {
					single++;
				} else if (entry.getValue().amount == 3) {
					single++;
				}
			}

			if (test == null) {
				if (single == 1 || hun > single) {
					return true;
				}
			} else {
				if (single == 0 || hun >= single) {
					System.out.println("七小对");
					return true;
				}
			}

			return false;
		}
		
		public boolean laizihu(HashMap<Integer, Poker> hand, ArrayList<Poker> universal, Poker test) {
			int sprite = 0;
			if (test != null && this.isUniversal(universal, test)) {
				sprite++;
			}
			for (Entry<Integer, Poker> entry : hand.entrySet()) {
				if (this.isUniversal(universal, entry.getValue())) {
					sprite++;
				}
			}
			if (sprite >= 4) {
				return true;
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
		ArrayList<SuitSets> straights = new ArrayList<SuitSets>();
		ArrayList<SuitSets> triples = new ArrayList<SuitSets>();
		ArrayList<SuitSets> doubles = new ArrayList<SuitSets>();

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
		}

		public void add(SettleVO res) {

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
						res.lacks.add(this.straights.get(i).lacks.get(j));
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
						res.lacks.add(this.triples.get(i).lacks.get(j));
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
						res.lacks.add(this.doubles.get(i).lacks.get(j));
					}
				}
				resultVO.pokers.add(pokerVO);
			}

			res.result.add(resultVO);
		}

	}

	// class Poker {
	// public int index;
	//
	// public int color;
	//
	// public int size;
	//
	// public Poker(int index, int color, int size) {
	// this.index = index;
	// this.color = color;
	// this.size = size;
	// }
	//
	// public String toString() {
	// return this.color + "_" + this.size;
	// }
	// }

}
