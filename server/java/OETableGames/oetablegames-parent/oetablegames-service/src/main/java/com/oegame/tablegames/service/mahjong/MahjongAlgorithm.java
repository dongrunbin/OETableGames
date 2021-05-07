package com.oegame.tablegames.service.mahjong;

import com.oegame.tablegames.service.mahjong.model.CombinationType;
import com.oegame.tablegames.service.mahjong.model.MahjongGroup;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import com.oegame.tablegames.service.mahjong.model.Mahjong;
import com.oegame.tablegames.service.mahjong.model.MahjongVO;
import com.oegame.tablegames.service.mahjong.model.ResultVO;
import com.oegame.tablegames.service.mahjong.model.RoomSetting;
import com.oegame.tablegames.service.mahjong.model.Seat;
import com.oegame.tablegames.service.mahjong.model.SettleVO;
import com.oegame.tablegames.service.mahjong.model.Tools;
import java.util.Set;

/**
 * 麻将胡牌算法 
 */
public class MahjongAlgorithm {

	public SettleVO checkHu(Seat seat, Mahjong test, boolean isSelf, RoomSetting roomSetting, boolean isTing) {

		SettleVO res = checkHu(seat.mahjong,seat.universal,isSelf, test, roomSetting,isTing);
		return res;
	}

	public boolean isUniversal(ArrayList<Mahjong> sprite, Mahjong mahjong) {
		for (int i = 0; i < sprite.size(); i++) {
			if (mahjong.size == sprite.get(i).size && mahjong.color == sprite.get(i).color) {
				return true;
			}
		}
		return false;
	}

	/**
	 * 判断是否能吃
	 *
	 * @param hand
	 * @param test
	 * @return ArrayList<Mahjong[]>
	 */
	public ArrayList<Mahjong[]> checkChi(HashMap<Integer, Mahjong> hand, ArrayList<Mahjong> sprite, Mahjong test) {

		ArrayList<Mahjong[]> list = new ArrayList<Mahjong[]>();

		if (test.color > 3) {
			return list;
		}

		HashMap<Integer, Mahjong> group = new HashMap<Integer, Mahjong>();

		Set<Entry<Integer, Mahjong>> pset = hand.entrySet();
		for (Entry<Integer, Mahjong> entry : pset) {
			if (!this.isUniversal(sprite, entry.getValue())) {
				int key = (entry.getValue().size << 8) + entry.getValue().color;
				group.put(key, entry.getValue());
			}
		}

		int k1 = 0;
		int k2 = 0;
		ArrayList<Integer> k = new ArrayList<Integer>();

		for (int i = -2; i <= 2; i++) {
			k1 = test.size + i;
			if (k1 < 1 || k1 > 9 || k1 == test.size) {
				continue;
			}
			k.add(k1);
		}

		for (int i = 0; i < k.size() - 1; i++) {
			k1 = (k.get(i) << 8) + test.color;
			k2 = (k.get(i + 1) << 8) + test.color;
			if (group.containsKey(k1) && group.containsKey(k2)) {
				list.add(new Mahjong[] { group.get(k1), group.get(k2) });
			}
		}

		return list;
	}

	/**
	 * 判断是否能碰
	 *
	 * @param hand
	 * @param test
	 * @return boolean
	 */
	public boolean checkPeng(HashMap<Integer, Mahjong> hand, Mahjong test) {

		ArrayList<Mahjong> mahjong = new ArrayList<Mahjong>();

		Set<Entry<Integer, Mahjong>> pset = hand.entrySet();
		for (Entry<Integer, Mahjong> entry : pset) {
			mahjong.add(entry.getValue());
		}

		LinkedHashMap<Integer, Mahjong> res = this.array_count_values(mahjong);

		int tags = (test.size << 8) + test.color;

		if (res.containsKey(tags) && res.get(tags).amount >= 2) {
			return true;
		}

		return false;
	}

