//===================================================
//Author      : DRB
//CreateTime  ：2021/4/5 21:39:43
//Description ：
//===================================================
using DrbFramework.Internal.Network;
using DrbFramework.Network;
using System.Net;

public static class NetworkSystemExtension
{
    private const string SERVER_CHANNEL = "MainServer";

    public static void Connect(this NetworkSystem system, string ip, int port)
    {
        INetworkChannel channel;
        if (!system.HasChannel(SERVER_CHANNEL))
        {
            channel = system.CreateChannel(SERVER_CHANNEL, new NetworkHandler(), new NetworkEncoder(), new NetworkDecoder());
        }
        else
        {
            channel = system.GetChannel(SERVER_CHANNEL);
        }

        channel.Connect(IPAddress.Parse(ip), port);
    }

    public static void Send(this NetworkSystem system, IProto proto)
    {
        system.GetChannel(SERVER_CHANNEL).Send(proto.Serialize());
    }

    public static void Close(this NetworkSystem system)
    {
        system.GetChannel(SERVER_CHANNEL).Close();
    }
}
