//===================================================
//Author      : DRB
//CreateTime  ：2021/4/3 22:54:34
//Description ：
//===================================================
using DrbFramework.Internal.Localization;
using UnityEngine;
using UnityEngine.UI;

public class UIItemSettingGroup : UIItemBase
{
    [SerializeField]
    private Text m_txtGroupName;
    [SerializeField]
    private ToggleGroup m_OptionsContainer;

    public ToggleGroup ToggleGroup
    {
        get { return m_OptionsContainer; }
    }

    private string m_GroupName;
    public string GroupName
    {
        get { return m_GroupName; }
        set
        {
            m_GroupName = value;
            m_txtGroupName.GetComponent<UGuiTextLocalizer>().Key = m_GroupName;
        }
    }
}
