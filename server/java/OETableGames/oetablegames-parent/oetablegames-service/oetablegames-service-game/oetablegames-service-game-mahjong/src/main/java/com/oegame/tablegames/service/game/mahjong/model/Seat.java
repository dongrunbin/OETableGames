package com.oegame.tablegames.service.game.mahjong.model;

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
	public ArrayList<GangGroup> gangIncomes = new ArrayList<GangGroup>();
	
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

	// 当前座位的第几手
	public int handCount = 0;

	// 当前座位的桌面牌面信息
	public ArrayList<Poker> desktop = new ArrayList<Poker>();

	// 当前座位的手里牌面信息
	public HashMap<Integer, Poker> poker = new HashMap<Integer, Poker>();

	// 已经使用的牌
	public ArrayList<PokerGroup> usePokerGroup = new ArrayList<PokerGroup>();

	// 正在询问的牌
	public ArrayList<PokerGroup> askPokerGroup = new ArrayList<PokerGroup>();

	// 过圈胡的记录
	public HashMap<Integer, Poker> lockPoker = new HashMap<Integer, Poker>();
	
	// 过圈胡的记录
	public ArrayList<Poker> pengPoker = new ArrayList<Poker>();

	// 自己的赖子
	public ArrayList<Poker> universal = new ArrayList<Poker>();

	//座位状态
	public MahjongSeatStatus gameStatus;

	// 刚摸的牌
	public Poker hitPoker = null;
	
	// 是否可以摸牌
	public boolean perflop = false;
	
	// 刚才是否是杠
	public boolean isGang = false;
	
	// 收益描述
	public String incomesDesc = "";
	
	//胡牌类型
	public HuSubType type = HuSubType.HU_SUBTYPE_PING;
	
	
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
		this.handCount = 0;
		this.desktop.clear();
		this.poker.clear();
		this.usePokerGroup.clear();
		this.askPokerGroup.clear();
		this.hitPoker = null;
		this.isBanker = false;
		this.perflop = false;
		this.isGang = false;
		this.winner = false;
		this.loser = false;
		this.settle = 0;
		this.universal.clear();
		this.lockPoker.clear();
		this.pengPoker.clear();
		this.incomesDesc = "";
		this.gangIncomes.clear();
		this.mgScore = 0;
		this.agScore = 0;
		this.bgScore = 0;
		this.huScore = 0;
		this.dmScore = 0;
		this.type = HuSubType.HU_SUBTYPE_PING;
		this.isLeave = false;

	}

	/**
	 * 得分
	 * 
	 * @param gold
	 */
	public void settle(int gold) {
		this.settle += gold;
		this.gold += gold;
	}

	public void writeLogs(String msg)
	{
//		Util.logs("logs/" + this.player.roomId + ".log", msg);
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
		seat.poker = this.poker;
		return seat;
	}

}
