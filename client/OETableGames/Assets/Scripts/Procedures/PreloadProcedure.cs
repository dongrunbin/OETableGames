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

        DrbComponent.ResourceSystem.LoadManifestFile();
        string language = DrbComponent.SettingSystem.GetString("Language");
        if (!string.IsNullOrEmpty(language))
        {
            DrbComponent.LocalizationSystem.Language = language;
        }
        else
        {
            //DrbComponent.LocalizationSystem.Language = Application.systemLanguage.ToString();
        }
        DrbComponent.LocalizationSystem.SetLanguage(DrbComponent.LocalizationSystem.Language);
        //TextAsset dictionary = DrbComponent.ResourceSystem.LoadAsset<TextAsset>(string.Format("Downloads/Localization/{0}/Dictionary.txt", DrbComponent.LocalizationSystem.Language));
        //DrbComponent.LocalizationSystem.ParseDictionary(dictionary);

#if !ASSETBUNDLE
        byte[] games = DrbComponent.ResourceSystem.LoadFile("Downloads/Datatable/Games.bytes", DrbFramework.Resource.LoadMode.Editor);
        DrbComponent.DataTableSystem.CreateDataTable<GamesDataEntity>(games);
        byte[] settings = DrbComponent.ResourceSystem.LoadFile("Downloads/Datatable/Settings.bytes", DrbFramework.Resource.LoadMode.Editor);
        DrbComponent.DataTableSystem.CreateDataTable<SettingsDataEntity>(settings);
#else
        TextAsset games = DrbComponent.ResourceSystem.LoadAsset<TextAsset>("Downloads/Datatable/Games.bytes");
        DrbComponent.DataTableSystem.CreateDataTable<GamesDataEntity>(games.bytes);
        TextAsset settings = DrbComponent.ResourceSystem.LoadAsset<TextAsset>("Downloads/Datatable/Settings.bytes");
        DrbComponent.DataTableSystem.CreateDataTable<SettingsDataEntity>(settings.bytes);
#endif

#if ASSETBUNDLE
        DrbComponent.LuaSystem.DoString(string.Format("package.path = '{0}Downloads/Lua/?.lua.txt'", DrbComponent.ResourceSystem.PersistentPath));
#else
        DrbComponent.LuaSystem.DoString(string.Format("package.path = '{0}Downloads/Lua/?.lua.txt'", DrbComponent.ResourceSystem.EditorPath));
#endif
        DrbComponent.LuaSystem.Initialize("require 'Main'", "LuaSystem.Init", "LuaSystem.Update", "LuaSystem.Shutdown");
        ChangeState<LoginProcedure>();
    }

    public override void OnLeave()
    {
        base.OnLeave();

        DrbComponent.UISystem.CloseAllForm();
    }
}
