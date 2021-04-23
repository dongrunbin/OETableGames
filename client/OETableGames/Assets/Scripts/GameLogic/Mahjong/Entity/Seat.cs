//===================================================
//Author      : DRB
//CreateTime  ：2021/4/6 4:10:28
//Description ：
//===================================================
using System.Collections.Generic;

public class Seat
{
    /// <summary>
    /// 玩家编号
    /// </summary>
    public int PlayerId;
    /// <summary>
    /// 是否是玩家
    /// </summary>
    public bool IsPlayer;
    /// <summary>
    /// 位置
    /// </summary>
    public int Pos;
    /// <summary>
    /// 索引
    /// </summary>
    public int Index;
    /// <summary>
    /// 是否房主
    /// </summary>
    public bool isOwner;
    /// <summary>
    /// 是否庄家
    /// </summary>
    public bool IsBanker;
    /// <summary>
    /// 是否赢家
    /// </summary>
    public bool isWiner;
    /// <summary>
    /// 是否输家
    /// </summary>
    public bool isLoser;

    public bool isZiMo;
    /// <summary>
    /// 玩家金币
    /// </summary>
    public int Gold;
    /// <summary>
    /// 初始金币
    /// </summary>
    public int InitGold;
    /// <summary>
    /// 当局得的分
    /// </summary>
    public int Settle;
    /// <summary>
    /// 玩家昵称
    /// </summary>
    public string Nickname;
    /// <summary>
    /// 性别
    /// </summary>
    public int Gender;
    /// <summary>
    /// 头像
    /// </summary>
    public string Avatar;
    /// <summary>
    /// 状态
    /// </summary>
    public SeatStatus Status;
    /// <summary>
    /// 解散房间状态
    /// </summary>
    public DisbandStatus DisbandState = DisbandStatus.Wait;
    /// <summary>
    /// 摸的牌
    /// </summary>
    public Mahjong HitMahjong;
    /// <summary>
    /// 剩余牌的数量
    /// </summary>
    public int MahjongAmount;
    /// <summary>
    /// 出牌次数
    /// </summary>
    public int playedTimes;
    /// <summary>
    /// 已打出的牌
    /// </summary>
    public List<Mahjong> DeskTopMahjong = new List<Mahjong>();
    /// <summary>
    /// 万能牌
    /// </summary>
    public List<Mahjong> UniversalList = new List<Mahjong>();
    /// <summary>
    /// 手牌
    /// </summary>
    public List<Mahjong> MahjongList = new List<Mahjong>();
    /// <summary>
    /// 吃碰杠的牌
    /// </summary>
    public List<MahjongGroup> UsedMahjongGroups = new List<MahjongGroup>();

    public string incomesDesc = "";

    public List<Mahjong> TingList = new List<Mahjong>();

    public Dictionary<Mahjong, List<Mahjong>> TingDic = new Dictionary<Mahjong, List<Mahjong>>();


    /// <summary>
    /// 初始化
    /// </summary>
    /// <param name="seat"></param>
    public virtual void Init(Seat seat)
    {
        if (seat == null) return;
        PlayerId = seat.PlayerId;
        Gold = seat.Gold;
        InitGold = seat.InitGold;
        Avatar = seat.Avatar;
        Gender = seat.Gender;
        Nickname = seat.Nickname;
    }


    /// <summary>
    /// 清空
    /// </summary>
    public virtual void Clear()
    {
        PlayerId = 0;
        Nickname = string.Empty;
        Status = SeatStatus.Idle;
        Gold = 0;
        InitGold = 0;
        Avatar = string.Empty;
        Gender = 0;
    }


    /// <summary>
    /// 重置
    /// </summary>
    public virtual void Reset()
    {
        IsBanker = false;
        isLoser = false;
        isWiner = false;
        isZiMo = false;
        Settle = 0;
        Status = SeatStatus.Idle;
        UniversalList.Clear();
        DeskTopMahjong.Clear();
        MahjongList.Clear();
        UsedMahjongGroups.Clear();
        playedTimes = 0;
        MahjongAmount = 0;
        HitMahjong = null;
        Status = SeatStatus.Idle;
    }
}
