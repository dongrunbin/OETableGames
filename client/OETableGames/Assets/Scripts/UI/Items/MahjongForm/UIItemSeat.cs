//===================================================
//Author      : DRB
//CreateTime  ：2021/4/6 3:24:09
//Description ：
//===================================================
using DrbFramework.Extensions;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class UIItemSeat : UIItemBase
{
    [SerializeField]
    protected GameObject m_Ready;
    [SerializeField]
    protected int m_nSeatIndex = -1;
    [SerializeField]
    protected RawImage m_ImageHead;
    [SerializeField]
    protected Text m_TextGold;
    [SerializeField]
    protected Text m_TextNickname;
    [SerializeField]
    protected Image m_Banker;
    [SerializeField]
    protected Image m_ImgOperating;

    public int SeatIndex
    {
        get { return m_nSeatIndex; }
        set { m_nSeatIndex = value; }
    }

    protected override void OnAwake()
    {
        base.OnAwake();
        if (m_Ready != null)
        {
            m_Ready.SetActive(false);
        }

        if (m_ImgOperating != null)
        {
            m_ImgOperating.gameObject.SetActive(false);
            m_ImgOperating.fillAmount = 0f;
        }
    }

    public void SetSeat(Seat seat)
    {
        m_Banker.SafeSetActive(seat.IsBanker);
        m_TextGold.SafeSetText(seat.Gold.ToString());
        m_Ready.SafeSetActive(seat.Status == SeatStatus.Ready);
        m_ImgOperating.SafeSetActive(seat.Status == SeatStatus.PlayPoker);
        m_TextNickname.SafeSetText(seat.Nickname);
    }

    public void SetBanker(bool isBanker)
    {
        m_Banker.SafeSetActive(isBanker);
    }

    public void SetGold(int gold)
    {
        m_TextGold.SafeSetText(gold.ToString());
    }

    public void SetReady(bool isReady)
    {
        m_Ready.SafeSetActive(isReady);
    }

    public void SetOperating(bool isOperating)
    {
        m_ImgOperating.SafeSetActive(isOperating);
    }

    public void SetNickName(string nickname)
    {
        m_TextNickname.SafeSetText(nickname);
    }
}
