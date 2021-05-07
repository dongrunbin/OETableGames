//===================================================
//Author      : DRB
//CreateTime  ：2021/5/1 20:35:40
//Description ：
//===================================================
using UnityEngine;
using UnityEngine.UI;

public class UIItemResultSeat : UIItemBase
{
    [SerializeField]
    private RawImage m_HeadImage;
    [SerializeField]
    private Text m_TextNickname;
    [SerializeField]
    private Text m_TextId;
    [SerializeField]
    private Text m_Score;

    public virtual void SetUI(Seat seat)
    {
        m_TextNickname.SafeSetText(seat.Nickname);
        m_TextId.SafeSetText(seat.PlayerId.ToString());
        m_Score.SafeSetText(seat.Gold.ToString());

        //if (seat.res != null)
        //{
        //    foreach (KeyValuePair<string, string> pair in seat.resultData)
        //    {
        //        string key = pair.Key;
        //        string value = pair.Value;

        //        GameObject go = Instantiate(m_DescPrefab);
        //        go.SetActive(true);
        //        go.SetParent(m_DescContainer);
        //        go.transform.GetChild(0).GetComponent<Text>().SafeSetText(key);
        //        go.transform.GetChild(0).GetChild(0).GetComponent<Text>().SafeSetText(value);
        //    }
        //}
    }
}