	/**
	 * 判断是否能补杠
	 *
	 * @param hand
	 * @param useMahjongGroup
	 * @param test
	 * @param checkHand
	 * @return int
	 */
	public int checkBuGang(HashMap<Integer, Mahjong> hand, ArrayList<MahjongGroup> useMahjongGroup, Mahjong test, boolean checkHand) {
		if (test == null) {
			return -1;
		}

		for (int i = 0; i < useMahjongGroup.size(); i++) {
			if (useMahjongGroup.get(i).typeId == CombinationType.POKER_TYPE_PENG) {
				Mahjong pk = useMahjongGroup.get(i).mahjong.get(0);
				if (pk.color == test.color && pk.size == test.size) {
					return i;
				}
				if (checkHand) {
					Set<Entry<Integer, Mahjong>> pset = hand.entrySet();
					for (Entry<Integer, Mahjong> entry : pset) {
						if (pk.color == entry.getValue().color && pk.size == entry.getValue().size) {
							return i;
						}
					}
				}
			}
		}
		return -1;
	}

	/**
	 * 判断是否能明杠、暗杠
	 *
	 * @param hand
	 * @param test
	 * @param self
	 * @return boolean
	 */
	public boolean checkGang(HashMap<Integer, Mahjong> hand, Mahjong test, boolean self) {
		ArrayList<Mahjong> mahjong = new ArrayList<Mahjong>();

		Set<Entry<Integer, Mahjong>> pset = hand.entrySet();
		for (Entry<Integer, Mahjong> entry : pset) {
			mahjong.add(entry.getValue());
		}

		if (test != null) {
			mahjong.add(test);
		}

		LinkedHashMap<Integer, Mahjong> res = this.array_count_values(mahjong);

		Set<Entry<Integer, Mahjong>> set = res.entrySet();
		for (Entry<Integer, Mahjong> entry : set) {
			if (self) { //是自己摸牌
				if (entry.getValue().amount >= 4) {
					return true;
				}
			} else { //是玩家打出来的牌
				if (entry.getValue().amount >= 4 && entry.getValue().size == test.size && entry.getValue().color == test.color) {
					return true;
				}
			}
		}

		return false;
	}

