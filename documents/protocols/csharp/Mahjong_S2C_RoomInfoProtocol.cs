//===================================================
//作    者：DRB
//创建时间：2021-04-15 03:55:33
//备    注：this code is generated by the tool
//===================================================
using System.Collections;
using System.Collections.Generic;
using System;
using System.IO;
using DrbFramework.Internal.Network;
using DrbFramework.Extensions;

/// <summary>
/// S2C_RoomInfo
/// </summary>
public class Mahjong_S2C_RoomInfoProto : IProto
{
    public int Code { get { return 30010; } }

    public byte roomStatus; //
    public int roomId; //
    public List<int> settingIdsList; //
    public int pokerAmount; //
    public int loop; //
    public int maxLoop; //
    public int pokerTotal; //
    public int baseScore; //
    public int diceFirst; //
    public int diceSecond; //
    public int diceFirstA; //
    public int diceFirstB; //
    public int diceSecondA; //
    public int diceSecondB; //
    public long dismissMaxTime; //
    public long dismissTime; //
    public List<MahjongGroup> askPokerGroupsList; //
    public List<Seat> seatsList; //

    /// <summary>
    /// 
    /// </summary>
    public class MahjongGroup
    {
        public int typeId; //
        public int subTypeId; //
        public int playerId; //
        public List<Mahjong> pokersList; //
        public MahjongGroup()
        {
        }

        public MahjongGroup(byte[] bytes)
        {
            Deserialize(bytes);
        }

        public byte[] Serialize()
        {
            using (MemoryStream ms = new MemoryStream())
            {
                ms.WriteInt(typeId);
                ms.WriteInt(subTypeId);
                ms.WriteInt(playerId);
                ms.WriteUShort((ushort)(pokersList == null?0:pokersList.Count));
                if(pokersList != null)
                {
                    for (int i = 0; i < pokersList.Count; ++i)
                    {
                        if(pokersList[i] != null)
                        {
                            ms.WriteBytes(pokersList[i].Serialize());
                        }
                        else
                        {
                            ms.WriteInt(0);
                        }
                    }
                }
                return ms.ToArray();
            }
        }

        public void Deserialize(byte[] buffer)
        {
            if (buffer == null) return;
            using (MemoryStream ms = new MemoryStream(buffer))
            {
                typeId = ms.ReadInt();
                subTypeId = ms.ReadInt();
                playerId = ms.ReadInt();
                ushort pokersListLength = ms.ReadUShort();
                pokersList = new List<Mahjong>();
                for (int i = 0; i < pokersListLength; ++i)
                {
                    pokersList.Add(new Mahjong(ms.ReadBytes()));
                }
            }
        }
    }

    /// <summary>
    /// 
    /// </summary>
    public class Seat
    {
        public byte status; //
        public byte gender; //
        public byte disbandtatus; //
        public bool isBanker; //
        public int playerId; //
        public int pos; //
        public int handCount; //
        public int pokerAmount; //
        public int gold; //
        public Mahjong hitPoker; //
        public List<Mahjong> desktopList; //
        public List<Mahjong> mahjongsList; //
        public List<Mahjong> universalList; //
        public List<MahjongGroup> usedPokerGroupList; //
        public string nickname = ""; //
        public string avatar = ""; //
        public Seat()
        {
        }

        public Seat(byte[] bytes)
        {
            Deserialize(bytes);
        }

