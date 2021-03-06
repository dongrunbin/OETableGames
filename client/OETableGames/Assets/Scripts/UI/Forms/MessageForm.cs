//===================================================
//Author      : DRB
//CreateTime  ：2021/3/3 23:05:51
//Description ：
//===================================================

using UnityEngine;
using System.Collections;
using UnityEngine.UI;
using System;
using DrbFramework.Internal;

/// <summary>
/// 消息提示窗口视图
/// </summary>
public class MessageForm : UGUIForm
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

    public override void OnInit()
    {
        base.OnInit();
        EventTriggerListener.Get(btnOk.gameObject).onClick = BtnOkClickCallBack;
        EventTriggerListener.Get(btnCancel.gameObject).onClick = BtnCancelClickCallBack;
    }

    public override void OnUpdate(float elapseSeconds, float realElapseSeconds)
    {
        base.OnUpdate(elapseSeconds, realElapseSeconds);

        if (m_AutoType != AutoClickType.None)
        {
            m_fCountDown -= Time.deltaTime;
            if (m_fCountDown < 0)
            {
                m_TextCancel.text = "取消";
                m_TextOk.text = "确定";
                switch (m_AutoType)
                {
                    case AutoClickType.Cancel:
                        BtnCancelClickCallBack(btnCancel.gameObject);
                        break;
                    case AutoClickType.Ok:
                        BtnOkClickCallBack(btnOk.gameObject);
                        break;
                }
            }
            switch (m_AutoType)
            {
                case AutoClickType.Cancel:
                    m_TextCancel.SafeSetText(string.Format("取消({0})", m_fCountDown.ToString("0")));
                    break;
                case AutoClickType.Ok:
                    m_TextOk.SafeSetText(string.Format("确定({0})", m_fCountDown.ToString("0")));
                    break;
            }

        }
    }

    private void BtnOkClickCallBack(GameObject go)
    {
        //DrbComponent.AudioSystem.PlayAudio("btnclick", Vector3.zero, false);
        Close();
        if (OnOkClickHandler != null) OnOkClickHandler();
    }

    private void BtnCancelClickCallBack(GameObject go)
    {
        //DrbComponent.AudioSystem.PlayAudio("btnclose", Vector3.zero, false);
        Close();
        if (OnCancelHandler != null) OnCancelHandler();
    }

    private void Close()
    {
        m_AutoType = AutoClickType.None;
        m_fCountDown = 0.0f;
        gameObject.transform.localPosition = new Vector3(0, 5000, 0);
    }

    public void Show(string title, string message, float countDown = 0f, AutoClickType autoType = AutoClickType.None, MessageViewType type = MessageViewType.Ok, Action okAction = null, Action cancelAction = null)
    {
        gameObject.transform.localPosition = Vector3.zero;
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
        switch (autoType)
        {
            case AutoClickType.None:
                m_TextOk.SafeSetText("确定");
                m_TextCancel.SafeSetText("取消");
                break;
            case AutoClickType.Cancel:
                m_TextOk.SafeSetText("确定");
                break;
            case AutoClickType.Ok:
                m_TextCancel.SafeSetText("取消");
                break;
        }

        OnOkClickHandler = okAction;
        OnCancelHandler = cancelAction;
    }
}
