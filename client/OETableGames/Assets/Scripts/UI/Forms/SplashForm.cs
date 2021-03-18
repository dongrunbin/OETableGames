//===================================================
//Author      : DRB
//CreateTime  ：2021/2/25 12:58:41
//Description ：
//===================================================
using DrbFramework;
using DrbFramework.Internal;
using DrbFramework.Timer;
using UnityEngine;
using UnityEngine.UI;

public class SplashForm : UGUIForm
{
    [SerializeField]
    private Text m_Title;

    private string m_Text;

    public override void OnInit()
    {
        base.OnInit();

        m_Text = m_Title.text;
    }

    public override void OnOpen()
    {
        base.OnOpen();

        DrbComponent.TimerSystem.RegisterTimer(0f, 0.1f, m_Text.Length + 20, null, OnTimerUpdate, OnTimerComplete);
    }

    private void OnTimerUpdate(Timer timer)
    {
        m_Title.text = m_Text.Substring(0, Mathf.Min(timer.CurrentLoop, m_Text.Length));
    }

    private void OnTimerComplete(Timer timer)
    {
        DrbComponent.ProcedureSystem.ChangeProcedure<CheckVersionProcedure>();
    }
}
