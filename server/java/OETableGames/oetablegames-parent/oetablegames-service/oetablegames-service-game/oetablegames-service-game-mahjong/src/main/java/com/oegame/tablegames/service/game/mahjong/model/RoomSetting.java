package com.oegame.tablegames.service.game.mahjong.model;

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
	public int pokerAmount = 13;

	//是否可以吃
	public boolean isChi = false;
	
	//可以可以碰
	public boolean isPeng = false;
	
	//是否可以杠
	public boolean isGang = false;
	
	//是否可以点炮
	public boolean isPihu = false;
	
	//是否可以胡七小对
	public boolean isQxd = false;

	//中
	public boolean isZhong = true;

	//一炮多响
	public boolean isManyhu = false;

	//是否抢杠
	public boolean isLoot = false;
}

