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
/// S2C_AskDisband
/// </summary>
public class Game_S2C_AskDisbandProto : IProto
{
    public int Code { get { return 20009; } }

    public int playerId; //
    public byte disbandStatus; //
    public long disbandTimestamp; //
    public long disbandMaxTime; //

    public Game_S2C_AskDisbandProto()
    {
    }

    public Game_S2C_AskDisbandProto(byte[] bytes)
    {
        Deserialize(bytes);
    }

    public byte[] Serialize()
    {
        using (MemoryStream ms = new MemoryStream())
        {
            ms.WriteInt(Code);
            ms.WriteInt(playerId);
            ms.WriteByte(disbandStatus);
            ms.WriteLong(disbandTimestamp);
            ms.WriteLong(disbandMaxTime);
            return ms.ToArray();
        }
    }

    public void Deserialize(byte[] buffer)
    {
        if (buffer == null) return;
        using (MemoryStream ms = new MemoryStream(buffer))
        {
            playerId = ms.ReadInt();
            disbandStatus = (byte)ms.ReadByte();
            disbandTimestamp = ms.ReadLong();
            disbandMaxTime = ms.ReadLong();
        }
    }
}
