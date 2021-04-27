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
        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.Game_S2C_InRoomProto, OnInRoom);

        DrbComponent.UISystem.OpenFormAsync("UI/Forms/MainMenuForm", "BackGround", (IUIForm form)=> 
        {
            m_MainMenuForm = (MainMenuForm)form;
            AccountEntity account = DrbComponent.SettingSystem.GetObject<AccountEntity>("AccountInfo");
            m_MainMenuForm.SetUI(account.nickname, account.passportId, account.gold);

            RequireRoom();

        });
    }

    public override void OnLeave()
    {
        base.OnLeave();
        DrbComponent.UISystem.CloseAllForm();
        DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.Game_S2C_EnterRoomProto, OnEnterRoom);
        DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.Game_S2C_InRoomProto, OnInRoom);
    }

    private void OnEnterRoom(object sender, EventArgs<int> args)
    {
        ChangeState<MahjongProcedure>();
    }

    private void OnInRoom(object sender, EventArgs<int> args)
    {
        //Game_S2C_InRoomProto proto = new Game_S2C_InRoomProto(((NetworkEventArgs)args).Data);
        ChangeState<MahjongProcedure>();
    }

    private void RequireRoom()
    {
        Game_C2S_InRoomProto proto = new Game_C2S_InRoomProto();
        DrbComponent.NetworkSystem.Send(proto);
    }
}
