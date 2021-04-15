package com.oegame.tablegames.service.game.mahjong.model;

import java.util.ArrayList;
import java.util.List;

public class SettleVO {

	/**
	 * 是否胡牌
	 */
	public int code = 0;

	/**
	 * 大胡类型
	 */
	public List<Integer> extend = new ArrayList<Integer>();

	/**
	 * 听牌数量
	 */
	public List<Poker> lacks = new ArrayList<Poker>();

	/**
	 * 胡牌类型
	 */
	public List<ResultVO> result = new ArrayList<ResultVO>();


}
