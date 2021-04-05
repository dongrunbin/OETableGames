//===================================================
//Author      : DRB
//CreateTime  ：2021/3/3 22:19:25
//Description ：
//===================================================

using DrbFramework.Event;
using DrbFramework.Internal;
using DrbFramework.Procedure;
using DrbFramework.UI;
using System;

public class LoginProcedure : Procedure
{
    private LoginForm m_LoginForm;
    public override void OnEnter(object userData)
    {
        base.OnEnter(userData);

        DrbComponent.UISystem.OpenFormAsync("UI/Forms/LoginForm", "BackGround", OnLoadUIComplete);
        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.System_C2S_HeartBeatProto - 1, OnConnected);
    }

    public override void OnLeave()
    {
        base.OnLeave();

        DrbComponent.UISystem.DestroyForm(m_LoginForm);
        DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.System_C2S_HeartBeatProto - 1, OnConnected);
    }

    private void OnLoadUIComplete(IUIForm form)
    {
        m_LoginForm = (LoginForm)form;
    }

    private void OnConnected(object sender, EventArgs<int> args)
    {
        //ChangeState<MainMenuProcedure>();

        ClientSendQueryRoom();
    }

    public override void OnUpdate(float elapseSeconds, float realElapseSeconds)
    {
        base.OnUpdate(elapseSeconds, realElapseSeconds);

    }

    private void ClientSendHandShake(int passportId, string token)
    {
        //System_ConnectProto proto = new System_ConnectProto();
        //proto.passportId = AccountCtrl.Instance.GetAccountEntity().passportId;
        //proto.token = AccountCtrl.Instance.GetAccountEntity().token;

        //m_SendHandShakeClientTime = TimeUtil.GetTimestampMS();
        //m_SocketClient.Send(proto.ToArray());
    }

    private void ClientSendQueryRoom()
    {
        //C2S_Game_QueryRoomProto proto = new C2S_Game_QueryRoomProto();
        //DrbComponent.NetworkSystem.GetChannel("MainServer").Send(proto.ToArray());
    }
}
