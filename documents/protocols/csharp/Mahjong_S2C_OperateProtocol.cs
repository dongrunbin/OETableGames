//===================================================
//作    者：DRB
//创建时间：2021-04-05 20:52:24
//备    注：this code is generated by the tool
//===================================================
using System.Collections;
using System.Collections.Generic;
using System;
using System.IO;
using DrbFramework.Internal.Network;
using DrbFramework.Extensions;

/// <summary>
/// S2C_Operate
/// </summary>
public class Mahjong_S2C_OperateProto : IProto
{
    public int Code { get { return 30007; } }

    public byte typeId; //
    public byte subTypeId; //
    public int playerId; //
    public List<Poker> pokersList; //

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

    public Mahjong_S2C_OperateProto()
    {
    }

    public Mahjong_S2C_OperateProto(byte[] bytes)
    {
        Deserialize(bytes);
    }

    public byte[] Serialize()
    {
        using (MemoryStream ms = new MemoryStream())
        {
            ms.WriteInt(Code);
            ms.WriteByte(typeId);
            ms.WriteByte(subTypeId);
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
            typeId = (byte)ms.ReadByte();
            subTypeId = (byte)ms.ReadByte();
            playerId = ms.ReadInt();
            ushort pokersListLength = ms.ReadUShort();
            pokersList = new List<Poker>();
            for (int i = 0; i < pokersListLength; ++i)
            {
                pokersList.Add(new Poker(ms.ReadBytes()));
            }
        }
    }
}