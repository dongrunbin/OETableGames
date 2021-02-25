//===================================================
//Author      : DRB
//CreateTime  ：2021/2/25 12:34:25
//Description ：
//===================================================
using UnityEngine;
using System.Collections;
using UnityEditor;
using System.Collections.Generic;

public class SettingWindow : EditorWindow
{
    private GUIStyle m_Style = new GUIStyle();

    private void OnEnable()
    {
        m_Style.alignment = TextAnchor.MiddleCenter;
    }

    private void OnGUI()
    {
        GUILayout.BeginHorizontal();
        GUILayout.Label("Developer Name", m_Style, GUILayout.Width(100));
        GUILayout.Space(10);
        string author = GUILayout.TextField(CustomMenu.AuthorName);
        if (!author.Equals(CustomMenu.AuthorName))
        {
            CustomMenu.AuthorName = author;
        }
        GUILayout.EndHorizontal();
    }
}
