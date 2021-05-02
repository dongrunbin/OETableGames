//===================================================
//Author      : DRB
//CreateTime  ：2021/5/1 20:56:09
//Description ：
//===================================================
using DrbFramework.Internal;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class UIItemResult : UIItemBase
{
    [SerializeField]
    protected Button m_BtnLeave;
    [SerializeField]
    protected List<UIItemResultSeat> m_Seat = new List<UIItemResultSeat>();

    protected override void OnAwake()
    {
        base.OnAwake();

        m_BtnLeave.onClick.AddListener(OnLeaveClick);
    }

    private void OnLeaveClick()
    {
        DrbComponent.ProcedureSystem.ChangeProcedure<MainMenuProcedure>();
    }


    public void SetUI(Room room)
    {
        for (int i = 0; i < m_Seat.Count; ++i)
        {
            m_Seat[i].gameObject.SetActive(false);
        }

        IList<Seat> seats = room.SeatList;
        for (int i = 0; i < seats.Count; ++i)
        {
            Seat seat = seats[i];
            UIItemResultSeat info = m_Seat[i];
            info.gameObject.SetActive(true);
            info.SetUI(seat);
        }
    }
}
