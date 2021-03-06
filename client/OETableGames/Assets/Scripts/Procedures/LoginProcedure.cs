//===================================================
//Author      : DRB
//CreateTime  ：2021/3/3 22:19:25
//Description ：
//===================================================

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
    }

    public override void OnLeave()
    {
        base.OnLeave();

        DrbComponent.UISystem.DestroyForm(m_LoginForm);
    }

    private void OnLoadUIComplete(IUIForm form)
    {
        m_LoginForm = (LoginForm)form;
    }
}
