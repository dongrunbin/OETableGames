//===================================================
//Author      : DRB
//CreateTime  ：2021/2/25 12:51:36
//Description ：
//===================================================
using DrbFramework.Event;
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

        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.Game_S2C_EnterRoomProto, OnEnterRoom);

        DrbComponent.UISystem.OpenFormAsync("UI/Forms/MainMenuForm", "BackGround", (IUIForm form)=> 
        {
            m_MainMenuForm = (MainMenuForm)form;
            AccountEntity account = DrbComponent.SettingSystem.GetObject<AccountEntity>("AccountInfo");
            m_MainMenuForm.SetUI(account.nickname, account.passportId, account.gold);
        });
    }

    public override void OnLeave()
    {
        base.OnLeave();
        DrbComponent.UISystem.CloseAllForm();
        DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.Game_S2C_EnterRoomProto, OnEnterRoom);
    }

    private void OnEnterRoom(object sender, EventArgs<int> args)
    {
        ChangeState<MahjongProcedure>();
    }
}
