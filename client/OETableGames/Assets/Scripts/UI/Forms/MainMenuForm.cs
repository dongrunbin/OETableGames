//===================================================
//Author      : DRB
//CreateTime  ：2021/3/7 0:45:33
//Description ：
//===================================================

using DrbFramework.Extensions;
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
    [SerializeField]
    private Button m_BtnExit;

    public override void OnInit()
    {
        base.OnInit();

#if !UNITY_EDITOR && !UNITY_STANDALONE_WIN
        m_BtnExit.SafeSetActive(false);
#endif
    }

    protected override void OnBtnClick(GameObject go)
    {
        base.OnBtnClick(go);
        if (go == m_BtnCreateRoom.gameObject)
        {
            DrbComponent.UISystem.OpenFormAsync("CreateRoomForm", "Form", null);
        }
        else if (go == m_BtnJoinRoom.gameObject)
        {
            DrbComponent.UISystem.OpenFormAsync("JoinRoomForm", "Form", null);
        }
        else if (go == m_BtnSetting.gameObject)
        {
            DrbComponent.UISystem.OpenFormAsync("SettingsForm", "Form", null);
        }
        else if (go == m_BtnCredit.gameObject)
        {
            DrbComponent.UISystem.OpenFormAsync("CreditForm", "Form", null);
        }
        else if (go == m_BtnExit.gameObject)
        {
            Application.Quit();
        }
    }

    public void SetUI(string nickname, int passportId, int gold)
    {
        m_TextNickname.SafeSetText(nickname);
        m_TextPassportId.SafeSetText(passportId.ToString());
        m_TextGold.SafeSetText(gold.ToString());
    }
}
