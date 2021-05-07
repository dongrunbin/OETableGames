//===================================================
//Author      : DRB
//CreateTime  ：2021/4/3 22:49:47
//Description ：
//===================================================
using DrbFramework.Extensions;
using DrbFramework.Internal.Localization;
using System;
using UnityEngine;
using UnityEngine.UI;

public class UIItemGameType : UIItemBase
{
    [SerializeField]
    private Toggle m_Toggle;
    [SerializeField]
    private Text m_txtType;

    private string m_GameType;
    public string GameType
    {
        get { return m_GameType; }
        set
        {
            m_GameType = value;
            m_txtType.gameObject.GetOrAddComponent<UGuiTextLocalizer>().Key = m_GameType;
        }
    }

    public bool isOn
    {
        get { return m_Toggle.isOn; }
        set
        {
            m_Toggle.isOn = value;
        }
    }

    public Action<string, bool> onValueChanged;

    public void SetToggleGroup(ToggleGroup group)
    {
        m_Toggle.group = group;
    }


    protected override void OnAwake()
    {
        base.OnAwake();
        m_Toggle.onValueChanged.AddListener(OnValueChanged);
    }

    private void OnValueChanged(bool isOn)
    {
        if (onValueChanged != null)
        {
            onValueChanged(m_GameType, isOn);
        }
    }
}
