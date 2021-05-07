package com.oegame.tablegames.service.mahjong.model;

import java.util.List;
import com.oegame.tablegames.service.game.RoomSettingBase;


public class RoomSetting extends RoomSettingBase
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public RoomSetting(List<Integer> rule)
	{
		super();
		
		this.init(rule);
	}

	//初试手牌数
	public int mahjongAmount = 13;

	//是否可以吃
	public boolean isChow = false;
	
	//可以可以碰
	public boolean isPong = false;
	
	//是否可以杠
	public boolean isKong = false;
	
	//是否可以点炮
	public boolean isWBD = false;
	
	//是否可以胡七小对
	public boolean isQxd = false;

	//中
	public boolean isZhong = true;

	//一炮多响
	public boolean isManyhu = false;

	//是否抢杠
	public boolean isLoot = false;
}

