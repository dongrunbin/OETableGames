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
/// S2C_Error
/// </summary>
public class System_S2C_ErrorProto : IProto
{
    public int Code { get { return 10007; } }

    public int code; //
    public string message = ""; //

    public System_S2C_ErrorProto()
    {
    }

    public System_S2C_ErrorProto(byte[] bytes)
    {
        Deserialize(bytes);
    }

    public byte[] Serialize()
    {
        using (MemoryStream ms = new MemoryStream())
        {
            ms.WriteInt(Code);
            ms.WriteInt(code);
            ms.WriteUTF8String(message);
            return ms.ToArray();
        }
    }

    public void Deserialize(byte[] buffer)
    {
        if (buffer == null) return;
        using (MemoryStream ms = new MemoryStream(buffer))
        {
            code = ms.ReadInt();
            message = ms.ReadUTF8String();
        }
    }
}