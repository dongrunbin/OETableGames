package com.oegame.tablegames.service.game.mahjong.settle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Set;

import com.oegame.tablegames.service.game.mahjong.MahjongAlgorithm;
import com.oegame.tablegames.service.game.mahjong.model.GangSubType;
import com.oegame.tablegames.service.game.mahjong.model.CombinationType;
import com.oegame.tablegames.service.game.mahjong.model.Poker;
import com.oegame.tablegames.service.game.mahjong.model.PokerGroup;
import com.oegame.tablegames.service.game.mahjong.model.PokerVO;
import com.oegame.tablegames.service.game.mahjong.model.RoomSetting;
import com.oegame.tablegames.service.game.mahjong.model.SettleVO;
import com.oegame.tablegames.service.game.mahjong.model.Tools;

public class MahjongMethod {
	
	// 麻将的操作类
	protected MahjongAlgorithm mahjongTest = new MahjongAlgorithm();

	String isQm = "";// 断门
	String isDm = "";// 断一门,断两门,断三门,断四门
	String isBz = "";// 八张
	String isWh1 = "wh";// 无混,牌里没有一个癞子
	String isWh2 = "wh";// 无混,混归位,不算混也能胡
	String isGskh = "gskh";// 杠上开花/杠上炮
	String isDdh = "ddh";// 对对胡
	String isQxd = "qxd";// 七小对
	String isQys = "qys";// 清一色
	String isFys = "fys";// 风一色
	String isYtl = "ytl";// 一条龙
	String isZw = "zw";// 捉五
	String isMq = "mq";// 门清
	String isHd = "hdly/hdp";// 海底捞月/海底炮
	String isQyj = "qyj";// 全幺九
	String isJd = "jd";// 将对
	String isZz = "zz";// 中张
	String isDd = "dd";// 单调
	String isDl = "dl";// 地龙
	String isTh = "th";// 天胡
	String isDh = "dh";// 地胡

	// 麻将的操作类
	protected MahjongAlgorithm model = new MahjongAlgorithm();

