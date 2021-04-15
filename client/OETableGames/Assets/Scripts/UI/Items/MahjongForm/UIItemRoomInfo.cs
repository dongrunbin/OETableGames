//===================================================
//Author      : DRB
//CreateTime  ：2021/4/6 3:30:09
//Description ：
//===================================================
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class UIItemRoomInfo : UIItemBase
{
    [SerializeField]
    protected Text m_RoomId;

    [SerializeField]
    protected Text m_TextTime;

    [SerializeField]
    protected Text m_TextBaseScore;

    [SerializeField]
    protected Text m_TextLoop;
    [SerializeField]
    protected Text m_TextRule;
    [SerializeField]
    private Image[] m_Battery;
    [SerializeField]
    private Sprite[] m_BatterySprite;
    [SerializeField]
    private GameObject m_RuleGO;

    private float m_Timer;

    private const float UPDATE_SPACE = 10f;

    private const int ELECTRICITY_LEVEL = 10;

    protected override void OnAwake()
    {
        base.OnAwake();

    }

    void Update() 
    {
        if (Time.time > m_Timer)
        {
            m_Timer = Time.time + UPDATE_SPACE;
            m_TextTime.SafeSetText(TimeUtil.GetLocalTime());
        }
    }

    public virtual void SetElectricity(float electricity)
    {

        if (m_Battery != null)
        {
            int elec = (int)(electricity * ELECTRICITY_LEVEL);
            for (int i = 0; i < m_Battery.Length; ++i)
            {
                m_Battery[i].gameObject.SetActive(i <= elec);
                m_Battery[i].overrideSprite = m_BatterySprite[elec < 2 ? 1 : 0];
            }
        }
    }

    public virtual void SetRoomId(int roomId)
    {
        m_RoomId.SafeSetText(string.Format("房间号:{0}", roomId.ToString()));
    }

    public virtual void SetBaseScore(int baseScore)
    {
        m_TextBaseScore.SafeSetText(string.Format("底    分:{0}", baseScore.ToString()));
    }

    public virtual void SetLoop(int currentLoop, int maxLoop)
    {
        if (maxLoop >= 99)
        {
            m_TextLoop.SafeSetText("");
        }
        else
        {
            m_TextLoop.SafeSetText(string.Format("游戏局数:{0}/{1}", currentLoop, maxLoop));
        }
    }

    public virtual void SetRoomConfig(string str)
    {
        m_TextRule.SafeSetText(str);
    }

    public virtual void ChangeRuleActive()
    {
        if (m_RuleGO != null)
        {
            m_RuleGO.gameObject.SetActive(!m_RuleGO.activeInHierarchy);
        }
    }
}
