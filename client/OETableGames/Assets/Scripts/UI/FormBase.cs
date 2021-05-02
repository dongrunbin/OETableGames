//===================================================
//Author      : DRB
//CreateTime  ：2021/3/6 21:16:06
//Description ：
//===================================================
using DrbFramework.Internal;
using System.Collections;
using System.Collections.Generic;
using UnityEngine;
using UnityEngine.UI;

public class FormBase : UGUIForm
{
    [SerializeField]
    private bool m_hasMask;
    [SerializeField]
    private Color m_MaskColor = new Color(0f, 0f, 0f, 0.6f);

    [SerializeField]
    private bool m_MaskCanClose = true;

    private GameObject m_Mask;

    public override void OnInit()
    {
        base.OnInit();

        Button[] btnArr = GetComponentsInChildren<Button>(true);
        for (int i = 0; i < btnArr.Length; i++)
        {
            EventTriggerListener.Get(btnArr[i].gameObject).onClick += OnBtnClick;
        }

        if (m_hasMask)
        {
            m_Mask = new GameObject("Mask(Instance)");
            m_Mask.transform.parent = transform;
            m_Mask.transform.SetAsFirstSibling();
            //m_Mask.transform.SetSiblingIndex(transform.parent.GetSiblingIndex());
            Image mask = m_Mask.AddComponent<Image>();
            mask.color = m_MaskColor;
            if (m_MaskCanClose)
            {
                Button btn = m_Mask.AddComponent<Button>();
                btn.onClick.AddListener(OnMaskClick);
            }
            mask.rectTransform.anchorMax = Vector2.one;
            mask.rectTransform.anchorMin = Vector2.zero;
            mask.rectTransform.sizeDelta = this.GetComponent<RectTransform>().sizeDelta;
        }
    }

    protected virtual void OnBtnClick(GameObject go) 
    {
        if (go.name.Equals("btnClose"))
        {
            DrbComponent.AudioSystem.PlaySoundEffect("btnclose");
            Close();
        }
        else
        {
            DrbComponent.AudioSystem.PlaySoundEffect("btnclick");
        }
    }

    protected virtual void OnMaskClick()
    {
        Close();
    }

    protected virtual void Close()
    {
        DrbComponent.UISystem.CloseForm(this);
    }
}
