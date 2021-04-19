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
/// S2C_Disconnect
/// </summary>
public class System_S2C_DisconnectProto : IProto
{
    public int Code { get { return 10005; } }


    public System_S2C_DisconnectProto()
    {
    }

    public System_S2C_DisconnectProto(byte[] bytes)
    {
        Deserialize(bytes);
    }

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
