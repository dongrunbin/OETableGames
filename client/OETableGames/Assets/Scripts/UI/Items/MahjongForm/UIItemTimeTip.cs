using UnityEngine;
using UnityEngine.UI;

public class UIItemTimeTip : UIItemBase
{
    [SerializeField]
    private Text m_TextCountDown;
    [SerializeField]
    private Image m_ImageOncePlace;
    [SerializeField]
    private Image m_ImageTensPlace;
    [SerializeField]
    private Sprite[] m_ArrTimeSprite;

    private float m_CurrentCountDown;

    private bool m_isCountDown;

    private bool m_isPlayedFirstAuido;

    private bool m_isPlayedSecondAudio;

    private bool m_isPlayer;

    private void Update()
    {
        if (m_isCountDown)
        {
            m_CurrentCountDown -= Time.deltaTime;
            if (m_CurrentCountDown < 8f)
            {
                if (!m_isPlayedFirstAuido)
                {
                    m_isPlayedFirstAuido = true;
                    if (m_isPlayer)
                    {
                        //AudioEffectManager.Instance.Play("daojishi", Vector3.zero, false);
                    }
                }
                if (m_CurrentCountDown < 3f)
                {
                    if (!m_isPlayedSecondAudio)
                    {
                        m_isPlayedSecondAudio = true;
                        if (m_isPlayer)
                        {
                            //AudioEffectManager.Instance.Play("daojishi", Vector3.zero, false);
                        }
                    }
                }
            }
            if (m_CurrentCountDown <= 0)
            {
                m_CurrentCountDown = 0;
                m_isCountDown = false;
                GameUtil.Vibrate();
            }
            SetTimeCount((int)m_CurrentCountDown);
        }
    }

    public void SetTime(int second, bool isPlayer)
    {

        m_isPlayedSecondAudio = false;
        m_isPlayedFirstAuido = false;
        m_isPlayer = isPlayer;
        if (second <= 0)
        {
            m_isCountDown = false;
            gameObject.SetActive(false);
            return;
        }
        gameObject.SetActive(true);

        SetTimeCount(second);

        m_CurrentCountDown = second;
        m_isCountDown = true;
    }

    private void SetTimeCount(int second)
    {
        if (m_TextCountDown != null)
        {
            m_TextCountDown.SafeSetText(second.ToString("0"));
        }

        int tens = second / 10;
        int once = second % 10;
        if (m_ImageOncePlace != null)
        {
            m_ImageOncePlace.overrideSprite = m_ArrTimeSprite[once];
        }

        if (m_ImageTensPlace != null)
        {
            m_ImageTensPlace.overrideSprite = m_ArrTimeSprite[Mathf.Clamp(tens, 0, 9)];
        }
    }
}
