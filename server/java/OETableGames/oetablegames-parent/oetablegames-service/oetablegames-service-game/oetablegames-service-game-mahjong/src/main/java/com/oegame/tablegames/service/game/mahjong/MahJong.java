package com.oegame.tablegames.service.game.mahjong;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;
import com.oegame.tablegames.service.game.mahjong.model.CombinationType;
import com.oegame.tablegames.service.game.mahjong.model.Poker;
import com.oegame.tablegames.service.game.mahjong.model.PokerGroup;
import com.oegame.tablegames.service.game.mahjong.model.RoomSetting;
import com.oegame.tablegames.service.game.mahjong.model.Seat;
import com.oegame.tablegames.service.game.mahjong.model.SettleVO;
import com.oegame.tablegames.service.game.mahjong.model.Tools;

/**
 * 
 * @author 麻将的玩法,吃碰杠胡等等
 *
 */
public class MahJong 
{
	//private static final Logger logger = LoggerFactory.getLogger(MahJong.class);
	
	public MahjongAlgorithm algorithm = new MahjongAlgorithm();

	public static void main(String args[]) 
	{

		MahJong mahjong = new MahJong();

		ArrayList<Poker> poker = Tools.mahjong("5_万", "5_万", "5_万", "7_筒", "7_筒", "7_筒", "3_筒", "4_条", "5_条","8_条", "9_条", "0_红中", "0_红中");

		HashMap<Integer, Poker> hand = new HashMap<Integer, Poker>();
		for (int i = 0; i < poker.size(); i++) {
			hand.put(poker.get(i).index, poker.get(i));
		}

		ArrayList<Poker> sprite = new ArrayList<Poker>();
		sprite.add(Tools.mahjong("0_红中").get(0));

		Poker test = Tools.mahjong("3_筒").get(0);

		ArrayList<PokerGroup> usePokerGroup = new ArrayList<PokerGroup>();

		RoomSetting roomSetting = null;

		Seat seat = new Seat(1);
		seat.universal = sprite;
		seat.poker = hand;
		seat.usePokerGroup = usePokerGroup;

		SettleVO settle = mahjong.checkHu(seat, test,false, roomSetting,false);
		if (settle != null && settle.code > 0) {
			System.out.println("胡了");
		}

	}

	public SettleVO checkHu(Seat seat, Poker test,boolean isSelf, RoomSetting roomSetting,boolean isTing) {
		
		SettleVO res = algorithm.checkHu(seat.poker,seat.universal,isSelf, test, roomSetting,isTing);
		return res;
	}

	public boolean isUniversal(ArrayList<Poker> sprite, Poker poker) {
		for (int i = 0; i < sprite.size(); i++) {
			if (poker.size == sprite.get(i).size && poker.color == sprite.get(i).color) {
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
	 * @return ArrayList<Poker[]>
	 */
	public ArrayList<Poker[]> checkChi(HashMap<Integer, Poker> hand, ArrayList<Poker> sprite, Poker test) {

		ArrayList<Poker[]> list = new ArrayList<Poker[]>();

		if (test.color > 3) {
			return list;
		}

		HashMap<Integer, Poker> group = new HashMap<Integer, Poker>();

		Set<Entry<Integer, Poker>> pset = hand.entrySet();
		for (Entry<Integer, Poker> entry : pset) {
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
				list.add(new Poker[] { group.get(k1), group.get(k2) });
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
	public boolean checkPeng(HashMap<Integer, Poker> hand, Poker test) {

		ArrayList<Poker> poker = new ArrayList<Poker>();

		Set<Entry<Integer, Poker>> pset = hand.entrySet();
		for (Entry<Integer, Poker> entry : pset) {
			poker.add(entry.getValue());
		}

		LinkedHashMap<Integer, Poker> res = this.array_count_values(poker);

		//Util.println(" test checkPeng: " + Tools.mahjong(test.color, test.size));

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
	 * @param usePokerGroup
	 * @param test
	 * @param checkHand
	 * @return int
	 */
	public int checkBuGang(HashMap<Integer, Poker> hand, ArrayList<PokerGroup> usePokerGroup, Poker test, boolean checkHand) {
		if (test == null) {
			return -1;
		}

		for (int i = 0; i < usePokerGroup.size(); i++) {
			if (usePokerGroup.get(i).typeId == CombinationType.POKER_TYPE_PENG) {
				Poker pk = usePokerGroup.get(i).poker.get(0);
				if (pk.color == test.color && pk.size == test.size) {
					return i;
				}
				if (checkHand) {
					Set<Entry<Integer, Poker>> pset = hand.entrySet();
					for (Entry<Integer, Poker> entry : pset) {
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
	public boolean checkGang(HashMap<Integer, Poker> hand, Poker test, boolean self) {
		ArrayList<Poker> poker = new ArrayList<Poker>();

		Set<Entry<Integer, Poker>> pset = hand.entrySet();
		for (Entry<Integer, Poker> entry : pset) {
			poker.add(entry.getValue());
		}

		if (test != null) {
			poker.add(test);
		}

		LinkedHashMap<Integer, Poker> res = this.array_count_values(poker);

		Set<Entry<Integer, Poker>> set = res.entrySet();
		for (Entry<Integer, Poker> entry : set) {
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
	
	public String format(Poker poker) {
		return Tools.mahjong(poker.color, poker.size);
	}

//	public void debug(List<ResultVO> result) {
//		if (result.size() > 0) {
//			logger.debug("牌型数量：" + result.size());
//
//			for (int i = 0; i < result.size(); i++) {
//
//				logger.debug("听牌：" + result.get(i).lacks.size());
//
//				for (int j = 0; j < result.get(i).lacks.size(); j++) {
//					logger.debug(Tools.mahjong(result.get(i).lacks.get(j)[0], result.get(i).lacks.get(j)[1]));
//				}
//				logger.debug("");
//
//				for (int j = 0; j < result.get(i).pokers.size(); j++) {
//					//Util.println(result.get(i).pokers.get(j).cardType);
//					logger.debug(" - ");
//					for (int x = 0; x < result.get(i).pokers.get(j).lacks.size(); x++) {
//						logger.debug(Tools.mahjong(result.get(i).pokers.get(j).lacks.get(x)[0], result.get(i).pokers.get(j).lacks.get(x)[1]));
//					}
//					logger.debug(" - ");
//					for (int x = 0; x < result.get(i).pokers.get(j).current.size(); x++) {
//						logger.debug(Tools.mahjong(result.get(i).pokers.get(j).current.get(x)[0], result.get(i).pokers.get(j).current.get(x)[1]));
//					}
//					logger.debug("");
//				}
//				logger.debug(" ----------- ");
//			}
//		}
//	}

}

