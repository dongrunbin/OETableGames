//===================================================
//Author      : DRB
//CreateTime  ：2021/3/7 1:39:08
//Description ：
//===================================================

using DrbFramework.Internal;
using System;
using UnityEngine;
using UnityEngine.UI;

public class SettingForm : FormBase
{
    [SerializeField]
    private Slider m_SliderBGMVolume;
    [SerializeField]
    private Slider m_SliderSoundEffectVolume;
    [SerializeField]
    private Toggle[] m_ArrLanguage;


    public override void OnInit()
    {
        base.OnInit();
        m_SliderBGMVolume.onValueChanged.AddListener(OnSliderBGMVolumeChanged);
        m_SliderSoundEffectVolume.onValueChanged.AddListener(OnSliderSoundEffectVolumeChanged);

        if (m_ArrLanguage != null && m_ArrLanguage.Length > 0)
        {
            for (int i = 0; i < m_ArrLanguage.Length; ++i)
            {
                m_ArrLanguage[i].onValueChanged.AddListener(OnLanguageChanged);
            }
        }

        m_SliderBGMVolume.value = DrbComponent.SettingSystem.GetFloat("BGMVolume");
        m_SliderSoundEffectVolume.value = DrbComponent.SettingSystem.GetFloat("SoundEffectVolume");

        if (m_ArrLanguage != null)
        {
            for (int i = 0; i < m_ArrLanguage.Length; ++i)
            {
                if (m_ArrLanguage[i].name.Equals(DrbComponent.LocalizationSystem.Language, StringComparison.CurrentCultureIgnoreCase))
                {
                    m_ArrLanguage[i].isOn = true;
                }
            }
        }
    }

    /// <summary>
    /// 语言变更
    /// </summary>
    /// <param name="arg0"></param>
    private void OnLanguageChanged(bool isOn)
    {
        for (int i = 0; i < m_ArrLanguage.Length; ++i)
        {
            if (m_ArrLanguage[i].isOn)
            {
                DrbComponent.LocalizationSystem.Language = m_ArrLanguage[i].name;
                break;
            }
        }
    }

    /// <summary>
    /// 背景音乐音量条值变化
    /// </summary>
    /// <param name="value"></param>
    private void OnSliderBGMVolumeChanged(float value)
    {
        //DrbComponent.AudioSystem.("BGMVolume", value);
    }

    private void OnSliderSoundEffectVolumeChanged(float value)
    {
        //DrbComponent.SettingSystem.SetFloat("SoundEffectVolume", value);
    }
}
