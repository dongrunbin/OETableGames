//===================================================
//Author      : DRB
//CreateTime  ：2021/4/28 2:48:37
//Description ：
//===================================================

using DrbFramework.Audio;
using DrbFramework.Internal;

public static class AudioSystemExtension
{
    public static void PlayMusic(this AudioSystem audioSystem, string audioPath)
    {
        AudioInfo info = new AudioInfo();
        info.Loop = true;
        info.Tag = "Music";
        info.Volume = DrbComponent.SettingSystem.GetFloat("MusicVolume");
        UnityEngine.Object audio = DrbComponent.ResourceSystem.LoadAsset<UnityEngine.Object>(audioPath);
        audioSystem.PlayAudio(audio, info);
    }

    public static void PlaySoundEffect(this AudioSystem audioSystem, string audioPath)
    {
        AudioInfo info = new AudioInfo();
        info.Loop = false;
        info.Tag = "SoundEffect";
        info.Volume = DrbComponent.SettingSystem.GetFloat("SoundEffectVolume");
        UnityEngine.Object audio = DrbComponent.ResourceSystem.LoadAsset<UnityEngine.Object>(audioPath);
        audioSystem.PlayAudio(audio, info);
    }
}