        public byte[] Serialize()
        {
            using (MemoryStream ms = new MemoryStream())
            {
                ms.WriteByte(status);
                ms.WriteByte(gender);
                ms.WriteByte(disbandtatus);
                ms.WriteBool(isBanker);
                ms.WriteInt(playerId);
                ms.WriteInt(pos);
                ms.WriteInt(handCount);
                ms.WriteInt(pokerAmount);
                ms.WriteInt(gold);
                if(hitPoker != null)
                {
                    ms.WriteBytes(hitPoker.Serialize());
                }
                else
                {
                    ms.WriteInt(0);
                }
                ms.WriteUShort((ushort)(desktopList == null?0:desktopList.Count));
                if(desktopList != null)
                {
                    for (int i = 0; i < desktopList.Count; ++i)
                    {
                        if(desktopList[i] != null)
                        {
                            ms.WriteBytes(desktopList[i].Serialize());
                        }
                        else
                        {
                            ms.WriteInt(0);
                        }
                    }
                }
                ms.WriteUShort((ushort)(mahjongsList == null?0:mahjongsList.Count));
                if(mahjongsList != null)
                {
                    for (int i = 0; i < mahjongsList.Count; ++i)
                    {
                        if(mahjongsList[i] != null)
                        {
                            ms.WriteBytes(mahjongsList[i].Serialize());
                        }
                        else
                        {
                            ms.WriteInt(0);
                        }
                    }
                }
                ms.WriteUShort((ushort)(universalList == null?0:universalList.Count));
                if(universalList != null)
                {
                    for (int i = 0; i < universalList.Count; ++i)
                    {
                        if(universalList[i] != null)
                        {
                            ms.WriteBytes(universalList[i].Serialize());
                        }
                        else
                        {
                            ms.WriteInt(0);
                        }
                    }
                }
                ms.WriteUShort((ushort)(usedPokerGroupList == null?0:usedPokerGroupList.Count));
                if(usedPokerGroupList != null)
                {
                    for (int i = 0; i < usedPokerGroupList.Count; ++i)
                    {
                        if(usedPokerGroupList[i] != null)
                        {
                            ms.WriteBytes(usedPokerGroupList[i].Serialize());
                        }
                        else
                        {
                            ms.WriteInt(0);
                        }
                    }
                }
                ms.WriteUTF8String(nickname);
                ms.WriteUTF8String(avatar);
                return ms.ToArray();
            }
        }

        public void Deserialize(byte[] buffer)
        {
            if (buffer == null) return;
            using (MemoryStream ms = new MemoryStream(buffer))
            {
                status = (byte)ms.ReadByte();
                gender = (byte)ms.ReadByte();
                disbandtatus = (byte)ms.ReadByte();
                isBanker = ms.ReadBool();
                playerId = ms.ReadInt();
                pos = ms.ReadInt();
                handCount = ms.ReadInt();
                pokerAmount = ms.ReadInt();
                gold = ms.ReadInt();
                hitPoker = new Mahjong(ms.ReadBytes());
                ushort desktopListLength = ms.ReadUShort();
                desktopList = new List<Mahjong>();
                for (int i = 0; i < desktopListLength; ++i)
                {
                    desktopList.Add(new Mahjong(ms.ReadBytes()));
                }
                ushort mahjongsListLength = ms.ReadUShort();
                mahjongsList = new List<Mahjong>();
                for (int i = 0; i < mahjongsListLength; ++i)
                {
                    mahjongsList.Add(new Mahjong(ms.ReadBytes()));
                }
                ushort universalListLength = ms.ReadUShort();
                universalList = new List<Mahjong>();
                for (int i = 0; i < universalListLength; ++i)
                {
                    universalList.Add(new Mahjong(ms.ReadBytes()));
                }
                ushort usedPokerGroupListLength = ms.ReadUShort();
                usedPokerGroupList = new List<MahjongGroup>();
                for (int i = 0; i < usedPokerGroupListLength; ++i)
                {
                    usedPokerGroupList.Add(new MahjongGroup(ms.ReadBytes()));
                }
                nickname = ms.ReadUTF8String();
                avatar = ms.ReadUTF8String();
            }
        }
    }

    /// <summary>
    /// 
    /// </summary>
    public class Mahjong
    {
        public int index; //
        public int color; //
        public int number; //
        public Mahjong()
        {
        }

        public Mahjong(byte[] bytes)
        {
            Deserialize(bytes);
        }

        public byte[] Serialize()
        {
            using (MemoryStream ms = new MemoryStream())
            {
                ms.WriteInt(index);
                ms.WriteInt(color);
                ms.WriteInt(number);
                return ms.ToArray();
            }
        }

