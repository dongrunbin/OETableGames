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
    protected Text m_TextLoop;
    [SerializeField]
    protected Text m_TextRule;

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

    public virtual void SetRoomId(int roomId)
    {
        m_RoomId.SafeSetText(string.Format("房间号:{0}", roomId.ToString()));
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
}
