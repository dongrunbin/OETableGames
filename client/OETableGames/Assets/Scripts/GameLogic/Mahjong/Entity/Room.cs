//===================================================
//Author      : DRB
//CreateTime  ：2021/4/6 4:10:21
//Description ：
//===================================================

using System.Collections.Generic;

public class Room
{
    public int roomId;
    public int gameId;
    public string ownerName;
    public int ownerId;
    public List<int> Config = new List<int>();
    public int BaseScore;
    public RoomSetting Setting = new RoomSetting();
    public int currentLoop;
    public int maxLoop;
    public long DisbandTime;
    public int DisbandTimeMax;
    public bool isDisband;
    public bool isOver;
    public List<Player> players = new List<Player>();
    public RoomStatus RoomStatus;

    public List<Seat> SeatList = new List<Seat>();

    public RoomSetting setting = new RoomSetting();

    public int MahjongTotal;
    public int MahjongAmount;
    public Dice FirstDice;
    public Dice SecondDice;
    public int PlayedTimes;

    public int BankerPos
    {
        get
        {
            for (int i = 0; i < SeatList.Count; ++i)
            {
                if (SeatList[i].IsBanker)
                {
                    return SeatList[i].Pos;
                }
            }
            return 1;
        }
    }

    public List<MahjongGroup> AskMahjongGroup;
    public Dictionary<int, int> MahjongCount = new Dictionary<int, int>();

    public Room(int roomId, int gameId, List<int> config)
    {
        this.roomId = roomId;
        this.gameId = gameId;
        this.Config.AddRange(config);
        this.Setting.Init(config);
    }

    public Room()
    {

    }

    public Seat PlayerSeat
    {
        get
        {
            for (int i = 0; i < SeatList.Count; ++i)
            {

                if (SeatList[i].IsPlayer)
                {
                    return SeatList[i];
                }
            }
            if (SeatList.Count > 0) return SeatList[0];
            return null;
        }
    }
}
