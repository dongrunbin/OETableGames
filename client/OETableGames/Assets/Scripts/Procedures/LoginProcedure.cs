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
        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.System_HeartBeatProto - 1, OnConnected);
    }

    public override void OnLeave()
    {
        base.OnLeave();

        DrbComponent.UISystem.DestroyForm(m_LoginForm);
        DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.System_HeartBeatProto - 1, OnConnected);
    }

    private void OnLoadUIComplete(IUIForm form)
    {
        m_LoginForm = (LoginForm)form;
    }

    private void OnConnected(object sender, EventArgs<int> args)
    {
        ChangeState<MainMenuProcedure>();
    }
}
