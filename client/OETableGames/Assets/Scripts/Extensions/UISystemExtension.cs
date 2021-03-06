//===================================================
//Author      : DRB
//CreateTime  ：2021/2/25 13:08:15
//Description ：
//===================================================
using DrbFramework;
using DrbFramework.Internal;
using DrbFramework.Resource;
using DrbFramework.UI;
using System;
using System.IO;
using XLua;

[LuaCallCSharp]
public delegate void OpenFormComplete(IUIForm form);

[LuaCallCSharp]
public static class UISystemExtensions
{
    public static IUIForm OpenInternalForm(this UISystem uiSystem, string assetPath, string groupName)
    {
        object asset = DrbComponent.ResourceSystem.LoadAsset(assetPath, LoadMode.Internal);
        return DrbComponent.UISystem.OpenForm(Path.GetFileNameWithoutExtension(assetPath), asset, groupName);
    }

    public static void OpenFormAsync(this UISystem uiSystem, string formName, string groupName, OpenFormComplete callback)
    {
        string assetPath = string.Format("Downloads/UI/Forms/{0}.prefab", formName);
        DrbComponent.ResourceSystem.LoadAssetAsync(assetPath, (LoadAssetCompleteEventArgs args) =>
        {
            IUIForm form = uiSystem.OpenForm(args.AssetName, args.Asset, groupName);
            if (callback != null)
            {
                callback(form);
            }
        }, null);
    }

    public static void ShowMessage(this UISystem uiSystem, string title, string message, float countDown = 0f,
        MessageForm.AutoClickType autoType = MessageForm.AutoClickType.None, MessageForm.MessageViewType type = MessageForm.MessageViewType.Ok,
        Action okAction = null, Action cancelAction = null)
    {
        MessageForm form = (MessageForm)OpenInternalForm(uiSystem, "UI/Forms/MessageForm", "Form");
        form.Show(DrbComponent.LocalizationSystem.GetString(title), DrbComponent.LocalizationSystem.GetString(message), countDown, autoType, type, okAction, cancelAction);
    }
}
