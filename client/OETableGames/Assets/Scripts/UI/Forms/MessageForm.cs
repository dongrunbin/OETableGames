//===================================================
//Author      : DRB
//CreateTime  ：2021/3/3 23:05:51
//Description ：
//===================================================

using UnityEngine;
using UnityEngine.UI;
using System;
using DrbFramework.Internal;

public class MessageForm : FormBase
{
    public enum AutoClickType
    {
        None,
        Ok,
        Cancel
    }
    public enum MessageViewType
    {
        None,
        Ok,
        OkAndCancel,
    }

    [SerializeField]
    private Text lblTitle;
    [SerializeField]
    private Text lblMessage;
    [SerializeField]
    private Button btnOk;
    [SerializeField]
    private Button btnCancel;
    [SerializeField]
    private Text m_TextOk;
    [SerializeField]
    private Text m_TextCancel;
    public Action OnOkClickHandler;
    public Action OnCancelHandler;
    private float m_fCountDown;
    private AutoClickType m_AutoType;

    public override void OnUpdate(float elapseSeconds, float realElapseSeconds)
    {
        base.OnUpdate(elapseSeconds, realElapseSeconds);

        if (m_AutoType != AutoClickType.None)
        {
            m_fCountDown -= Time.deltaTime;
            if (m_fCountDown < 0)
            {
                m_TextCancel.text = DrbComponent.LocalizationSystem.GetString("MessageForm.Cancel");
                m_TextOk.text = DrbComponent.LocalizationSystem.GetString("MessageForm.Ok");
                switch (m_AutoType)
                {
                    case AutoClickType.Cancel:
                        OnBtnClick(btnCancel.gameObject);
                        break;
                    case AutoClickType.Ok:
                        OnBtnClick(btnOk.gameObject);
                        break;
                }
            }
            switch (m_AutoType)
            {
                case AutoClickType.Cancel:
                    m_TextCancel.SafeSetText(string.Format("{0}({1})", DrbComponent.LocalizationSystem.GetString("MessageForm.Cancel"), m_fCountDown.ToString("0")));
                    break;
                case AutoClickType.Ok:
                    m_TextOk.SafeSetText(string.Format("{0}({1})", DrbComponent.LocalizationSystem.GetString("MessageForm.Ok"), m_fCountDown.ToString("0")));
                    break;
            }

        }
    }

    protected override void OnBtnClick(GameObject go)
    {
        base.OnBtnClick(go);
        if (go == btnOk.gameObject)
        {
            Close();
            if (OnOkClickHandler != null) OnOkClickHandler();
        }
        else if (go == btnCancel.gameObject)
        {
            Close();
            if (OnCancelHandler != null) OnCancelHandler();
        }
    }

    protected override void Close()
    {
        base.Close();
        m_AutoType = AutoClickType.None;
        m_fCountDown = 0.0f;
        //gameObject.transform.localPosition = new Vector3(0, 5000, 0);
    }

    public void Show(string title, string message, float countDown = 0f, AutoClickType autoType = AutoClickType.None, MessageViewType type = MessageViewType.Ok, Action okAction = null, Action cancelAction = null)
    {
        //gameObject.transform.localPosition = Vector3.zero;
        lblTitle.text = title;
        lblMessage.text = message;

        switch (type)
        {
            case MessageViewType.Ok:
                btnOk.transform.localPosition = new Vector3(0, btnOk.transform.localPosition.y, 0);
                btnCancel.gameObject.SetActive(false);
                btnOk.gameObject.SetActive(true);
                break;
            case MessageViewType.OkAndCancel:
                btnOk.transform.localPosition = new Vector3(-138, btnOk.transform.localPosition.y, 0);
                btnCancel.gameObject.SetActive(true);
                btnOk.gameObject.SetActive(true);
                break;
            case MessageViewType.None:
                btnCancel.gameObject.SetActive(false);
                btnOk.gameObject.SetActive(false);
                break;
        }
        m_AutoType = autoType;
        m_fCountDown = countDown;

        m_TextOk.SafeSetText(DrbComponent.LocalizationSystem.GetString("MessageForm.Ok"));
        m_TextCancel.SafeSetText(DrbComponent.LocalizationSystem.GetString("MessageForm.Cancel"));

        OnOkClickHandler = okAction;
        OnCancelHandler = cancelAction;
    }
}
