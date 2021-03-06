//===================================================
//Author      : DRB
//CreateTime  ：2021/3/3 22:22:54
//Description ：
//===================================================

using DrbFramework.Extensions;
using DrbFramework.Http;
using DrbFramework.Internal;
using System;
using System.Collections.Generic;
using System.Text;
using UnityEngine;
using UnityEngine.UI;
using DrbFramework.Internal.Network;
using DrbFramework.Network;

public class LoginForm : FormBase
{
    [SerializeField]
    private Button m_BtnGuestLogin;

    protected override void OnBtnClick(GameObject go)
    {
        base.OnBtnClick(go);
        if (go == m_BtnGuestLogin.gameObject)
        {
            GuestLogin();
        }
    }

    private void GuestLogin()
    {
        Dictionary<string, object> dic = new Dictionary<string, object>();
        DrbComponent.HttpSystem.Request(ConstDefine.WebUrl + "guest", dic, Encoding.UTF8, 1000, OnGuestLoginCallBack);
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

        DrbComponent.HttpSystem.Request(ConstDefine.WebUrl + "relogin", dic, Encoding.UTF8, 1000, OnGuestLoginCallBack);
    }

    private void OnLoginCallBack(object sender, HttpRequestCompleteEventArgs args)
    {
        if (args.HasError)
        {
            DrbComponent.UISystem.ShowMessage("错误", "网络连接失败");
            if (PlayerPrefs.HasKey("passportId"))
            {
                PlayerPrefs.DeleteKey("passportId");
                PlayerPrefs.DeleteKey("token");
            }
        }
        else
        {
            LitJson.JsonData jsonData = LitJson.JsonMapper.ToObject(Encoding.UTF8.GetString(args.Data));
            if (jsonData["code"].ToString().ToInt() < 0)
            {
                DrbComponent.UISystem.ShowMessage("错误", "网络连接失败");
                if (PlayerPrefs.HasKey("passportId"))
                {
                    PlayerPrefs.DeleteKey("passportId");
                    PlayerPrefs.DeleteKey("token");
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

        DrbComponent.HttpSystem.Request(ConstDefine.WebUrl + "passport/server", dic, Encoding.UTF8, 1000, OnGuestLoginCallBack);
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

            INetworkChannel channel = DrbComponent.NetworkSystem.CreateChannel("MainServer", new ExampleHandler(), new ExampleEncoder(), new ExampleDecoder());
            channel.Connect(System.Net.IPAddress.Parse(ip), port);
        }
    }
}
