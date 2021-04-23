//===================================================
//Author      : DRB
//CreateTime  ：2021/3/7 1:39:08
//Description ：
//===================================================

using DrbFramework.Internal;
using DrbFramework.Internal.Localization;
using System;
using UnityEngine;
using UnityEngine.UI;

public class SettingsForm : FormBase
{
    [SerializeField]
    private Slider m_SliderMusicVolume;
    [SerializeField]
    private Slider m_SliderSoundEffectVolume;
    [SerializeField]
    private Toggle[] m_ArrLanguage;


    public override void OnInit()
    {
        base.OnInit();
        m_SliderMusicVolume.onValueChanged.AddListener(OnSliderMusicVolumeChanged);
        m_SliderSoundEffectVolume.onValueChanged.AddListener(OnSliderSoundEffectVolumeChanged);

        if (m_ArrLanguage != null && m_ArrLanguage.Length > 0)
        {
            for (int i = 0; i < m_ArrLanguage.Length; ++i)
            {
                m_ArrLanguage[i].onValueChanged.AddListener(OnLanguageChanged);
            }
        }

        m_SliderMusicVolume.value = DrbComponent.SettingSystem.GetFloat("MusicVolume");
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

    private void OnLanguageChanged(bool isOn)
    {
        if (!isOn) return;
        for (int i = 0; i < m_ArrLanguage.Length; ++i)
        {
            if (m_ArrLanguage[i].isOn)
            {
                DrbComponent.LocalizationSystem.SetLanguage(m_ArrLanguage[i].name);
                break;
            }
        }
    }

    private void OnSliderMusicVolumeChanged(float value)
    {
        DrbComponent.SettingSystem.SetFloat("MusicVolume", value);
    }

    private void OnSliderSoundEffectVolumeChanged(float value)
    {
        DrbComponent.SettingSystem.SetFloat("SoundEffectVolume", value);
    }
}
