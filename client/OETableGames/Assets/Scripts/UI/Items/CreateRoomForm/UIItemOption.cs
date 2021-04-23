//===================================================
//Author      : DRB
//CreateTime  ：2021/4/3 22:55:26
//Description ：
//===================================================
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;
using System;
using DrbFramework.Internal;

public enum SelectMode
{
    None,
    Single,
    Multiple,
    Loop,
}

[Serializable]
public struct RuleEvent
{
    public int id;
    public bool isShow;
    public bool isOn;
    public string Description;
}

public struct SelectContent
{
    public int OptionId;
    public string OptionName;
}

public class UIItemOption : UIItemBase
{
    [SerializeField]
    private Toggle m_Toggle;
    [SerializeField]
    private Text m_txtName;
    [SerializeField]
    private Image m_imgBg;
    [SerializeField]
    private Image m_imgMark;
    [SerializeField]
    private Sprite m_SingleBg;
    [SerializeField]
    private Sprite m_SingleMark;
    [SerializeField]
    private Sprite m_MultiBg;
    [SerializeField]
    private Sprite m_MultiMark;
    [SerializeField]
    private Button m_btnNext;
    [SerializeField]
    private Button m_btnPrev;
    [SerializeField]
    private Text m_txtCurrent;

    public Action<UIItemOption, bool> onValueChanged;


    public int OptionId { get; set; }
    public string OptionTags { get; set; }
    public int Value { get; set; }
    public int Cost { get; set; }
    public int CostTotal { get; set; }

    private int m_Index;
    public int Index
    {
        get
        {
            return m_Index;
        }
        set
        {
            m_Index = value;
            if (m_Index < 0)
            {
                m_Index = Content.Count - 1;
            }
            else if (m_Index > Content.Count - 1)
            {
                m_Index = 0;
            }
            if (Content.Count > m_Index)
            {
                m_txtCurrent.SafeSetText(DrbComponent.LocalizationSystem.GetString(Content[m_Index].OptionName));
            }
        }
    }

    private List<SelectContent> m_Content = new List<SelectContent>();
    public List<SelectContent> Content
    {
        get { return m_Content; }
    }

    private string m_OptionName;
    public string OptionName
    {
        get { return m_OptionName; }
        set
        {
            m_OptionName = value;
            m_txtName.SafeSetText(DrbComponent.LocalizationSystem.GetString(m_OptionName));

            if (m_Content.Count > 0)
            {
                m_txtCurrent.SafeSetText(DrbComponent.LocalizationSystem.GetString(Content[0].OptionName));
            }
        }
    }

    private string m_GroupName;
    public string GroupName
    {
        get { return m_GroupName; }
        set
        {
            m_GroupName = value;
        }
    }

    private SelectMode m_Mode;
    public SelectMode Mode
    {
        get { return m_Mode; }
        set
        {
            m_Mode = value;
            switch (m_Mode)
            {
                case SelectMode.Single:
                    m_imgBg.overrideSprite = m_SingleBg;
                    m_imgMark.overrideSprite = m_SingleMark;
                    break;
                case SelectMode.Multiple:
                    m_imgBg.overrideSprite = m_MultiBg;
                    m_imgMark.overrideSprite = m_MultiMark;
                    break;
                case SelectMode.Loop:
                    break;
            }
            m_Toggle.gameObject.SetActive(!(m_Mode == SelectMode.Loop));
            m_txtCurrent.gameObject.SetActive(m_Mode == SelectMode.Loop);
        }
    }


    private bool m_isDisplay;
    public bool isDisplay
    {
        get { return m_isDisplay; }
        set
        {
            m_isDisplay = value;
            this.gameObject.SetActive(m_isDisplay);
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

    public void SetGroup(ToggleGroup group)
    {
        m_Toggle.group = group;
    }


    protected override void OnAwake()
    {
        base.OnAwake();
        m_Toggle.onValueChanged.AddListener(OnValueChanged);
        m_btnPrev.onClick.AddListener(OnPrevClick);
        m_btnNext.onClick.AddListener(OnNextClick);
    }

    private void OnNextClick()
    {
        ++Index;
    }

    private void OnPrevClick()
    {
        --Index;
    }

    private void OnValueChanged(bool isOn)
    {
        if (onValueChanged != null)
        {
            onValueChanged(this, isOn);
        }
    }
}
