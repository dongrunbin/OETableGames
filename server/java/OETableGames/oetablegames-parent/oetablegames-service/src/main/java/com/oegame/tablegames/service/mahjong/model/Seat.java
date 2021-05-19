package com.oegame.tablegames.service.mahjong.model;

import java.util.ArrayList;
import java.util.HashMap;

import com.oegame.tablegames.service.game.SeatBase;

public class Seat extends SeatBase
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	// 杠记录
	public ArrayList<KongGroup> gangIncomes = new ArrayList<KongGroup>();
	
	//牌型分数
	public int dmScore = 0;

	//胡牌分数
	public int huScore = 0;
	
	//明杠分数
	public int mgScore = 0;
	
	//暗杠分数
	public int agScore = 0;
		
	//补杠分数
	public int bgScore = 0;

	// 当前座位的桌面牌面信息
	public ArrayList<Mahjong> desktop = new ArrayList<Mahjong>();

	// 当前座位的手里牌面信息
	public HashMap<Integer, Mahjong> mahjong = new HashMap<Integer, Mahjong>();

	// 已经使用的牌
	public ArrayList<MahjongGroup> useMahjongGroup = new ArrayList<MahjongGroup>();

	// 正在询问的牌
	public ArrayList<MahjongGroup> askMahjongGroup = new ArrayList<MahjongGroup>();

	// 自己的赖子
	public ArrayList<Mahjong> universal = new ArrayList<Mahjong>();

	//座位状态
	public MahjongSeatStatus gameStatus;

	// 刚摸的牌
	public Mahjong hitMahjong = null;
	
	// 是否可以摸牌
	public boolean perflop = false;
	
	// 刚才是否是杠
	public boolean isGang = false;
	
	// 收益描述
	public String incomesDesc = "";
	
	//胡牌类型
	public WinSubType type = WinSubType.HU_SUBTYPE_PING;
	
	
	/**
	 * 点炮
	 */
	public int paoCount;
	/**
	 * 自摸
	 */
	public int zimoCount;
	/**
	 * 明杠
	 */
	public int mgangCount;
	/**
	 * 暗杠
	 */
	public int agangCount;
	/**
	 * 补杠
	 */
	public int bgangCount;

	public Seat(int pos)
	{
		super(pos);
		init();
	}

	public void clear()
	{
		super.clear();

		this.paoCount = 0;
		this.zimoCount = 0;
		this.mgangCount = 0;
		this.agangCount = 0;
		this.bgangCount = 0;

		this.init();
	}

	public void init()
	{
		this.gameStatus = MahjongSeatStatus.SEAT_STATUS_IDLE;
		this.desktop.clear();
		this.mahjong.clear();
		this.useMahjongGroup.clear();
		this.askMahjongGroup.clear();
		this.hitMahjong = null;
		this.isBanker = false;
		this.perflop = false;
		this.isGang = false;
		this.winner = false;
		this.loser = false;
		this.settle = 0;
		this.universal.clear();
		this.incomesDesc = "";
		this.gangIncomes.clear();
		this.mgScore = 0;
		this.agScore = 0;
		this.bgScore = 0;
		this.huScore = 0;
		this.dmScore = 0;
		this.type = WinSubType.HU_SUBTYPE_PING;
	}

	public void settle(int gold) {
		this.settle += gold;
		this.gold += gold;
	}
	
	@Override
	public SeatBase clone()
	{
		Seat seat = new Seat(-1);
		seat.playerId = this.playerId;
		seat.player = this.player;
		seat.gold = this.gold;
		seat.initGold = this.initGold;
		seat.type = this.type;
		seat.isBanker = this.isBanker;
		seat.mahjong = this.mahjong;
		return seat;
	}

}
