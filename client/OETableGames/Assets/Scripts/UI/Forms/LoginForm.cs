//===================================================
//Author      : DRB
//CreateTime  ：2021/3/3 22:22:54
//Description ：
//===================================================

using DrbFramework.Extensions;
using DrbFramework.Http;
using DrbFramework.Internal;
using System.Collections.Generic;
using System.Text;
using UnityEngine;
using UnityEngine.UI;
using DrbFramework;
using System;

public class LoginForm : FormBase
{
    [SerializeField]
    private Button m_BtnGuestLogin;

    public Action OnGuestLoginClick;

    protected override void OnBtnClick(GameObject go)
    {
        base.OnBtnClick(go);
        if (go == m_BtnGuestLogin.gameObject)
        {
            if (OnGuestLoginClick != null)
            {
                OnGuestLoginClick();
            }
        }
    }
}