	/**
	 * 同样牌出现的数量
	 *
	 * @param mahjong
	 * @return LinkedHashMap<Integer,Mahjong>
	 */
	public static LinkedHashMap<Integer, Mahjong> array_count_values(ArrayList<Mahjong> mahjong) {
		LinkedHashMap<Integer, Mahjong> array = new LinkedHashMap<Integer, Mahjong>();

		// 装填到hashMap
		for (int i = 0; i < mahjong.size(); i++) {
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

		return array;
	}

	// 通用胡牌检测
	public SettleVO checkHu(HashMap<Integer, Mahjong> hand, ArrayList<Mahjong> universal,boolean isSelf, Mahjong test,
			RoomSetting roomSetting, boolean isTing) {

		Card card = new Card();

		SettleVO res = new SettleVO();

		// 判断七小对
		if (roomSetting != null && roomSetting.isQxd) {
			if (card.qxd(hand, universal, test, isSelf)) {
				if (test == null) {
					res.lacks.add(new Mahjong());
				} else {
					res.code = 1;
					return res;
				}
			}
		}		

		res = card.hu(hand, universal,isSelf, test, roomSetting,isTing);

		return res;

	}

	enum ENUM_SUIT_TYPE {
		Double, Triple, Straight
	}

	static class SuitSets {
		public ENUM_SUIT_TYPE type;
		public ArrayList<Mahjong> lacks = new ArrayList<Mahjong>();
		public ArrayList<Mahjong> mahjong = new ArrayList<Mahjong>();

		public SuitSets(ENUM_SUIT_TYPE type) {
			this.type = type;
		}

	}

	public static class Card {

		int count = 0;// 胡牌组合个数

		int lackCount = 0;// 癞子个数

		public SettleVO hu(HashMap<Integer, Mahjong> hand, ArrayList<Mahjong> sprite,boolean isSelf, Mahjong test, RoomSetting roomSetting
				,boolean isTing) {

			this.count = 0;

			this.lackCount = 0;

			SettleVO res = new SettleVO();

			ArrayList<Mahjong> mahjong = new ArrayList<Mahjong>();

			for (Entry<Integer, Mahjong> entry : hand.entrySet()) {
				if (this.isUniversal(sprite, entry.getValue())) {
					lackCount++;
				} else {
					mahjong.add(entry.getValue());
				}
			}

			if (test != null) {
				if (this.isUniversal(sprite, test) && isSelf) {
					lackCount++;
				} else {
					mahjong.add(test);
				}
			}

			Collections.sort(mahjong, new Comparator<Mahjong>() {
				public int compare(Mahjong o1, Mahjong o2) {
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

			Result result = new Result(mahjong);

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

			if (result.mahjong.size() <= 0) {

				if (isTing) {
					result.add(settleVO);					
					if (lackCount-result.lacks > 0) {
						for (int i = 0; i < 27; i++) {
							settleVO.lacks.add(new Mahjong());
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
			if (res.mahjong.size() < 3) {
				return;
			}

			Result result = new Result(res);

			Mahjong mahjong = result.mahjong.remove(0);
			if (mahjong.color > 3) {
				return;
			}

			int pos1 = -1;
			int pos2 = -1;

			for (int i = 0; i < result.mahjong.size(); i++) {
				if (pos1 == -1 && result.mahjong.get(i).color == mahjong.color
						&& mahjong.size + 1 == result.mahjong.get(i).size) {
					pos1 = i;
				} else if (pos2 == -1 && result.mahjong.get(i).color == mahjong.color
						&& mahjong.size + 2 == result.mahjong.get(i).size) {
					pos2 = i;
				}

				if (pos1 > -1 && pos2 > -1) {
					SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Straight);

					sets.mahjong.add(mahjong);
					Mahjong mahjong2 = result.mahjong.remove(pos2);
					Mahjong mahjong1 = result.mahjong.remove(pos1);
					sets.mahjong.add(mahjong1);
					sets.mahjong.add(mahjong2);

//					System.out.println("---------顺子-----------");
//					System.out.println(this.format(com.oegame.tablegames.service.mahjong) + "," + this.format(mahjong1) + "," + this.format(mahjong2));

					result.straights.add(sets);

//					for (int j = 0; j < result.com.oegame.tablegames.service.mahjong.size(); j++) {
//						System.out.print("," + this.format(result.com.oegame.tablegames.service.mahjong.get(j)));
//					}
//					System.out.println("\n-------------");

					this.CheckHu(result, settleVO,isTing);
					return;
				}
			}

		}

		// 刻子
		public void CalculateTriple(Result res, SettleVO settleVO,boolean isTing) {

			if (res.mahjong.size() < 3) {
				return;
			}

			Result result = new Result(res);

			Mahjong mahjong = result.mahjong.remove(0);

			if (this.isEqual(mahjong, result.mahjong.get(0)) && this.isEqual(mahjong, result.mahjong.get(1))) {

				SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Triple);

				sets.mahjong.add(mahjong);
				sets.mahjong.add(result.mahjong.remove(0));
				sets.mahjong.add(result.mahjong.remove(0));

//				System.out.println("---------刻子-----------");
//				System.out.println(this.format(com.oegame.tablegames.service.mahjong) + "," + this.format(sets.com.oegame.tablegames.service.mahjong.get(1)) + ","
//						+ this.format(sets.com.oegame.tablegames.service.mahjong.get(2)));

				result.triples.add(sets);

//				for (int j = 0; j < result.com.oegame.tablegames.service.mahjong.size(); j++) {
//					System.out.print("," + this.format(result.com.oegame.tablegames.service.mahjong.get(j)));
//				}
//				System.out.println("\n-------------");

				this.CheckHu(result, settleVO,isTing);
				return;

			}

		}

		// 对子
		public void CalculateDouble(Result res, SettleVO settleVO,boolean isTing) {

			if (res.mahjong.size() < 2) {
				return;
			}

			Result result = new Result(res);

			Mahjong mahjong = result.mahjong.remove(0);

			if (this.isEqual(mahjong, result.mahjong.get(0))) {
				Mahjong twins = result.mahjong.remove(0);

				{
					if (result.doubles.size() == 0) {
						SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Double);

						sets.mahjong.add(mahjong);
						sets.mahjong.add(twins);

//						System.out.println("---------对子-----------");
//						System.out.println(this.format(com.oegame.tablegames.service.mahjong) + "," + this.format(twins));

						result.doubles.add(sets);

//						for (int j = 0; j < result.com.oegame.tablegames.service.mahjong.size(); j++) {
//							System.out.print("," + this.format(result.com.oegame.tablegames.service.mahjong.get(j)));
//						}
//						System.out.println("\n-------------");

						this.CheckHu(result, settleVO,isTing);

					}
				}

//				{
//					SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Triple);
//
//					sets.com.oegame.tablegames.service.mahjong.add(com.oegame.tablegames.service.mahjong);
//					sets.com.oegame.tablegames.service.mahjong.add(twins);
//					sets.lacks.add(new Mahjong(0, com.oegame.tablegames.service.mahjong.color, com.oegame.tablegames.service.mahjong.size));
//
//					System.out.println("---------对子-----配刻------");
//					System.out.println(
//							this.format(com.oegame.tablegames.service.mahjong) + "," + this.format(twins) + ",--" + this.format(sets.lacks.get(0)));
//
//					result.lacks++;
//
//					// result.doubles.clear();
//					result.triples.add(sets);
//
//					for (int j = 0; j < result.com.oegame.tablegames.service.mahjong.size(); j++) {
//						System.out.print("," + this.format(result.com.oegame.tablegames.service.mahjong.get(j)));
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

			if (res.mahjong.size() < 2) {
				return;
			}

			Result result = new Result(res);

			Mahjong mahjong = result.mahjong.remove(0);

			if (this.isEqual(mahjong, result.mahjong.get(0))) {
				Mahjong twins = result.mahjong.remove(0);

				SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Triple);

				sets.mahjong.add(mahjong);
				sets.mahjong.add(twins);
				sets.lacks.add(new Mahjong(0, mahjong.color, mahjong.size));

//				System.out.println("---------对子-----配刻------");
//				System.out.println(
//						this.format(com.oegame.tablegames.service.mahjong) + "," + this.format(twins) + ",--" + this.format(sets.lacks.get(0)));

				result.lacks++;

				// result.doubles.clear();
				result.triples.add(sets);

//				for (int j = 0; j < result.com.oegame.tablegames.service.mahjong.size(); j++) {
//					System.out.print("," + this.format(result.com.oegame.tablegames.service.mahjong.get(j)));
//				}
//				System.out.println("\n-------------");

				this.CheckHu(result, settleVO,isTing);
			

				return;
			}

		}

		// 顺子缺两边
		public void CalculateStraightLR(Result res, SettleVO settleVO,boolean isTing) {

			if (res.mahjong.size() < 2) {
				return;
			}

			Result result = new Result(res);

			Mahjong mahjong = result.mahjong.remove(0);
			if (mahjong.color > 3) {
				return;
			}

			for (int i = 0; i < result.mahjong.size(); i++) {
				if (mahjong.size == 1 && mahjong.size + 1 == result.mahjong.get(i).size
						&& mahjong.color == result.mahjong.get(i).color) {

					SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Straight);

					sets.mahjong.add(mahjong);
					sets.mahjong.add(result.mahjong.remove(i));
					sets.lacks.add(new Mahjong(0, mahjong.color, mahjong.size + 2));

//					System.out.println("---------顺子缺两边-----------");
//					System.out.println(this.format(com.oegame.tablegames.service.mahjong) + "," + this.format(sets.com.oegame.tablegames.service.mahjong.get(1)) + ","
//							+ this.format(sets.lacks.get(0)));

					result.lacks++;

					result.straights.add(sets);

//					for (int j = 0; j < result.com.oegame.tablegames.service.mahjong.size(); j++) {
//						System.out.print("," + this.format(result.com.oegame.tablegames.service.mahjong.get(j)));
//					}
//					System.out.println("\n-------------");

					this.CheckHu(result, settleVO,isTing);

					return;
				} else if (mahjong.size == 8 && mahjong.size + 1 == result.mahjong.get(i).size
						&& mahjong.color == result.mahjong.get(i).color) {

					SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Straight);

					sets.mahjong.add(mahjong);
					sets.mahjong.add(result.mahjong.remove(i));
					sets.lacks.add(new Mahjong(0, mahjong.color, mahjong.size - 1));

//					System.out.println("---------顺子缺两边-----------");
//					System.out.println(this.format(com.oegame.tablegames.service.mahjong) + "," + this.format(sets.com.oegame.tablegames.service.mahjong.get(1)) + ","
//							+ this.format(sets.lacks.get(0)));

					result.lacks++;

					result.straights.add(sets);

//					for (int j = 0; j < result.com.oegame.tablegames.service.mahjong.size(); j++) {
//						System.out.print("," + this.format(result.com.oegame.tablegames.service.mahjong.get(j)));
//					}
//					System.out.println("\n-------------");

					this.CheckHu(result, settleVO,isTing);

					return;
				} else if (mahjong.size + 1 == result.mahjong.get(i).size && mahjong.color == result.mahjong.get(i).color) {

					SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Straight);

					sets.mahjong.add(mahjong);
					sets.mahjong.add(result.mahjong.remove(i));
					sets.lacks.add(new Mahjong(0, mahjong.color, mahjong.size - 1));
					sets.lacks.add(new Mahjong(0, mahjong.color, mahjong.size + 2));

//					System.out.println("---------顺子缺两边-----------");
//					System.out.println(this.format(com.oegame.tablegames.service.mahjong) + "," + this.format(sets.com.oegame.tablegames.service.mahjong.get(1)) + ","
//							+ this.format(sets.lacks.get(0)) + "|" + this.format(sets.lacks.get(1)));

					result.lacks++;

					result.straights.add(sets);

//					for (int j = 0; j < result.com.oegame.tablegames.service.mahjong.size(); j++) {
//						System.out.print("," + this.format(result.com.oegame.tablegames.service.mahjong.get(j)));
//					}
//					System.out.println("\n-------------");

					this.CheckHu(result, settleVO,isTing);

					return;
				}
			}

		}

		// 顺子夹中间
		public void CalculateStraightMD(Result res, SettleVO settleVO,boolean isTing) {

			if (res.mahjong.size() < 2) {
				return;
			}

			Result result = new Result(res);

			Mahjong mahjong = result.mahjong.remove(0);
			if (mahjong.color > 3) {
				return;
			}
			for (int i = 0; i < result.mahjong.size(); i++) {
				if (mahjong.size + 2 == result.mahjong.get(i).size && mahjong.color == result.mahjong.get(i).color) {

					SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Straight);

					sets.mahjong.add(mahjong);
					sets.mahjong.add(result.mahjong.remove(i));
					sets.lacks.add(new Mahjong(0, mahjong.color, mahjong.size + 1));

//					System.out.println("---------顺子夹中间-----------");
//					System.out.println(this.format(com.oegame.tablegames.service.mahjong) + "," + this.format(sets.com.oegame.tablegames.service.mahjong.get(1)) + ","
//							+ this.format(sets.lacks.get(0)));

					result.lacks++;

					result.straights.add(sets);

//					for (int j = 0; j < result.com.oegame.tablegames.service.mahjong.size(); j++) {
//						System.out.print("," + this.format(result.com.oegame.tablegames.service.mahjong.get(j)));
//					}
//					System.out.println("\n-------------");

					this.CheckHu(result, settleVO,isTing);

					return;
				}
			}

		}

		// 单牌
		public void CalculateSigle(Result res, SettleVO settleVO,boolean isTing) {

			if (res.mahjong.size() < 1) {
				return;
			}

			Result result = new Result(res);

			Mahjong mahjong = result.mahjong.remove(0);

			{
				if (result.doubles.size() == 0) {

					SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Double);

					sets.mahjong.add(mahjong);
					sets.lacks.add(new Mahjong(0, mahjong.color, mahjong.size));

//					System.out.println("---------单牌------配对-----");
//					System.out.println(this.format(com.oegame.tablegames.service.mahjong) + "," + this.format(sets.lacks.get(0)));

					result.lacks++;

					result.doubles.add(sets);

//					for (int j = 0; j < result.com.oegame.tablegames.service.mahjong.size(); j++) {
//						System.out.print("," + this.format(result.com.oegame.tablegames.service.mahjong.get(j)));
//					}
//					System.out.println("\n-------------");

					this.CheckHu(result, settleVO,isTing);

				}
			}

			/** -=====================================- **/

			{
				SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Triple);

				sets.mahjong.add(mahjong);
				sets.lacks.add(new Mahjong(0, mahjong.color, mahjong.size));
				sets.lacks.add(new Mahjong(0, mahjong.color, mahjong.size));

//				System.out.println("---------单牌------刻子-----");
//				System.out.println(this.format(com.oegame.tablegames.service.mahjong) + "," + this.format(sets.lacks.get(0)) + ","
//						+ this.format(sets.lacks.get(1)));

				result.lacks += 2;

				// result.doubles.clear();
				result.triples.add(sets);

//				for (int j = 0; j < result.com.oegame.tablegames.service.mahjong.size(); j++) {
//					System.out.print("," + this.format(result.com.oegame.tablegames.service.mahjong.get(j)));
//				}
//				System.out.println("\n-------------");

				this.CheckHu(result, settleVO,isTing);
			}

			/** -=====================================- **/

			{
				SuitSets sets = new SuitSets(ENUM_SUIT_TYPE.Straight);

				sets.mahjong.add(mahjong);
				if (mahjong.size > 2) {
					sets.lacks.add(new Mahjong(0, mahjong.color, mahjong.size - 2));
				}
				if (mahjong.size > 1) {
					sets.lacks.add(new Mahjong(0, mahjong.color, mahjong.size - 1));
				}
				if (mahjong.size < 9) {
					sets.lacks.add(new Mahjong(0, mahjong.color, mahjong.size + 1));
				}
				if (mahjong.size < 8) {
					sets.lacks.add(new Mahjong(0, mahjong.color, mahjong.size + 2));
				}
//				System.out.println("---------单牌-----顺子------");
//				System.out.print("," + this.format(com.oegame.tablegames.service.mahjong));
//				for (int j = 0; j < sets.lacks.size(); j++) {
//					System.out.print("," + this.format(sets.lacks.get(j)));
//				}
//				System.out.println("\n-------------");

				result.lacks += 2;

				result.straights.add(sets);

//				for (int j = 0; j < result.com.oegame.tablegames.service.mahjong.size(); j++) {
//					System.out.print("," + this.format(result.com.oegame.tablegames.service.mahjong.get(j)));
//				}
//				System.out.println("\n-------------");

				this.CheckHu(result, settleVO,isTing);
			}

		}

