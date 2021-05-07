//===================================================
//Author      : DRB
//CreateTime  ：2021/4/3 22:52:49
//Description ：
//===================================================
using DrbFramework.Internal.Localization;
using DrbFramework.Extensions;
using System;
using UnityEngine;
using UnityEngine.UI;

public class UIItemToggleGame : UIItemBase
{
    [SerializeField]
    private Toggle m_Toggle;
    [SerializeField]
    private Text m_txtGameName;

    private string m_GameName;
    public string GameName
    {
        get { return m_GameName; }
        set
        {
            m_GameName = value;
            m_txtGameName.gameObject.GetOrAddComponent<UGuiTextLocalizer>().Key = m_GameName;
        }
    }

    public string GameType { get; set; }
    public int GameId { get; set; }

    public bool isOn
    {
        get { return m_Toggle.isOn; }
        set
        {
            m_Toggle.isOn = value;
        }
    }

    public Action<int, bool> onValueChanged;

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
            onValueChanged(GameId, isOn);
        }
    }
}
