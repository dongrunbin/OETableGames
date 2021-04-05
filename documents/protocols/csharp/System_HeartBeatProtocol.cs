//===================================================
//作    者：DRB
//创建时间：2021-03-07 00:08:53
//备    注：this code is generated by the tool
//===================================================
using System.Collections;
using System.Collections.Generic;
using System;
using System.IO;
using DrbFramework.Internal.Network;
using DrbFramework.Extensions;

/// <summary>
/// HeartBeat
/// </summary>
public class System_HeartBeatProto : IProto
{
    public int Code { get { return 10001; } }


    public byte[] Serialize()
    {
        using (MemoryStream ms = new MemoryStream())
        {
            ms.WriteInt(Code);
            return ms.ToArray();
        }
    }

    public void Deserialize(byte[] buffer)
    {
        if (buffer == null) return;
        using (MemoryStream ms = new MemoryStream(buffer))
        {
        }
    }
}