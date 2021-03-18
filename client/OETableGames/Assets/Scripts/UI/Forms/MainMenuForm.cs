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
    private Text m_TextRoomCardCount;

    [SerializeField]
    private Text m_TextGold;

    [SerializeField]
    private Transform m_GameContainer;

    [SerializeField]
    private Image m_ImgRechargeDouble;
    [SerializeField]
    private Image m_ImgBoy;
    [SerializeField]
    private Image m_ImgGirl;
    [SerializeField]
    private RawImage m_ImgIcon;
    [SerializeField]
    private Button btn_return;
    public override void OnInit()
    {
        base.OnInit();

    }

    protected override void OnBtnClick(GameObject go)
    {
        base.OnBtnClick(go);
        switch (go.name)
        {
            case "btnCreateRoom":
                DrbComponent.UISystem.OpenFormAsync("UI/Forms/CreateRoomForm", "Form", null);
                break;
            case "btnJoinRoom":
                DrbComponent.UISystem.OpenFormAsync("UI/Forms/JoinRoomForm", "Form", null);
                break;
        }
    }
}
