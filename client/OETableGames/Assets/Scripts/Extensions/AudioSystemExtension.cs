
using DrbFramework.Audio;
using DrbFramework.Internal;

public static class AudioSystemExtension
{
    public static void PlayAudio(this AudioSystem audioSystem, string audioPath, string audioName, AudioInfo info)
    {
        UnityEngine.Object audio = DrbComponent.ResourceSystem.LoadAsset<UnityEngine.Object>(audioPath);
        audioSystem.PlayAudio(audio, info);
    }
}
