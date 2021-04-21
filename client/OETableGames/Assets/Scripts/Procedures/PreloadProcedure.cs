//===================================================
//Author      : DRB
//CreateTime  ：2021/2/25 12:48:51
//Description ：
//===================================================
using DrbFramework.Internal;
using DrbFramework.Procedure;

public class PreloadProcedure : Procedure
{
    public override void OnEnter(object userData)
    {
        base.OnEnter(userData);

        //DrbFramework.Log.Info("wtf");
        byte[] games = DrbComponent.ResourceSystem.LoadFile("Datatable/Games.bytes", DrbFramework.Resource.LoadMode.Editor);
        DrbComponent.DataTableSystem.CreateDataTable<GamesDataEntity>(games);
        byte[] settings = DrbComponent.ResourceSystem.LoadFile("Datatable/MahjongSettings.bytes", DrbFramework.Resource.LoadMode.Editor);
        DrbComponent.DataTableSystem.CreateDataTable<MahjongSettingsDataEntity>(settings);
        ChangeState<LoginProcedure>();
    }

    public override void OnLeave()
    {
        base.OnLeave();

        DrbComponent.UISystem.CloseAllForm();
    }
}
