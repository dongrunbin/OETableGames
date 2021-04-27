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
/// S2C_HeartBeat
/// </summary>
public class System_S2C_HeartBeatProto : IProto
{
    public int Code { get { return 10004; } }

    public long serverTimestamp; //Server timestamp
    public long clientTimestamp; //Client timestamp

    public System_S2C_HeartBeatProto()
    {
    }

    public System_S2C_HeartBeatProto(byte[] bytes)
    {
        Deserialize(bytes);
    }

    public byte[] Serialize()
    {
        using (MemoryStream ms = new MemoryStream())
        {
            ms.WriteInt(Code);
            ms.WriteLong(serverTimestamp);
            ms.WriteLong(clientTimestamp);
            return ms.ToArray();
        }
    }

    public void Deserialize(byte[] buffer)
    {
        if (buffer == null) return;
        using (MemoryStream ms = new MemoryStream(buffer))
        {
            serverTimestamp = ms.ReadLong();
            clientTimestamp = ms.ReadLong();
        }
    }
}
