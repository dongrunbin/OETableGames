//===================================================
//Author      : DRB
//CreateTime  ：2021/3/6 22:23:35
//Description ：
//===================================================

using DrbFramework;
using DrbFramework.Internal;
using DrbFramework.Network;
using System;
using System.IO;
using System.Threading;

public class NetworkHandler : INetworkHandler
{
    public void OnChannelSent(INetworkChannel channel, int sentLength)
    {
        Log.Info("received:" + sentLength + " bytes");
    }

    public void OnChannelReceived(INetworkChannel channel, object obj)
    {
        byte[] buffer = (byte[])obj;
        Log.Info("received:" + buffer.Length + "bytes");

        byte[] protocodeBuffer = new byte[4];
        byte[] protoContent = new byte[buffer.Length - 4];
        using (MemoryStream ms = new MemoryStream(buffer))
        {
            ms.Read(protocodeBuffer, 0, protocodeBuffer.Length);
            ms.Read(protoContent, 0, protoContent.Length);
            Array.Reverse(protocodeBuffer);
            int protoCode = BitConverter.ToInt32(protocodeBuffer, 0);
            if (protoCode != CodeDef.System_S2C_HeartBeatProto)
            {
                Log.Info(string.Format("=================received:{0},{1},{2}", protoCode, CodeDef.GetEn(protoCode), CodeDef.GetCn(protoCode)));
            }
            if (protoCode == CodeDef.System_S2C_DisconnectProto)
            {
                Log.Info("Server actively disconnected");
                channel.Close();
            }
            else
            {
                DrbComponent.GetEventSystem<int>().Dispatch(this, protoCode, new NetworkEventArgs(protoCode, protoContent));
            }
        }
    }

    public void OnClosed(INetworkChannel channel)
    {
        Log.Info(channel.Name + "Disconnected");

        DrbComponent.GetEventSystem<int>().Dispatch(this, CodeDef.System_C2S_HeartBeatProto - 2, new NetworkEventArgs(CodeDef.System_C2S_HeartBeatProto - 2, null));
    }

    public void OnConnected(INetworkChannel channel)
    {
        Log.Info(channel.Name + "Connected");

        DrbComponent.GetEventSystem<int>().Dispatch(this, CodeDef.System_C2S_HeartBeatProto - 1, new NetworkEventArgs(CodeDef.System_C2S_HeartBeatProto - 1, null));
    }

    public void OnExceptionCaught(INetworkChannel channel, Exception exception)
    {
        Log.Warn("channel name:" + channel.Name + "   exception:" + exception.Message);
    }
}
