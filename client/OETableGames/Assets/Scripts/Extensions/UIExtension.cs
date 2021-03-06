//===================================================
//Author      : DRB
//CreateTime  ：2021/3/3 23:23:03
//Description ：
//===================================================

using DG.Tweening;
using UnityEngine.UI;

public static class UIExtension
{
    /// <summary>
    /// 安全设置Text
    /// </summary>
    /// <param name="txtObj"></param>
    /// <param name="text"></param>
    public static void SafeSetText(this Text txtObj, string text, bool isAnimation = false, float duration = 0.2f, ScrambleMode scrambleMode = ScrambleMode.None)
    {
        if (txtObj != null)
        {
            if (isAnimation)
            {
                txtObj.text = "";
                txtObj.DOText(text, duration, true, scrambleMode);
            }
            else
            {
                txtObj.text = text;
            }
        }
    }

    public static void SafeSetSliderValue(this Slider sliObj, float value)
    {
        if (sliObj != null)
        {
            sliObj.value = value;
        }
    }
}
