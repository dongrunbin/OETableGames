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
    private GameObject m_Ready;
    [SerializeField]
    private int m_nSeatIndex = -1;
    [SerializeField]
    private RawImage m_ImageHead;
    [SerializeField]
    private Text m_TextGold;
    [SerializeField]
    private Text m_TextNickname;
    [SerializeField]
    private Image m_Banker;
    [SerializeField]
    private Image m_ImgOperating;
    [SerializeField]
    private Button m_BtnAddRobot;
    [SerializeField]
    private GameObject m_SeatInfo;



    public int SeatIndex
    {
        get { return m_nSeatIndex; }
        set { m_nSeatIndex = value; }
    }

    protected override void OnAwake()
    {
        base.OnAwake();

        m_Ready.SafeSetActive(false);

        if (m_ImgOperating != null)
        {
            m_ImgOperating.gameObject.SetActive(false);
            m_ImgOperating.fillAmount = 0f;
        }

        if (m_BtnAddRobot != null)
        {
            m_BtnAddRobot.GetComponent<Button>().onClick.AddListener(() =>
            {
                MahjongService.Instance.ClientSendAddRobot(SeatIndex + 1);
            });
        }
    }

    public override void Show()
    {
        m_SeatInfo.SafeSetActive(true);
        SetAddRobot(false);
    }

    public override void Hide()
    {
        m_SeatInfo.SafeSetActive(false);
        SetAddRobot(true);
    }

    public void SetSeat(Seat seat)
    {
        m_Banker.SafeSetActive(seat.IsBanker);
        m_TextGold.SafeSetText(seat.Gold.ToString());
        m_Ready.SafeSetActive(seat.Status == SeatStatus.Ready);
        m_ImgOperating.SafeSetActive(seat.Status == SeatStatus.Discard);
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

    public void SetAddRobot(bool isActive)
    {
        m_BtnAddRobot.SafeSetActive(isActive);
    }
}
