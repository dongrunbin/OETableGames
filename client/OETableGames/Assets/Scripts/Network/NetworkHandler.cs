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
using UnityEngine;

public class NetworkHandler : INetworkHandler
{
    private const int HAND_SHAKE_TIME_OUT = 5000;
    private const float SEND_HEART_BEAT_SPACE = 10f;
    private const float HEART_BEAT_OVER_TIME = 30f;
    private long m_SendHandShakeClientTime;
    private float m_PrevReceiveHeartTime = 0.0f;

    private MemoryStream m_MemoryStream = new MemoryStream();

    public void OnChannelSent(INetworkChannel channel, int sentLength)
    {
        //Log.Info("sent:" + sentLength + " bytes");
    }

    public void OnChannelReceived(INetworkChannel channel, object obj)
    {
        byte[] buffer = (byte[])obj;
        //Log.Info("received:" + buffer.Length + "bytes");

        byte[] protocodeBuffer = new byte[4];
        byte[] protoContent = new byte[buffer.Length - 4];

        m_MemoryStream.Position = 0;
        m_MemoryStream.Write(buffer, 0, buffer.Length);
        m_MemoryStream.Position = 0;
        m_MemoryStream.Read(protocodeBuffer, 0, protocodeBuffer.Length);
        m_MemoryStream.Read(protoContent, 0, protoContent.Length);
        Array.Reverse(protocodeBuffer);
        int protoCode = BitConverter.ToInt32(protocodeBuffer, 0);
        if (protoCode != CodeDef.System_S2C_HeartBeatProto)
        {
            Log.Info(string.Format("=================received:{0},{1}", protoCode, CodeDef.GetEn(protoCode)));
        }
        if (protoCode == CodeDef.System_S2C_DisconnectProto)
        {
            Log.Info("Server actively disconnected");
            channel.Close();
        }
        else if (protoCode == CodeDef.System_S2C_ErrorProto)
        {
            System_S2C_ErrorProto proto = new System_S2C_ErrorProto(protoContent);
            DrbComponent.UISystem.ShowMessage("Error", proto.message);
        }
        else if (protoCode == CodeDef.System_S2C_ConnectProto)
        {
            System_S2C_ConnectProto proto = new System_S2C_ConnectProto(protoContent);
            long serverTime = proto.timestamp;
            int handShakePing = (int)((TimeUtil.GetTimestampMS() - m_SendHandShakeClientTime) / 2);
            Log.Info("fps=" + handShakePing + "ms");
            long ServerCurrentTime = proto.timestamp + handShakePing;
            long timeDifference = TimeUtil.GetTimestampMS() - ServerCurrentTime;
            DrbComponent.SettingSystem.SetLong("TimeDistance", timeDifference);
            Log.Info("TimeDistance between client and server : " + timeDifference + "ms");
            m_PrevReceiveHeartTime = Time.realtimeSinceStartup;

            DrbComponent.TimerSystem.RegisterTimer(0.0f, SEND_HEART_BEAT_SPACE, -1, null, CheckHeartBeat, null);
            DrbComponent.GetEventSystem<int>().Dispatch(this, protoCode, new NetworkEventArgs(protoCode, protoContent));
        }
        else if (protoCode == CodeDef.System_S2C_HeartBeatProto)
        {
            System_S2C_HeartBeatProto proto = new System_S2C_HeartBeatProto(protoContent);

            long sendTime = proto.clientTimestamp;
            long serverTime = proto.serverTimestamp;
            m_PrevReceiveHeartTime = Time.realtimeSinceStartup;
            long localTime = TimeUtil.GetTimestampMS();
            long fps = (localTime - sendTime) / 2;
            serverTime = serverTime + fps;
            DrbComponent.SettingSystem.SetLong("TimeDistance", localTime - serverTime);
        }
        else
        {
            DrbComponent.GetEventSystem<int>().Dispatch(this, protoCode, new NetworkEventArgs(protoCode, protoContent));
        }
    }

    public void OnClosed(INetworkChannel channel)
    {
        Log.Info(channel.Name + "Disconnected");
        DrbComponent.UISystem.ShowMessage("Error", "Network was disconnected", okAction: Connect);
    }

    public void OnConnected(INetworkChannel channel, bool isSuccess)
    {
        if (isSuccess)
        {
            Log.Info(channel.Name + "Connected");
            AccountEntity entity = DrbComponent.SettingSystem.GetObject<AccountEntity>("AccountInfo");
            ClientSendHandShake(entity.passportId, entity.token);
        }
        else
        {
            DrbComponent.UISystem.ShowMessage("Error", "Network connected fail", okAction: Connect);
        }
    }

    public void OnExceptionCaught(INetworkChannel channel, Exception exception)
    {
        //Debug.Log("channel name:" + channel.Name + "   exception:" + exception.Message);
    }

    private void Connect()
    {
        string ip = DrbComponent.SettingSystem.GetString("IP");
        int port = DrbComponent.SettingSystem.GetInt("Port");
        DrbComponent.NetworkSystem.Connect(ip, port);
    }

    private void ClientSendHandShake(int passportId, string token)
    {
        System_C2S_ConnectProto proto = new System_C2S_ConnectProto();
        proto.passportId = passportId;
        proto.token = token;
        m_SendHandShakeClientTime = TimeUtil.GetTimestampMS();
        DrbComponent.NetworkSystem.Send(proto);
    }

    private void CheckHeartBeat(DrbFramework.Timer.Timer timer)
    {
        if (Time.realtimeSinceStartup - m_PrevReceiveHeartTime > HEART_BEAT_OVER_TIME)
        {
            Debug.LogWarning("Heart beat time out");
            DrbComponent.NetworkSystem.Close();

            timer.Stop();
        }

        ClientSendHeart();
    }

    private void ClientSendHeart()
    {
        System_C2S_HeartBeatProto proto = new System_C2S_HeartBeatProto();
        proto.clientTimestamp = TimeUtil.GetTimestampMS();
        DrbComponent.NetworkSystem.Send(proto);
    }
}
