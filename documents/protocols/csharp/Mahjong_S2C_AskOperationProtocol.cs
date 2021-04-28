//===================================================
//作    者：DRB
//创建时间：2021-04-28 02:06:09
//备    注：this code is generated by the tool
//===================================================
using System.Collections;
using System.Collections.Generic;
using System;
using System.IO;
using DrbFramework.Internal.Network;
using DrbFramework.Extensions;

/// <summary>
/// S2C_AskOperation
/// </summary>
public class Mahjong_S2C_AskOperationProto : IProto
{
    public int Code { get { return 30011; } }

    public List<MahjongGroup> askMahjongGroupsList; //
    public long countdown; //

    /// <summary>
    /// 
    /// </summary>
    public class MahjongGroup
    {
        public byte typeId; //
        public byte subTypeId; //
        public int playerId; //
        public List<Mahjong> mahjongsList; //
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
                ms.WriteByte(typeId);
                ms.WriteByte(subTypeId);
                ms.WriteInt(playerId);
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
                ushort mahjongsListLength = ms.ReadUShort();
                mahjongsList = new List<Mahjong>();
                for (int i = 0; i < mahjongsListLength; ++i)
                {
                    byte[] mahjongsBytes = ms.ReadBytes();
                    if(mahjongsBytes != null) mahjongsList.Add(new Mahjong(mahjongsBytes));
                }
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

    public Mahjong_S2C_AskOperationProto()
    {
    }

    public Mahjong_S2C_AskOperationProto(byte[] bytes)
    {
        Deserialize(bytes);
    }

    public byte[] Serialize()
    {
        using (MemoryStream ms = new MemoryStream())
        {
            ms.WriteInt(Code);
            ms.WriteUShort((ushort)(askMahjongGroupsList == null?0:askMahjongGroupsList.Count));
            if(askMahjongGroupsList != null)
            {
                for (int i = 0; i < askMahjongGroupsList.Count; ++i)
                {
                    if(askMahjongGroupsList[i] != null)
                    {
                        ms.WriteBytes(askMahjongGroupsList[i].Serialize());
                    }
                    else
                    {
                        ms.WriteInt(0);
                    }
                }
            }
            ms.WriteLong(countdown);
            return ms.ToArray();
        }
    }

    public void Deserialize(byte[] buffer)
    {
        if (buffer == null) return;
        using (MemoryStream ms = new MemoryStream(buffer))
        {
            ushort askMahjongGroupsListLength = ms.ReadUShort();
            askMahjongGroupsList = new List<MahjongGroup>();
            for (int i = 0; i < askMahjongGroupsListLength; ++i)
            {
                byte[] askMahjongGroupsBytes = ms.ReadBytes();
                if(askMahjongGroupsBytes != null) askMahjongGroupsList.Add(new MahjongGroup(askMahjongGroupsBytes));
            }
            countdown = ms.ReadLong();
        }
    }
}
