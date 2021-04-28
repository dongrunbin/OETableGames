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
        DrbComponent.GetEventSystem<int>().AddEventListener(CodeDef.Game_S2C_InRoomProto, OnInRoom);

        DrbComponent.AudioSystem.PlayMusic("Audio/Music/bgm_main.mp3");

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

        DrbComponent.AudioSystem.StopAllAudios();

        DrbComponent.GetEventSystem<int>().RemoveEventListener(CodeDef.Game_S2C_InRoomProto, OnInRoom);
    }

    private void OnInRoom(object sender, EventArgs<int> args)
    {
        Game_S2C_InRoomProto proto = new Game_S2C_InRoomProto(((NetworkEventArgs)args).Data);
        GamesDataEntity entity = DrbComponent.DataTableSystem.GetDataTable<GamesDataEntity>().GetEntity(proto.gameId);
        if (entity == null) return;
        ChangeState(entity.ProcedureName);
    }

    private void RequireRoom()
    {
        Game_C2S_InRoomProto proto = new Game_C2S_InRoomProto();
        DrbComponent.NetworkSystem.Send(proto);
    }
}
