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
/// S2C_Settle
/// </summary>
public class Mahjong_S2C_SettleProto : IProto
{
    public int Code { get { return 30008; } }

    public bool isOver; //
    public List<Seat> seatsList; //

    /// <summary>
    /// 
    /// </summary>
    public class Seat
    {
        public byte status; //
        public bool isLoser; //
        public bool isWinner; //
        public bool isReadyHand; //
        public int playerId; //
        public int pos; //
        public int gold; //
        public int settle; //
        public Poker universalPoker; //
        public Poker hitPoker; //
        public List<Poker> pokersList; //
        public List<Poker> desktopPokersList; //
        public string incomesDesc = ""; //
        public List<PokerGroup> usedPokerGroupList; //
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
                ms.WriteBool(isLoser);
                ms.WriteBool(isWinner);
                ms.WriteBool(isReadyHand);
                ms.WriteInt(playerId);
                ms.WriteInt(pos);
                ms.WriteInt(gold);
                ms.WriteInt(settle);
                if(universalPoker != null)
                {
                    ms.WriteBytes(universalPoker.Serialize());
                }
                else
                {
                    ms.WriteInt(0);
                }
                if(hitPoker != null)
                {
                    ms.WriteBytes(hitPoker.Serialize());
                }
                else
                {
                    ms.WriteInt(0);
                }
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
                ms.WriteUShort((ushort)(desktopPokersList == null?0:desktopPokersList.Count));
                if(desktopPokersList != null)
                {
                    for (int i = 0; i < desktopPokersList.Count; ++i)
                    {
                        if(desktopPokersList[i] != null)
                        {
                            ms.WriteBytes(desktopPokersList[i].Serialize());
                        }
                        else
                        {
                            ms.WriteInt(0);
                        }
                    }
                }
                ms.WriteUTF8String(incomesDesc);
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
                return ms.ToArray();
            }
        }

        public void Deserialize(byte[] buffer)
        {
            if (buffer == null) return;
            using (MemoryStream ms = new MemoryStream(buffer))
            {
                status = (byte)ms.ReadByte();
                isLoser = ms.ReadBool();
                isWinner = ms.ReadBool();
                isReadyHand = ms.ReadBool();
                playerId = ms.ReadInt();
                pos = ms.ReadInt();
                gold = ms.ReadInt();
                settle = ms.ReadInt();
                universalPoker = new Poker(ms.ReadBytes());
                hitPoker = new Poker(ms.ReadBytes());
                ushort pokersListLength = ms.ReadUShort();
                pokersList = new List<Poker>();
                for (int i = 0; i < pokersListLength; ++i)
                {
                    pokersList.Add(new Poker(ms.ReadBytes()));
                }
                ushort desktopPokersListLength = ms.ReadUShort();
                desktopPokersList = new List<Poker>();
                for (int i = 0; i < desktopPokersListLength; ++i)
                {
                    desktopPokersList.Add(new Poker(ms.ReadBytes()));
                }
                incomesDesc = ms.ReadUTF8String();
                ushort usedPokerGroupListLength = ms.ReadUShort();
                usedPokerGroupList = new List<PokerGroup>();
                for (int i = 0; i < usedPokerGroupListLength; ++i)
                {
                    usedPokerGroupList.Add(new PokerGroup(ms.ReadBytes()));
                }
            }
        }
    }

    /// <summary>
    /// 
    /// </summary>
    public class Poker
    {
        public int index; //
        public int color; //
        public int number; //
        public int pos; //
        public Poker()
        {
        }

        public Poker(byte[] bytes)
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
                ms.WriteInt(pos);
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
                pos = ms.ReadInt();
            }
        }
    }

    /// <summary>
    /// 
    /// </summary>
    public class PokerGroup
    {
        public byte typeId; //
        public byte subTypeId; //
        public List<Poker> pokersList; //
        public PokerGroup()
        {
        }

        public PokerGroup(byte[] bytes)
        {
            Deserialize(bytes);
        }

        public byte[] Serialize()
        {
            using (MemoryStream ms = new MemoryStream())
            {
                ms.WriteByte(typeId);
                ms.WriteByte(subTypeId);
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
                typeId = (byte)ms.ReadByte();
                subTypeId = (byte)ms.ReadByte();
                ushort pokersListLength = ms.ReadUShort();
                pokersList = new List<Poker>();
                for (int i = 0; i < pokersListLength; ++i)
                {
                    pokersList.Add(new Poker(ms.ReadBytes()));
                }
            }
        }
    }

    public Mahjong_S2C_SettleProto()
    {
    }

    public Mahjong_S2C_SettleProto(byte[] bytes)
    {
        Deserialize(bytes);
    }

    public byte[] Serialize()
    {
        using (MemoryStream ms = new MemoryStream())
        {
            ms.WriteInt(Code);
            ms.WriteBool(isOver);
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
            isOver = ms.ReadBool();
            ushort seatsListLength = ms.ReadUShort();
            seatsList = new List<Seat>();
            for (int i = 0; i < seatsListLength; ++i)
            {
                seatsList.Add(new Seat(ms.ReadBytes()));
            }
        }
    }
}
