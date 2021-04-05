//===================================================
//Author      : DRB
//CreateTime  ：2021/2/25 12:51:36
//Description ：
//===================================================
using DrbFramework.Internal;
using DrbFramework.Procedure;
using DrbFramework.UI;
using System;

public class MainMenuProcedure : Procedure
{
    private MainMenuForm m_MainMenuForm;

    public override void OnEnter(object userData)
    {
        base.OnEnter(userData);

        DrbComponent.UISystem.OpenFormAsync("UI/Forms/MainMenuForm", "BackGround", (IUIForm form)=> 
        {
            m_MainMenuForm = (MainMenuForm)form;
        });
    }
}
