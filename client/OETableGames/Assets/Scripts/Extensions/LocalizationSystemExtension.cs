//===================================================
//Author      : DRB
//CreateTime  ：2021/4/23 6:46:26
//Description ：
//===================================================
using DrbFramework.Internal;
using DrbFramework.Localization;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;

public static class LocalizationSystemExtension
{
    public static void SetLanguage(this LocalizationSystem system, string language)
    {
        DrbComponent.LocalizationSystem.Language = language;
        TextAsset dictionary = DrbComponent.ResourceSystem.LoadAsset<TextAsset>(string.Format("Localization/{0}/Dictionary.txt", language));
        DrbComponent.LocalizationSystem.ParseDictionary(dictionary);
        DrbComponent.SettingSystem.SetString("Language", language);
    }
}
