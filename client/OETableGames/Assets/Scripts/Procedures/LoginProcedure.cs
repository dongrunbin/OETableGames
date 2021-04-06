//===================================================
//Author      : DRB
//CreateTime  ：2021/3/3 22:19:25
//Description ：
//===================================================

using DrbFramework;
using DrbFramework.Event;
using DrbFramework.Extensions;
using DrbFramework.Http;
using DrbFramework.Internal;
using DrbFramework.Procedure;
using DrbFramework.Timer;
using DrbFramework.UI;
using System;
using System.Collections.Generic;
using System.Text;
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

        AccountEntity entity = DrbComponent.SettingSystem.GetObject<AccountEntity>("AccountInfo");
        DrbComponent.UISystem.OpenFormAsync("UI/Forms/LoginForm", "BackGround", (IUIForm form)=>{
            m_LoginForm = (LoginForm)form;
            m_LoginForm.OnGuestLoginClick = GuestLogin;
        });
        if (entity != null)
        {
            AccountLogin(entity.passportId, entity.token);
        }
        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.System_C2S_HeartBeatProto - 1, OnConnected);
        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.System_C2S_HeartBeatProto - 2, OnDisconnected);
        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.System_S2C_ConnectProto, OnHandShaked);
        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.System_S2C_HeartBeatProto, OnHearBeat);
    }

    public override void OnLeave()
    {
        base.OnLeave();
        if (m_LoginForm != null)
        {
            DrbComponent.UISystem.DestroyForm(m_LoginForm);
        }
        DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.System_C2S_HeartBeatProto - 1, OnConnected);
        DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.System_C2S_HeartBeatProto - 2, OnDisconnected);
        DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.System_S2C_ConnectProto, OnHandShaked);
    }

    private void GuestLogin()
    {
        Dictionary<string, object> dic = new Dictionary<string, object>();
        DrbComponent.HttpSystem.EncryptedRequest(ConstDefine.WebUrl, "passport/guest", dic, OnGuestLoginCallBack);
    }

    private void OnGuestLoginCallBack(object sender, HttpRequestCompleteEventArgs args)
    {
        if (args.HasError)
        {
            DrbComponent.UISystem.ShowMessage("错误", "网络连接失败");
        }
        else
        {
            LitJson.JsonData jsonData = LitJson.JsonMapper.ToObject(Encoding.UTF8.GetString(args.Data));
            if (jsonData["code"].ToString().ToInt() < 0)
            {
                DrbComponent.UISystem.ShowMessage("错误", jsonData["msg"].ToString());
                return;
            }

            int passportId = jsonData["data"]["passportId"].ToString().ToInt();
            string token = jsonData["data"]["token"].ToString();
            AccountLogin(passportId, token);
        }
    }

    private void AccountLogin(int passportId, string token)
    {
        Dictionary<string, object> dic = new Dictionary<string, object>();
        dic["passportId"] = passportId;
        dic["token"] = token;
        dic["device"] = DeviceUtil.GetPlatform();
        DrbComponent.HttpSystem.EncryptedRequest(ConstDefine.WebUrl, "passport/relogin", dic, OnLoginCallBack);
    }

    private void OnLoginCallBack(object sender, HttpRequestCompleteEventArgs args)
    {
        if (args.HasError)
        {
            DrbComponent.UISystem.ShowMessage("错误", "网络连接失败");
        }
        else
        {
            LitJson.JsonData jsonData = LitJson.JsonMapper.ToObject(Encoding.UTF8.GetString(args.Data));
            Log.Info(jsonData.ToJson());
            if (jsonData["code"].ToString().ToInt() < 0)
            {
                DrbComponent.UISystem.ShowMessage("Error", jsonData["msg"].ToString());
                ;
                if (DrbComponent.SettingSystem.HasSetting("AccountInfo"))
                {
                    DrbComponent.SettingSystem.DeleteSetting("AccountInfo");
                }
                return;
            }

            AccountEntity entity = LitJson.JsonMapper.ToObject<AccountEntity>(LitJson.JsonMapper.ToJson(jsonData["data"]));

            DrbComponent.SettingSystem.SetObject("AccountInfo", entity);
            RequestServer();
        }
    }

    private void RequestServer()
    {
        AccountEntity entity = DrbComponent.SettingSystem.GetObject<AccountEntity>("AccountInfo");
        Dictionary<string, object> dic = new Dictionary<string, object>();
        dic["passportId"] = entity.passportId;
        dic["token"] = entity.token;
        DrbComponent.HttpSystem.EncryptedRequest(ConstDefine.WebUrl, "passport/server", dic, RequestServerCallBack);
    }

    private void RequestServerCallBack(object sender, HttpRequestCompleteEventArgs args)
    {
        if (args.HasError)
        {
            DrbComponent.UISystem.ShowMessage("错误", "网络连接失败", type: MessageForm.MessageViewType.Ok, okAction: RequestServer);
        }
        else
        {
            LitJson.JsonData jsonData = LitJson.JsonMapper.ToObject(Encoding.UTF8.GetString(args.Data));
            if (jsonData["code"].ToString().ToInt() < 0)
            {
                DrbComponent.UISystem.ShowMessage("错误", jsonData["msg"].ToString());
                return;
            }
            string ip = jsonData["data"]["ip"].ToString();
            int port = jsonData["data"]["port"].ToString().ToInt();
            DrbComponent.SettingSystem.SetString("IP", ip);
            DrbComponent.SettingSystem.SetInt("Port", port);
            Log.Info(ip + ":" + port);

            Connect();
        }
    }

    private void OnConnected(object sender, EventArgs<int> args)
    {
        AccountEntity entity = DrbComponent.SettingSystem.GetObject<AccountEntity>("AccountInfo");
        ClientSendHandShake(entity.passportId, entity.token);
    }

    private void OnDisconnected(object sender, EventArgs<int> args)
    {
        DrbComponent.UISystem.ShowMessage("Error", "Network was disconnected", okAction: Connect);

    }

    private void Connect()
    {
        string ip = DrbComponent.SettingSystem.GetString("IP");
        int port = DrbComponent.SettingSystem.GetInt("Port");
        DrbComponent.NetworkSystem.Connect(ip, port);
    }

    public override void OnUpdate(float elapseSeconds, float realElapseSeconds)
    {
        base.OnUpdate(elapseSeconds, realElapseSeconds);

        //hand shake time out
        if (m_SendHandShakeClientTime > 0 && TimeUtil.GetTimestampMS() - m_SendHandShakeClientTime > HAND_SHAKE_TIME_OUT)
        {
            Log.Info("hand shake time out");
            m_SendHandShakeClientTime = 0;
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
