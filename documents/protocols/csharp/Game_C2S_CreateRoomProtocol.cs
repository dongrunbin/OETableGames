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
/// C2S_CreateRoom
/// </summary>
public class Game_C2S_CreateRoomProto : IProto
{
    public int Code { get { return 20001; } }

    public List<int> settingIdsList; //setting ids
    public int gameId; //game id

    public Game_C2S_CreateRoomProto()
    {
    }

    public Game_C2S_CreateRoomProto(byte[] bytes)
    {
        Deserialize(bytes);
    }

    public byte[] Serialize()
    {
        using (MemoryStream ms = new MemoryStream())
        {
            ms.WriteInt(Code);
            ms.WriteUShort((ushort)(settingIdsList == null?0:settingIdsList.Count));
            if(settingIdsList != null)
            {
                for (int i = 0; i < settingIdsList.Count; ++i)
                {
                    ms.WriteInt(settingIdsList[i]);
                }
            }
            ms.WriteInt(gameId);
            return ms.ToArray();
        }
    }

    public void Deserialize(byte[] buffer)
    {
        if (buffer == null) return;
        using (MemoryStream ms = new MemoryStream(buffer))
        {
            ushort settingIdsListLength = ms.ReadUShort();
            settingIdsList = new List<int>();
            for (int i = 0; i < settingIdsListLength; ++i)
            {
                settingIdsList.Add(ms.ReadInt());
            }
            gameId = ms.ReadInt();
        }
    }
}