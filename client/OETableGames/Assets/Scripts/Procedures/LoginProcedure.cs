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
    private LoginForm m_LoginForm;

    private bool m_isBusy;

    public override void OnEnter(object userData)
    {
        base.OnEnter(userData);

        AccountEntity entity = DrbComponent.SettingSystem.GetObject<AccountEntity>("AccountInfo");
        if (entity != null)
        {
            AccountLogin(entity.passportId, entity.token);
        }
        DrbComponent.UISystem.OpenFormAsync("LoginForm", "BackGround", (IUIForm form) =>
        {
            m_LoginForm = (LoginForm)form;
            m_LoginForm.OnGuestLoginClick = GuestLogin;
        });
        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.System_S2C_ConnectProto, OnHandShaked);
    }

    public override void OnLeave()
    {
        base.OnLeave();
        if (m_LoginForm != null)
        {
            DrbComponent.UISystem.DestroyForm(m_LoginForm);
        }
        DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.System_S2C_ConnectProto, OnHandShaked);
    }

    private void GuestLogin()
    {
        if (m_isBusy) return;
        m_isBusy = true;
        Dictionary<string, object> dic = new Dictionary<string, object>();
        DrbComponent.HttpSystem.EncryptedRequest(ConstDefine.WebUrl, "passport/guest", dic, OnGuestLoginCallBack);
    }

    private void OnGuestLoginCallBack(object sender, HttpRequestCompleteEventArgs args)
    {
        m_isBusy = false;
        if (args.HasError)
        {
            DrbComponent.UISystem.ShowMessage("Error", "Connected fail");
        }
        else
        {
            LitJson.JsonData jsonData = LitJson.JsonMapper.ToObject(Encoding.UTF8.GetString(args.Data));
            if (jsonData["code"].ToString().ToInt() < 0)
            {
                DrbComponent.UISystem.ShowMessage("Error", jsonData["msg"].ToString());
                return;
            }

            int passportId = jsonData["data"]["passportId"].ToString().ToInt();
            string token = jsonData["data"]["token"].ToString();
            AccountLogin(passportId, token);
        }
    }

    private void AccountLogin(int passportId, string token)
    {
        if (m_isBusy) return;
        m_isBusy = true;
        Dictionary<string, object> dic = new Dictionary<string, object>();
        dic["passportId"] = passportId;
        dic["token"] = token;
        dic["device"] = DeviceUtil.GetPlatform();
        DrbComponent.HttpSystem.EncryptedRequest(ConstDefine.WebUrl, "passport/relogin", dic, OnLoginCallBack);
    }

    private void OnLoginCallBack(object sender, HttpRequestCompleteEventArgs args)
    {
        m_isBusy = false;
        if (args.HasError)
        {
            DrbComponent.UISystem.ShowMessage("Error", "Connected fail");
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
        if (m_isBusy) return;
        m_isBusy = true;
        AccountEntity entity = DrbComponent.SettingSystem.GetObject<AccountEntity>("AccountInfo");
        Dictionary<string, object> dic = new Dictionary<string, object>();
        dic["passportId"] = entity.passportId;
        dic["token"] = entity.token;
        DrbComponent.HttpSystem.EncryptedRequest(ConstDefine.WebUrl, "passport/server", dic, RequestServerCallBack);
    }

    private void RequestServerCallBack(object sender, HttpRequestCompleteEventArgs args)
    {
        m_isBusy = false;
        if (args.HasError)
        {
            DrbComponent.UISystem.ShowMessage("Error", "Connected fail", type: MessageForm.MessageViewType.Ok, okAction: RequestServer);
        }
        else
        {
            LitJson.JsonData jsonData = LitJson.JsonMapper.ToObject(Encoding.UTF8.GetString(args.Data));
            if (jsonData["code"].ToString().ToInt() < 0)
            {
                DrbComponent.UISystem.ShowMessage("Error", jsonData["msg"].ToString());
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



    private void Connect()
    {
        if (m_isBusy) return;
        m_isBusy = true;
        string ip = DrbComponent.SettingSystem.GetString("IP");
        int port = DrbComponent.SettingSystem.GetInt("Port");
        DrbComponent.NetworkSystem.Connect(ip, port);
    }

    public override void OnUpdate(float elapseSeconds, float realElapseSeconds)
    {
        base.OnUpdate(elapseSeconds, realElapseSeconds);

        //hand shake time out
        //if (m_SendHandShakeClientTime > 0 && TimeUtil.GetTimestampMS() - m_SendHandShakeClientTime > HAND_SHAKE_TIME_OUT)
        //{
        //    Log.Info("hand shake time out");
        //    m_SendHandShakeClientTime = 0;
        //    DrbComponent.NetworkSystem.Close();
        //}
    }

    private void OnHandShaked(object sender, EventArgs<int> args)
    {
        m_isBusy = false;
        ChangeState<MainMenuProcedure>();
    }
}
