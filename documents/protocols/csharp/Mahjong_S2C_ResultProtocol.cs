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
/// S2C_Result
/// </summary>
public class Mahjong_S2C_ResultProto : IProto
{
    public int Code { get { return 30009; } }

    public List<Seat> seatsList; //

    /// <summary>
    /// 
    /// </summary>
    public class Seat
    {
        public int pos; //
        public int playerId; //
        public int gold; //
        public bool isWinner; //
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
                ms.WriteInt(pos);
                ms.WriteInt(playerId);
                ms.WriteInt(gold);
                ms.WriteBool(isWinner);
                return ms.ToArray();
            }
        }

        public void Deserialize(byte[] buffer)
        {
            if (buffer == null) return;
            using (MemoryStream ms = new MemoryStream(buffer))
            {
                pos = ms.ReadInt();
                playerId = ms.ReadInt();
                gold = ms.ReadInt();
                isWinner = ms.ReadBool();
            }
        }
    }

    public Mahjong_S2C_ResultProto()
    {
    }

    public Mahjong_S2C_ResultProto(byte[] bytes)
    {
        Deserialize(bytes);
    }

    public byte[] Serialize()
    {
        using (MemoryStream ms = new MemoryStream())
        {
            ms.WriteInt(Code);
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
            ushort seatsListLength = ms.ReadUShort();
            seatsList = new List<Seat>();
            for (int i = 0; i < seatsListLength; ++i)
            {
                seatsList.Add(new Seat(ms.ReadBytes()));
            }
        }
    }
}
