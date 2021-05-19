//===================================================
//Author      : DRB
//CreateTime  ：2021/5/1 20:35:40
//Description ：
//===================================================
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class UIItemSettleSeat : UIItemBase
{
    [SerializeField]
    protected RawImage m_Head;
    [SerializeField]
    protected Text m_TextNickName;
    [SerializeField]
    protected Image m_ImageBanker;
    [SerializeField]
    protected Text m_TextGold;
    [SerializeField]
    private Transform m_MahjongContainer;
    [SerializeField]
    private Text m_TextInfo;
    [SerializeField]
    private GameObject m_MahjongPrefab;
    [SerializeField]
    private List<Image> m_Mahjongs = new List<Image>();



    public void SetUI(Seat seat)
    {
        m_TextNickName.SafeSetText(seat.Nickname);
        m_TextGold.SafeSetText(seat.Settle.ToString("+#;-#;0"));
        m_ImageBanker.gameObject.SetActive(seat.IsBanker);

        string strInfo = string.Empty;
        if (!string.IsNullOrEmpty(seat.incomesDesc))
        {
            strInfo = seat.incomesDesc;
        }
        m_TextInfo.SafeSetText(strInfo);

        if (seat.HitMahjong != null)
        {
            seat.MahjongList.Add(seat.HitMahjong);
        }

        for (int i = 0; i < m_Mahjongs.Count; ++i)
        {
            m_Mahjongs[i].gameObject.SetActive(false);
        }
        int index = 0;
        if (seat.UsedMahjongGroups != null)
        {
            for (int i = 0; i < seat.UsedMahjongGroups.Count; ++i)
            {
                for (int j = 0; j < seat.UsedMahjongGroups[i].MahjongList.Count; ++j)
                {
                    m_Mahjongs[index].overrideSprite = MahjongManager.Instance.GetMahjongSprite(seat.UsedMahjongGroups[i].MahjongList[j]);
                    m_Mahjongs[index].gameObject.SetActive(true);
                    ++index;
                }
            }
        }
        for (int i = 0; i < seat.MahjongList.Count; ++i, ++index)
        {
            if (index >= m_Mahjongs.Count) continue;
            m_Mahjongs[index].overrideSprite = MahjongManager.Instance.GetMahjongSprite(seat.MahjongList[i]);
            m_Mahjongs[index].gameObject.SetActive(true);
        }


    }
}
