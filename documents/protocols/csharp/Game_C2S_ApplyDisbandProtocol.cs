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
/// C2S_ApplyDisband
/// </summary>
public class Game_C2S_ApplyDisbandProto : IProto
{
    public int Code { get { return 20008; } }

    public byte disbandStatus; //

    public Game_C2S_ApplyDisbandProto()
    {
    }

    public Game_C2S_ApplyDisbandProto(byte[] bytes)
    {
        Deserialize(bytes);
    }

    public byte[] Serialize()
    {
        using (MemoryStream ms = new MemoryStream())
        {
            ms.WriteInt(Code);
            ms.WriteByte(disbandStatus);
            return ms.ToArray();
        }
    }

    public void Deserialize(byte[] buffer)
    {
        if (buffer == null) return;
        using (MemoryStream ms = new MemoryStream(buffer))
        {
            disbandStatus = (byte)ms.ReadByte();
        }
    }
}
