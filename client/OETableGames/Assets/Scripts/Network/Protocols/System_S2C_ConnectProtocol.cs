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
/// S2C_Connect
/// </summary>
public class System_S2C_ConnectProto : IProto
{
    public int Code { get { return 10006; } }

    public long timestamp; //timestamp

    public System_S2C_ConnectProto()
    {
    }

    public System_S2C_ConnectProto(byte[] bytes)
    {
        Deserialize(bytes);
    }

    public byte[] Serialize()
    {
        using (MemoryStream ms = new MemoryStream())
        {
            ms.WriteInt(Code);
            ms.WriteLong(timestamp);
            return ms.ToArray();
        }
    }

    public void Deserialize(byte[] buffer)
    {
        if (buffer == null) return;
        using (MemoryStream ms = new MemoryStream(buffer))
        {
            timestamp = ms.ReadLong();
        }
    }
}