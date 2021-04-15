//===================================================
//Author      : DRB
//CreateTime  ：2021/4/6 4:31:31
//Description ：
//===================================================
using System.Collections.Generic;
using UnityEngine;

public class MahjongGroup
{
    public OperationType CombinationType;
    public int SubType;
    public List<Mahjong> MahjongList;
    public int SeatPos;

    public MahjongGroup(OperationType typeId, int subType, List<Mahjong> lst, int seatPos)
    {
        CombinationType = typeId;
        SubType = subType;
        MahjongList = lst;
        SeatPos = seatPos;
        if (MahjongList == null)
        {
            MahjongList = new List<Mahjong>();
        }
    }

    public void Sort()
    {
        if (MahjongList == null) return;

        if (CombinationType != OperationType.Gang)
        {
            MahjongHelper.SimpleSort(MahjongList);
        }
    }
}
