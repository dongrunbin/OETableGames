//===================================================
//Author      : DRB
//CreateTime  ：2021/3/3 22:19:25
//Description ：
//===================================================

using DrbFramework;
using DrbFramework.Event;
using DrbFramework.Internal;
using DrbFramework.Procedure;
using DrbFramework.Timer;
using DrbFramework.UI;
using System;
using UnityEngine;

public class LoginProcedure : Procedure
{
    private const int HAND_SHAKE_TIME_OUT = 5000;
    private const float SEND_HEART_BEAT_SPACE = 10f;
    private const float HEART_BEAT_OVER_TIME = 30f;
    private long m_SendHandShakeClientTime;
    private float m_PrevReceiveHeartTime = 0.0f;

    private LoginForm m_LoginForm;

    public override void OnEnter(object userData)
    {
        base.OnEnter(userData);

        DrbComponent.UISystem.OpenFormAsync("UI/Forms/LoginForm", "BackGround", OnLoadUIComplete);
        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.System_C2S_HeartBeatProto - 1, OnConnected);
        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.System_C2S_HeartBeatProto - 2, OnDisconnected);
        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.System_S2C_ConnectProto, OnHandShaked);
        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.System_S2C_HeartBeatProto, OnHearBeat);
    }

    public override void OnLeave()
    {
        base.OnLeave();

        DrbComponent.UISystem.DestroyForm(m_LoginForm);
        DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.System_C2S_HeartBeatProto - 1, OnConnected);
        DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.System_C2S_HeartBeatProto - 2, OnDisconnected);
        DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.System_S2C_ConnectProto, OnHandShaked);
    }

    private void OnLoadUIComplete(IUIForm form)
    {
        m_LoginForm = (LoginForm)form;
    }

    private void OnConnected(object sender, EventArgs<int> args)
    {
        AccountEntity entity = DrbComponent.SettingSystem.GetObject<AccountEntity>("AccountInfo");
        ClientSendHandShake(entity.passportId, entity.token);
    }

    private void OnDisconnected(object sender, EventArgs<int> args)
    {
        DrbComponent.UISystem.ShowMessage("Error", "Network was disconnected", okAction: DrbComponent.NetworkSystem.Reconnect);
    }

    public override void OnUpdate(float elapseSeconds, float realElapseSeconds)
    {
        base.OnUpdate(elapseSeconds, realElapseSeconds);

        //hand shake time out
        if (m_SendHandShakeClientTime > 0 && TimeUtil.GetTimestampMS() - m_SendHandShakeClientTime > HAND_SHAKE_TIME_OUT)
        {
            Log.Info("hand shake time out");
            DrbComponent.NetworkSystem.Close();
        }
    }

    private void ClientSendHandShake(int passportId, string token)
    {
        System_C2S_ConnectProto proto = new System_C2S_ConnectProto();
        proto.passportId = passportId;
        proto.token = token;
        m_SendHandShakeClientTime = TimeUtil.GetTimestampMS();
        DrbComponent.NetworkSystem.Send(proto.Serialize());
    }

    private void OnHandShaked(object sender, EventArgs<int> args)
    {
        System_S2C_ConnectProto proto = new System_S2C_ConnectProto(((NetworkEventArgs)args).Data);
        long serverTime = proto.timestamp;
        int handShakePing = (int)((TimeUtil.GetTimestampMS() - m_SendHandShakeClientTime) / 2);
        Log.Info("fps=" + handShakePing + "ms");
        long ServerCurrentTime = proto.timestamp + handShakePing;
        long timeDifference = TimeUtil.GetTimestampMS() - ServerCurrentTime;
        DrbComponent.SettingSystem.SetLong("TimeDistance", timeDifference);
        Log.Info("TimeDistance between client and server : " + timeDifference + "ms");
        m_PrevReceiveHeartTime = Time.realtimeSinceStartup;

        DrbComponent.TimerSystem.RegisterTimer(0.0f, SEND_HEART_BEAT_SPACE, 0, null, CheckHeartBeat, null);


        ChangeState<MainMenuProcedure>();
    }

    private void OnHearBeat(object sender, EventArgs<int> args)
    {
        System_S2C_HeartBeatProto proto = new System_S2C_HeartBeatProto(((NetworkEventArgs)args).Data);

        long sendTime = proto.clientTimestamp;
        long serverTime = proto.serverTimestamp;
        m_PrevReceiveHeartTime = Time.realtimeSinceStartup;
        long localTime = TimeUtil.GetTimestampMS();
        long fps = (localTime - sendTime) / 2;
        serverTime = serverTime + fps;
        DrbComponent.SettingSystem.SetLong("TimeDistance", localTime - serverTime);
    }

    private void CheckHeartBeat(Timer timer)
    {
        if (Time.realtimeSinceStartup - m_PrevReceiveHeartTime > HEART_BEAT_OVER_TIME)
        {
            Debug.LogWarning("Heart beat time out");
            DrbComponent.NetworkSystem.Close();
        }

        ClientSendHeart();
    }

    private void ClientSendHeart()
    {
        System_C2S_HeartBeatProto proto = new System_C2S_HeartBeatProto();
        proto.clientTimestamp = TimeUtil.GetTimestampMS();
        DrbComponent.NetworkSystem.Send(proto.Serialize());
    }
}
