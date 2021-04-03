//===================================================
//Author      : DRB
//CreateTime  ：2021/3/7 1:52:34
//Description ：
//===================================================

using DrbFramework.Extensions;
using UnityEngine;
using UnityEngine.UI;

public class JoinRoomForm : FormBase
{
    [SerializeField]
    private Button[] m_Buttons;
    [SerializeField]
    private Text[] m_Texts;

    /// <summary>
    /// 房间号
    /// </summary>
    private string m_RoomId;
    /// <summary>
    /// 已经输入的数量
    /// </summary>
    private int m_nAlreadyInputCount;
    protected override void OnBtnClick(GameObject go)
    {
        base.OnBtnClick(go);
        //for (int i = 0; i < m_Buttons.Length; ++i)
        //{
        //    if (m_Buttons[i].name.Equals(go.name))
        //    {
        //        if (m_nAlreadyInputCount == m_Texts.Length) return;
        //        m_RoomId += i.ToString();
        //        m_Texts[m_nAlreadyInputCount].SafeSetText(i.ToString());
        //        ++m_nAlreadyInputCount;

        //        if (m_nAlreadyInputCount == m_Texts.Length)
        //        {
        //            TransferData data = new TransferData();
        //            data.SetValue("RoomId", m_RoomId.ToInt());
        //            SendNotification(ConstDefine.BtnJoinRoomViewJoin, data);
        //        }
        //        return;
        //    }
        //}
        switch (go.name)
        {
            case "btnResetRoomId":
                ResetUI();
                break;
            case "btnDeleteRoomId":
                DeleteRoomId();
                break;
        }
    }

    private void ResetUI()
    {
        for (int i = 0; i < m_Texts.Length; ++i)
        {
            m_Texts[i].SafeSetText("");
        }
        m_RoomId = string.Empty;
        m_nAlreadyInputCount = 0;
    }

    private void DeleteRoomId()
    {
        if (m_nAlreadyInputCount == 0) return;
        --m_nAlreadyInputCount;
        m_RoomId = m_RoomId.Substring(0, m_nAlreadyInputCount);
        m_Texts[m_nAlreadyInputCount].SafeSetText("");
    }


    public override void OnFocus()
    {
        base.OnFocus();
        ResetUI();
    }
}
