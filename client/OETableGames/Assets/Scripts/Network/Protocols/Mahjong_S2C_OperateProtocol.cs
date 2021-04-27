//===================================================
//作    者：DRB
//创建时间：2021-04-27 01:22:52
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
    public List<Mahjong> mahjongsList; //

    /// <summary>
    /// 
    /// </summary>
    public class Mahjong
    {
        public int index; //
        public int color; //
        public int number; //
        public int pos; //
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