        public void Deserialize(byte[] buffer)
        {
            if (buffer == null) return;
            using (MemoryStream ms = new MemoryStream(buffer))
            {
                index = ms.ReadInt();
                color = ms.ReadInt();
                number = ms.ReadInt();
            }
        }
    }

    public Mahjong_S2C_RoomInfoProto()
    {
    }

    public Mahjong_S2C_RoomInfoProto(byte[] bytes)
    {
        Deserialize(bytes);
    }

    public byte[] Serialize()
    {
        using (MemoryStream ms = new MemoryStream())
        {
            ms.WriteInt(Code);
            ms.WriteByte(roomStatus);
            ms.WriteInt(roomId);
            ms.WriteUShort((ushort)(settingIdsList == null?0:settingIdsList.Count));
            if(settingIdsList != null)
            {
                for (int i = 0; i < settingIdsList.Count; ++i)
                {
                    ms.WriteInt(settingIdsList[i]);
                }
            }
            ms.WriteInt(pokerAmount);
            ms.WriteInt(loop);
            ms.WriteInt(maxLoop);
            ms.WriteInt(pokerTotal);
            ms.WriteInt(baseScore);
            ms.WriteInt(diceFirst);
            ms.WriteInt(diceSecond);
            ms.WriteInt(diceFirstA);
            ms.WriteInt(diceFirstB);
            ms.WriteInt(diceSecondA);
            ms.WriteInt(diceSecondB);
            ms.WriteLong(dismissMaxTime);
            ms.WriteLong(dismissTime);
            ms.WriteUShort((ushort)(askPokerGroupsList == null?0:askPokerGroupsList.Count));
            if(askPokerGroupsList != null)
            {
                for (int i = 0; i < askPokerGroupsList.Count; ++i)
                {
                    if(askPokerGroupsList[i] != null)
                    {
                        ms.WriteBytes(askPokerGroupsList[i].Serialize());
                    }
                    else
                    {
                        ms.WriteInt(0);
                    }
                }
            }
            ms.WriteUShort((ushort)(seatsList == null?0:seatsList.Count));
            if(seatsList != null)
            {
                for (int i = 0; i < seatsList.Count; ++i)
                {
                    if(seatsList[i] != null)
                    {
                        ms.WriteBytes(seatsList[i].Serialize());
                    }
                    else
                    {
                        ms.WriteInt(0);
                    }
                }
            }
            return ms.ToArray();
        }
    }

    public void Deserialize(byte[] buffer)
    {
        if (buffer == null) return;
        using (MemoryStream ms = new MemoryStream(buffer))
        {
            roomStatus = (byte)ms.ReadByte();
            roomId = ms.ReadInt();
            ushort settingIdsListLength = ms.ReadUShort();
            settingIdsList = new List<int>();
            for (int i = 0; i < settingIdsListLength; ++i)
            {
                settingIdsList.Add(ms.ReadInt());
            }
            pokerAmount = ms.ReadInt();
            loop = ms.ReadInt();
            maxLoop = ms.ReadInt();
            pokerTotal = ms.ReadInt();
            baseScore = ms.ReadInt();
            diceFirst = ms.ReadInt();
            diceSecond = ms.ReadInt();
            diceFirstA = ms.ReadInt();
            diceFirstB = ms.ReadInt();
            diceSecondA = ms.ReadInt();
            diceSecondB = ms.ReadInt();
            dismissMaxTime = ms.ReadLong();
            dismissTime = ms.ReadLong();
            ushort askPokerGroupsListLength = ms.ReadUShort();
            askPokerGroupsList = new List<MahjongGroup>();
            for (int i = 0; i < askPokerGroupsListLength; ++i)
            {
                askPokerGroupsList.Add(new MahjongGroup(ms.ReadBytes()));
            }
            ushort seatsListLength = ms.ReadUShort();
            seatsList = new List<Seat>();
            for (int i = 0; i < seatsListLength; ++i)
            {
                seatsList.Add(new Seat(ms.ReadBytes()));
            }
        }
    }
}
