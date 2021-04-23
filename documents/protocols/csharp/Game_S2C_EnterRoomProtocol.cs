//===================================================
//作    者：DRB
//创建时间：2021-04-23 08:04:28
//备    注：this code is generated by the tool
//===================================================
using System.Collections;
using System.Collections.Generic;
using System;
using System.IO;
using DrbFramework.Internal.Network;
using DrbFramework.Extensions;

/// <summary>
/// S2C_EnterRoom
/// </summary>
public class Game_S2C_EnterRoomProto : IProto
{
    public int Code { get { return 20003; } }

    public int playerId; //
    public byte gender; //
    public string nickname = ""; //
    public string avatar = ""; //
    public int gold; //
    public int pos; //
    public long online; //
    public int originalGold; //

    public Game_S2C_EnterRoomProto()
    {
    }

    public Game_S2C_EnterRoomProto(byte[] bytes)
    {
        Deserialize(bytes);
    }

    public byte[] Serialize()
    {
        using (MemoryStream ms = new MemoryStream())
        {
            ms.WriteInt(Code);
            ms.WriteInt(playerId);
            ms.WriteByte(gender);
            ms.WriteUTF8String(nickname);
            ms.WriteUTF8String(avatar);
            ms.WriteInt(gold);
            ms.WriteInt(pos);
            ms.WriteLong(online);
            ms.WriteInt(originalGold);
            return ms.ToArray();
        }
    }

    public void Deserialize(byte[] buffer)
    {
        if (buffer == null) return;
        using (MemoryStream ms = new MemoryStream(buffer))
        {
            playerId = ms.ReadInt();
            gender = (byte)ms.ReadByte();
            nickname = ms.ReadUTF8String();
            avatar = ms.ReadUTF8String();
            gold = ms.ReadInt();
            pos = ms.ReadInt();
            online = ms.ReadLong();
            originalGold = ms.ReadInt();
        }
    }
}