		// 判断七小对
		public boolean qxd(HashMap<Integer, Mahjong> hand, ArrayList<Mahjong> universal, Mahjong test, boolean isSelf) {

			if (hand.size() != 13) {
				return false;
			}

			ArrayList<Mahjong> mahjong = new ArrayList<Mahjong>();

			int hun = 0;
			for (Entry<Integer, Mahjong> entry : hand.entrySet()) {
				if (this.isUniversal(universal, entry.getValue())) {
					hun++;
				} else {
					mahjong.add(entry.getValue());
				}
			}

			if (test != null) {
				if (this.isUniversal(universal, test) && isSelf) {
					hun++;
				} else {
					mahjong.add(test);
				}
			}

			LinkedHashMap<Integer, Mahjong> values = array_count_values(mahjong);

			int single = 0;

			for (Entry<Integer, Mahjong> entry : values.entrySet()) {
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

		public boolean isUniversal(ArrayList<Mahjong> sprite, Mahjong test) {
			for (int i = 0; i < sprite.size(); i++) {
				if (test.size == sprite.get(i).size && test.color == sprite.get(i).color) {
					return true;
				}
			}
			return false;
		}

		public String format(Mahjong mahjong) {
			return Tools.mahjong(mahjong.color, mahjong.size);
		}

		public boolean isEqual(Mahjong mahjong1, Mahjong mahjong2) {
			if (mahjong1.color == mahjong2.color && mahjong1.size == mahjong2.size) {
				return true;
			}
			return false;
		}
	}

	static class Result {

		ArrayList<Mahjong> mahjong = new ArrayList<Mahjong>();
		int lacks = 0;
		ArrayList<SuitSets> straights = new ArrayList<SuitSets>();
		ArrayList<SuitSets> triples = new ArrayList<SuitSets>();
		ArrayList<SuitSets> doubles = new ArrayList<SuitSets>();

		public Result(ArrayList<Mahjong> mahjong) {
			this.mahjong = mahjong;
		}

		public String format(Mahjong mahjong) {
			return Tools.mahjong(mahjong.color, mahjong.size);
		}

		public Result(Result result) {
			for (int i = 0; i < result.mahjong.size(); i++) {
				this.mahjong.add(result.mahjong.get(i));
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

				MahjongVO mahjongVO = new MahjongVO();
				mahjongVO.cardType = 4;

				for (int j = 0; j < this.straights.get(i).mahjong.size(); j++) {
					mahjongVO.current.add(this.straights.get(i).mahjong.get(j));
				}
				if (this.straights.get(i).lacks.size() > 0) {
					for (int j = 0; j < this.straights.get(i).lacks.size(); j++) {
						mahjongVO.lacks.add(this.straights.get(i).lacks.get(j));
						res.lacks.add(this.straights.get(i).lacks.get(j));
					}
				}

				resultVO.mahjongs.add(mahjongVO);

			}
			for (int i = 0; i < this.triples.size(); i++) {

				MahjongVO mahjongVO = new MahjongVO();
				mahjongVO.cardType = 2;

				for (int j = 0; j < this.triples.get(i).mahjong.size(); j++) {
					mahjongVO.current.add(this.triples.get(i).mahjong.get(j));
				}
				if (this.triples.get(i).lacks.size() > 0) {

					for (int j = 0; j < this.triples.get(i).lacks.size(); j++) {
						mahjongVO.lacks.add(this.triples.get(i).lacks.get(j));
						res.lacks.add(this.triples.get(i).lacks.get(j));
					}
				}

				resultVO.mahjongs.add(mahjongVO);

			}
			for (int i = 0; i < this.doubles.size(); i++) {

				MahjongVO mahjongVO = new MahjongVO();
				mahjongVO.cardType = 1;

				for (int j = 0; j < this.doubles.get(i).mahjong.size(); j++) {
					mahjongVO.current.add(this.doubles.get(i).mahjong.get(j));
				}
				if (this.doubles.get(i).lacks.size() > 0) {

					for (int j = 0; j < this.doubles.get(i).lacks.size(); j++) {
						mahjongVO.lacks.add(this.doubles.get(i).lacks.get(j));
						res.lacks.add(this.doubles.get(i).lacks.get(j));
					}
				}
				resultVO.mahjongs.add(mahjongVO);
			}

			res.result.add(resultVO);
		}

	}

	// class Mahjong {
	// public int index;
	//
	// public int color;
	//
	// public int size;
	//
	// public Mahjong(int index, int color, int size) {
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
