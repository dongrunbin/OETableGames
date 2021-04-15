//===================================================
//Author      : DRB
//CreateTime  ：2021/4/6 4:13:36
//Description ：
//===================================================
using System;

public class Player : IComparable<Player>
{
    public int playerId;
    public string nickname;
    public string avatar;
    public int gender;
    public int gameId;
    public int roomId;
    public long online;

    public virtual int CompareTo(Player other)
    {
        return playerId - other.playerId;
    }
}