	/**
	 * 是否是癞子
	 * 
	 * @param sprite
	 * @param poker
	 * @return
	 */
	public boolean isUniversal(ArrayList<Poker> sprite, Poker poker) {
		for (int i = 0; i < sprite.size(); i++) {
			if (poker.size == sprite.get(i).size && poker.color == sprite.get(i).color) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * 对对胡
	 * 
	 * @param res
	 * @param hand
	 * @param use
	 */
	public boolean isDdH(SettleVO res, HashMap<Integer, Poker> hand, ArrayList<PokerGroup> use) {

		boolean exist = true;
		
		if (res.result.size() <= 0) {
			return false;
		}

		// 判断use里是否全是碰杠
		for (int i = 0; i < use.size(); i++) {
			if (use.get(i).typeId == CombinationType.POKER_TYPE_PENG
					|| use.get(i).typeId == CombinationType.POKER_TYPE_GANG) {

			} else {
				System.out.println("use里面不只有碰杠 " + use.get(i).typeId);
				return false;
			}
		}

		for (int j = 0; j < res.result.size(); j++) {
			for (PokerVO item : res.result.get(j).pokers) {
				if (item.cardType == 1 || item.cardType == 2) {

				} else {
					System.out.println("不止是刻子和对子,不能对对胡 " + item.cardType);
					return false;
				}
			}
		}
		
		System.out.println("对对胡");	
		return exist;
	}

	public int isQxd(HashMap<Integer, Poker> hand,ArrayList<Poker> sprite,ArrayList<PokerGroup> use, Poker test,boolean isSelf) {

		if (hand.size() != 13) {
			return -1;
		}
		ArrayList<Poker> poker1 = new ArrayList<Poker>();

		int hun = 0;
		for (Entry<Integer, Poker> entry : hand.entrySet()) {
			if (this.isUniversal(sprite, entry.getValue())) {
				hun++;
			}else {
				poker1.add(entry.getValue());	
			}			
		}
		
		if (test != null) {
			if (this.isUniversal(sprite, test) && isSelf) {
				hun++;
			}else {
				poker1.add(test);
			}			
		}

		LinkedHashMap<Integer, Poker> values = this.array_count_values(poker1);

		int single = 0;
		int gang = 0;
		
		for (Entry<Integer, Poker> entry : values.entrySet()) {
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

	public boolean isQys(HashMap<Integer, Poker> hand, ArrayList<Poker> sprite,ArrayList<PokerGroup> use, Poker test,boolean isSelf) {

		ArrayList<Poker> poker1 = new ArrayList<Poker>();
		ArrayList<Integer> color = new ArrayList<Integer>();

		for (Entry<Integer, Poker> entry : hand.entrySet()) {
			poker1.add(entry.getValue());
		}

		if (test != null) {
			poker1.add(test);
		}

		for (int i = poker1.size() - 1; i >= 0; i--) {
			if (this.isUniversal(sprite, poker1.get(i))) {

			} else {
				if (poker1.get(i).color <= 3) {
					if (!color.contains(poker1.get(i).color)) {
						color.add(poker1.get(i).color);
					}
				} else {
					if (!color.contains(4)) {
						color.add(4);
					}
				}
			}
		}

		for (int i = 0; i < use.size(); i++) {
			for (int j = 0; j < use.get(i).poker.size(); j++) {
				if (use.get(i).poker.get(j).color <= 3) {
					if (!color.contains(use.get(i).poker.get(j).color)) {
						color.add(use.get(i).poker.get(j).color);
					}
				} else {
					if (!color.contains(4)) {
						color.add(4);
					}
				}
			}
		}

		if (color.size() == 1) {
			System.out.println("清一色");
			return true;
		}
		return false;
	}

	/**
	 * 一条龙
	 * 
	 * @param hand
	 * @param test
	 * @param sprite
	 */
	public boolean isYtl(HashMap<Integer, Poker> hand, Poker test, ArrayList<Poker> sprite,RoomSetting roomSetting,boolean isSelf) {

		boolean ytl = false;

		HashMap<Integer, Integer> wan = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> tong = new HashMap<Integer, Integer>();
		HashMap<Integer, Integer> tiao = new HashMap<Integer, Integer>();
		int hun = 0;

		ArrayList<Poker> poker = new ArrayList<Poker>();
		Set<Entry<Integer, Poker>> pset = hand.entrySet();
		for (Entry<Integer, Poker> entry : pset) {
			if (this.isUniversal(sprite, entry.getValue())) {
				hun++;
			}else {
				poker.add(entry.getValue());	
			}			
		}

		if (test != null) {
			if (this.isUniversal(sprite, test) && isSelf) {
				hun++;
			}else {
				poker.add(test);
			}						
		}

		for (int i = 0; i < poker.size(); i++) {
			if (poker.get(i).color == 1) {
				wan.put(poker.get(i).size, poker.get(i).index);
			} else if (poker.get(i).color == 2) {
				tong.put(poker.get(i).size, poker.get(i).index);
			} else if (poker.get(i).color == 3) {
				tiao.put(poker.get(i).size, poker.get(i).index);
			}
		}

		if (hun > 0) {
			System.out.println("癞子个数为" + hun);
		}

		if (wan.size() + hun >= 9 || tong.size() + hun >= 9 || tiao.size() + hun >= 9) {

			ArrayList<Integer> hunList = new ArrayList<Integer>();
			// 判断万字牌是否能一条龙
			if (wan.size() + hun >= 9) {
				System.out.println("检测万一条龙，但是看看一条龙以后是否还能胡牌");

				hunList.clear();
				int hunCount = 9 - wan.size();
				if (hunCount > 0) {
					System.out.println("所需癞子数" + hunCount);
					for (int i = 0; i < poker.size(); i++) {
						if (this.isUniversal(sprite, poker.get(i))) {
							hunList.add(poker.get(i).index);
							hunCount--;
							if (hunCount == 0) {
								break;
							}
						}
					}
				}

				System.out.println("\n万字牌一条龙了以后手牌：");
				HashMap<Integer, Poker> wanpoker = new HashMap<Integer, Poker>();
				for (int i = 0; i < poker.size(); i++) {
					if (!wan.containsValue(poker.get(i).index) && !hunList.contains(poker.get(i).index)) {
						System.out.print(Tools.mahjong(poker.get(i).color, poker.get(i).size) + ", ");
						wanpoker.put(poker.get(i).index, poker.get(i));
					}
				}
				System.out.println("\n-------------");

				SettleVO settle = this.checkYtl(wanpoker, null, sprite,roomSetting,isSelf);
				if (settle.code > 0) {
					ytl = true;
					System.out.println("胡万字一条龙");
				}
			}

			if (!ytl && tong.size() + hun >= 9) {
				// 判断筒字牌是否能一条龙
				System.out.println("检测筒一条龙，但是看看一条龙以后是否还能胡牌");

				hunList.clear();
				int hunCount = 9 - tong.size();
				if (hunCount > 0) {
					System.out.println("所需癞子数" + hunCount);
					for (int i = 0; i < poker.size(); i++) {
						if (this.isUniversal(sprite, poker.get(i))) {
							hunList.add(poker.get(i).index);
							hunCount--;
							if (hunCount == 0) {
								break;
							}
						}
					}
				}

				System.out.println("\n筒字牌一条龙了以后手牌：");
				HashMap<Integer, Poker> tongpoker = new HashMap<Integer, Poker>();
				for (int i = 0; i < poker.size(); i++) {
					if (!tong.containsValue(poker.get(i).index) && !hunList.contains(poker.get(i).index)) {
						System.out.print(Tools.mahjong(poker.get(i).color, poker.get(i).size) + ", ");
						tongpoker.put(poker.get(i).index, poker.get(i));
					}
				}
				System.out.println("\n-------------");

				SettleVO settle = this.checkYtl(tongpoker, null, sprite,roomSetting,isSelf);
				if (settle.code > 0) {
					ytl = true;
					System.out.println("胡筒字一条龙");
				}
			}

			if (!ytl && tiao.size() + hun >= 9) {
				// 判断条字牌是否能一条龙
				System.out.println("检测条一条龙，但是看看一条龙以后是否还能胡牌");

				hunList.clear();
				int hunCount = 9 - tiao.size();
				if (hunCount > 0) {
					System.out.println("所需癞子数" + hunCount);
					for (int i = 0; i < poker.size(); i++) {
						if (this.isUniversal(sprite, poker.get(i))) {
							hunList.add(poker.get(i).index);
							hunCount--;
							if (hunCount == 0) {
								break;
							}
						}
					}
				}

				System.out.println("\n条字牌一条龙了以后手牌：");
				HashMap<Integer, Poker> tiaopoker = new HashMap<Integer, Poker>();
				for (int i = 0; i < poker.size(); i++) {
					if (!tiao.containsValue(poker.get(i).index) && !hunList.contains(poker.get(i).index)) {
						System.out.print(Tools.mahjong(poker.get(i).color, poker.get(i).size) + ", ");
						tiaopoker.put(poker.get(i).index, poker.get(i));
					}
				}
				System.out.println("\n-------------");

				SettleVO settle = this.checkYtl(tiaopoker, null, sprite,roomSetting,isSelf);
				if (settle.code > 0) {
					ytl = true;
					System.out.println("胡条字一条龙");
				}
			}

			if (ytl) {
				System.out.println("一条龙");
			}
		}
		return ytl;
	}
	
	//检测一条龙之后是否能胡牌
	protected SettleVO checkYtl(HashMap<Integer, Poker> hand, Poker test, ArrayList<Poker> sprite,RoomSetting roomSetting,boolean isSelf) {
		SettleVO res = mahjongTest.checkHu(hand,sprite,isSelf, test, roomSetting,false);
		return res;
	}
	
	//检测无魂之后是否能胡牌
	protected SettleVO checkWuhun(HashMap<Integer, Poker> hand, Poker test, ArrayList<Poker> sprite,RoomSetting roomSetting,boolean isSelf) {
		SettleVO res = mahjongTest.checkHu(hand,sprite,isSelf, test, roomSetting,false);
		return res;
	}	

	public int sprite(HashMap<Integer, Poker> hand, ArrayList<Poker> sprite, Poker test) {
		int count = 0;
		for (Entry<Integer, Poker> entry : hand.entrySet()) {
			if (this.isUniversal(sprite, entry.getValue())) {
				count++;
			}
		}
		if (test != null) {
			if (this.isUniversal(sprite, test)) {
				count++;
			}
		}
		return count;
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

	/**
	 * 缺门
	 * 
	 * @param res
	 * @param hand
	 * @param test
	 * @param sprite
	 * @param use
	 * @return
	 */
	public boolean isQM(SettleVO res, HashMap<Integer, Poker> hand, Poker test, ArrayList<Poker> sprite,
			ArrayList<PokerGroup> use) {

		ArrayList<Integer> color = new ArrayList<Integer>();

		if (test != null) {
			if (this.isUniversal(sprite, test)) {

			} else {
				if (test.color <= 3) {
					color.add(test.color);
				} else {
					color.add(4);
				}
			}
		}

		for (Entry<Integer, Poker> entry : hand.entrySet()) {
			if (this.isUniversal(sprite, test)) {

			} else {
				if (entry.getValue().color <= 3) {
					color.add(entry.getValue().color);
				} else {
					color.add(4);
				}
			}
		}

		for (int i = 0; i < use.size(); i++) {
			for (int j = 0; j < use.get(i).poker.size(); j++) {
				if (use.get(i).poker.get(j).color <= 3) {
					color.add(use.get(i).poker.get(j).color);
				} else {
					color.add(4);
				}
			}
		}

		if (color.contains(1) && color.contains(2) && color.contains(3) && color.contains(4)) {
			return false;
		} else {
			return true;
		}
	}

	/**
	 * 地龙
	 * 
	 * @param res
	 * @param hand
	 * @param test
	 * @param sprite
	 * @param use
	 * @param isSelf
	 * @param isGang
	 * @param isDahu
	 * @param cardType
	 */
	public void isDl(SettleVO res, HashMap<Integer, Poker> hand, Poker test, ArrayList<Poker> sprite,
			ArrayList<PokerGroup> use, boolean isSelf, boolean isGang, boolean isDahu, ArrayList<String> cardType) {

		HashMap<Integer, Poker> poker = hand;
		for (int i = 0; i < use.size(); i++) {
			for (int j = 0; j < use.get(i).poker.size(); j++) {
				poker.put(use.get(i).poker.get(j).index, use.get(i).poker.get(j));
			}
		}
		
		int gang = this.isQxd(poker, sprite,use,test,isSelf);
		if (gang > 0) {
			if (gang == 3) {
				cardType.add("sandl");
				System.out.println("-------------------可以胡3地龙");
			} else if (gang == 2) {
				cardType.add("shuangdl");
				System.out.println("-------------------可以胡双地龙");
			} else if (gang == 1) {
				cardType.add("dl");
				System.out.println("-------------------可以胡地龙");
			}
		}
	}

	/**
	 * 无混,手牌里没有一个癞子
	 * 
	 * @param res
	 * @param hand
	 * @param test
	 * @param sprite
	 * @param use
	 * @param cardType
	 */
	public void isWh1(SettleVO res, HashMap<Integer, Poker> hand, Poker test, ArrayList<Poker> sprite,
			ArrayList<PokerGroup> use, ArrayList<String> cardType) {

		// 判断无混
		boolean hun = false;
		// 手牌
		for (Entry<Integer, Poker> entry : hand.entrySet()) {
			if (this.isUniversal(sprite, entry.getValue())) {
				hun = true;
				break;
			}
		}

		if (test != null && !hun) {
			if (this.isUniversal(sprite, test)) {
				hun = true;
			}
		}

		cardType.add("wh");
		System.out.println("可以无混儿1");
	}

	/**
	 * 杠上开花
	 * 
	 * @param isSelf
	 * @param isGang
	 * @param cardType
	 */
	public void isGskh(boolean isSelf, boolean isGang, ArrayList<String> cardType) {
		if (isGang) {

			if (isSelf) {
				cardType.add("gskh");
				System.out.println("杠上开花");
			} else {
				cardType.add("gsp");
				System.out.println("杠上炮");
			}
		}
	}

	/**
	 * 海底捞月
	 * 
	 * @param isSelf
	 * @param pokerTotal
	 * @param cardType
	 */
	public void isHd(boolean isSelf, int pokerTotal, ArrayList<String> cardType) {
		if (pokerTotal == 0) {
			if (isSelf) {
				cardType.add("hdly");
				System.out.println("海底捞月");
			} else {
				cardType.add("hdp");
				System.out.println("海底炮");
			}
		}
	}

	/**
	 * 门清
	 * 
	 * @param use
	 * @param cardType
	 */
	public void isMq(ArrayList<PokerGroup> use, ArrayList<String> cardType) {// 门清
		boolean exist = true;

		for (int i = 0; i < use.size(); i++) {
			if (use.get(i).typeId == CombinationType.POKER_TYPE_PENG) {
				System.out.println("有碰,不能门清");
				exist = false;
				break;
			} else if (use.get(i).typeId == CombinationType.POKER_TYPE_GANG) {
				if (use.get(i).subTypeId == GangSubType.POKER_SUBTYPE_GANG_AN.ordinal()) {

				} else {
					System.out.println("有明杠或补杠,不能门清");
					exist = false;
					break;
				}
			} else if (use.get(i).typeId == CombinationType.POKER_TYPE_CHI) {
				System.out.println("有吃,不能门清");
				exist = false;
				break;
			}
		}

		if (exist) {
			cardType.add("mq");
			System.out.println("门清");
		}
	}



}
