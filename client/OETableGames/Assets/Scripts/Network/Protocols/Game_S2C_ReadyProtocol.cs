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
/// S2C_Ready
/// </summary>
public class Game_S2C_ReadyProto : IProto
{
    public int Code { get { return 20007; } }

    public int playerId; //

    public Game_S2C_ReadyProto()
    {
    }

    public Game_S2C_ReadyProto(byte[] bytes)
    {
        Deserialize(bytes);
    }

    public byte[] Serialize()
    {
        using (MemoryStream ms = new MemoryStream())
        {
            ms.WriteInt(Code);
            ms.WriteInt(playerId);
            return ms.ToArray();
        }
    }

    public void Deserialize(byte[] buffer)
    {
        if (buffer == null) return;
        using (MemoryStream ms = new MemoryStream(buffer))
        {
            playerId = ms.ReadInt();
        }
    }
}
