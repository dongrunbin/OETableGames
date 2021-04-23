//===================================================
//Author      : DRB
//CreateTime  ：2021/2/25 12:48:51
//Description ：
//===================================================
using DrbFramework.Internal;
using DrbFramework.Procedure;
using UnityEngine;

public class PreloadProcedure : Procedure
{
    public override void OnEnter(object userData)
    {
        base.OnEnter(userData);
        string language = DrbComponent.SettingSystem.GetString("Language");
        if (!string.IsNullOrEmpty(language))
        {
            DrbComponent.LocalizationSystem.Language = language;
        }
        TextAsset dictionary = DrbComponent.ResourceSystem.LoadAsset<TextAsset>(string.Format("Localization/{0}/Dictionary.txt", DrbComponent.LocalizationSystem.Language));
        DrbComponent.LocalizationSystem.ParseDictionary(dictionary);

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
