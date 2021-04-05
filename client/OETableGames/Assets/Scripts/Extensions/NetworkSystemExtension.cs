//===================================================
//Author      : DRB
//CreateTime  ：2021/4/5 21:39:43
//Description ：
//===================================================
using DrbFramework.Network;
using System.Net;

public static class NetworkSystemExtension
{
    private const string SERVER_CHANNEL = "MainServer";

    public static void Connect(this NetworkSystem system, string ip, int port)
    {
        INetworkChannel channel = system.CreateChannel(SERVER_CHANNEL, new NetworkHandler(), new NetworkEncoder(), new NetworkDecoder());
        channel.Connect(IPAddress.Parse(ip), port);
    }

    public static void Reconnect(this NetworkSystem system)
    {
        INetworkChannel channel = system.GetChannel(SERVER_CHANNEL);
        channel.Connect(channel.RemoteIpAddress, channel.RemotePort);
    }

    public static void Send(this NetworkSystem system, object data)
    {
        system.GetChannel(SERVER_CHANNEL).Send(data);
    }

    public static void Close(this NetworkSystem system)
    {
        system.GetChannel(SERVER_CHANNEL).Close();
    }
}
