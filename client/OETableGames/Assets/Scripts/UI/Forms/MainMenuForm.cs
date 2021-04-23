//===================================================
//Author      : DRB
//CreateTime  ：2021/3/7 0:45:33
//Description ：
//===================================================

using DrbFramework.Internal;
using UnityEngine;
using UnityEngine.UI;

public class MainMenuForm : FormBase
{
    [SerializeField]
    private RawImage m_Avatar;
    [SerializeField]
    private Text m_TextNickname;
    [SerializeField]
    private Text m_TextPassportId;
    [SerializeField]
    private Text m_TextGold;
    [SerializeField]
    private Button m_BtnCreateRoom;
    [SerializeField]
    private Button m_BtnJoinRoom;
    [SerializeField]
    private Button m_BtnSetting;
    [SerializeField]
    private Button m_BtnCredit;

    protected override void OnBtnClick(GameObject go)
    {
        base.OnBtnClick(go);
        if (go == m_BtnCreateRoom.gameObject)
        {
            DrbComponent.UISystem.OpenFormAsync("UI/Forms/CreateRoomForm", "Form", null);
        }
        else if (go == m_BtnJoinRoom.gameObject)
        {
            DrbComponent.UISystem.OpenFormAsync("UI/Forms/JoinRoomForm", "Form", null);
        }
        else if (go == m_BtnSetting.gameObject)
        {
            DrbComponent.UISystem.OpenFormAsync("UI/Forms/SettingsForm", "Form", null);
        }
        else if (go == m_BtnCredit.gameObject)
        {
            DrbComponent.UISystem.OpenFormAsync("UI/Forms/CreditForm", "Form", null);
        }
    }

    public void SetUI(string nickname, int passportId, int gold)
    {
        m_TextNickname.SafeSetText(nickname);
        m_TextPassportId.SafeSetText(passportId.ToString());
        m_TextGold.SafeSetText(gold.ToString());
    }
}
