//===================================================
//Author      : DRB
//CreateTime  ：2021/3/3 22:22:54
//Description ：
//===================================================

using DrbFramework.Extensions;
using DrbFramework.Http;
using DrbFramework.Internal;
using System.Collections.Generic;
using System.Text;
using UnityEngine;
using UnityEngine.UI;
using DrbFramework;

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

    public override void OnOpen()
    {
        base.OnOpen();
        AccountEntity entity = DrbComponent.SettingSystem.GetObject<AccountEntity>("AccountInfo");
        if (entity != null)
        {
            AccountLogin(entity.passportId, entity.token);
        }
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
        dic["device"] = DeviceUtil.GetOssPlatform();
        DrbComponent.HttpSystem.EncryptedRequest(ConstDefine.WebUrl, "passport/relogin", dic, OnLoginCallBack);
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
            Log.Info(ip + ":" + port);
            DrbComponent.NetworkSystem.Connect(ip, port);
        }
    }
}
