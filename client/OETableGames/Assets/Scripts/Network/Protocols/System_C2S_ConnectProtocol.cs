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
/// C2S_Connect
/// </summary>
public class System_C2S_ConnectProto : IProto
{
    public int Code { get { return 10003; } }

    public int passportId; //Passport Id
    public string token = ""; //Token

    public System_C2S_ConnectProto()
    {
    }

    public System_C2S_ConnectProto(byte[] bytes)
    {
        Deserialize(bytes);
    }

    public byte[] Serialize()
    {
        using (MemoryStream ms = new MemoryStream())
        {
            ms.WriteInt(Code);
            ms.WriteInt(passportId);
            ms.WriteUTF8String(token);
            return ms.ToArray();
        }
    }

    public void Deserialize(byte[] buffer)
    {
        if (buffer == null) return;
        using (MemoryStream ms = new MemoryStream(buffer))
        {
            passportId = ms.ReadInt();
            token = ms.ReadUTF8String();
        }
    }
}
