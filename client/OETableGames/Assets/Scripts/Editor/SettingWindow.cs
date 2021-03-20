//===================================================
//Author      : DRB
//CreateTime  ：2021/3/19 18:59:38
//Description ：
//===================================================
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
    private class MacroItem
    {
        public string Name { get; set; }
        public string DisplayName { get; set; }

        public string Group { get; set; }

        public MacroItem(string name, string displayName)
        {
            Name = name;
            DisplayName = displayName;
        }

        public MacroItem(string name, string displayName, string group)
        {
            Name = name;
            DisplayName = displayName;
            Group = group;
        }
    }

    private class MacroGroup
    {
        public int Index { get; set; }

        public List<MacroItem> Items { get; set; }


        private string[] m_Content;
        public string[] Content
        {
            get
            {
                if (m_Content == null)
                {
                    if (Items == null) return null;
                    List<string> content = new List<string>();
                    for (int i = 0; i < Items.Count; ++i)
                    {
                        content.Add(Items[i].DisplayName);
                    }
                    m_Content = content.ToArray();
                }
                return m_Content;
            }
        }

        public MacroGroup()
        {
            Index = 0;
            Items = new List<MacroItem>();
        }
    }
    private GUIStyle m_Style = new GUIStyle();

    private List<MacroItem> m_ListMacroItem = new List<MacroItem>();
    private string m_strMacro = null;
    private Dictionary<string, bool> m_Dic = new Dictionary<string, bool>();
    private Dictionary<string, MacroGroup> m_DicMacroItem = new Dictionary<string, MacroGroup>();

    private void OnEnable()
    {
        m_Style.alignment = TextAnchor.MiddleCenter;

        m_strMacro = PlayerSettings.GetScriptingDefineSymbolsForGroup(BuildTargetGroup.Standalone);
        Debug.Log(m_strMacro);

        m_ListMacroItem.Clear();
        m_ListMacroItem.Add(new MacroItem("MODE_DEBUG", "Debug", "Environment"));
        m_ListMacroItem.Add(new MacroItem("MODE_TEST", "Test", "Environment"));
        m_ListMacroItem.Add(new MacroItem("MODE_PRODUCTION", "Production", "Environment"));
        m_ListMacroItem.Add(new MacroItem("ASSETBUNDLE", "AssetBundle"));

        for (int i = 0; i < m_ListMacroItem.Count; ++i)
        {
            m_Dic[m_ListMacroItem[i].Name] = false;
            if (!string.IsNullOrEmpty(m_ListMacroItem[i].Group))
            {
                if (!m_DicMacroItem.ContainsKey(m_ListMacroItem[i].Group))
                {
                    m_DicMacroItem[m_ListMacroItem[i].Group] = new MacroGroup();
                }
                m_DicMacroItem[m_ListMacroItem[i].Group].Items.Add(m_ListMacroItem[i]);
            }
        }
        string[] macro = m_strMacro.Split(';');
        for (int j = 0; j < macro.Length; ++j)
        {
            for (int i = 0; i < m_ListMacroItem.Count; ++i)
            {
                if (macro[j].Equals(m_ListMacroItem[i].Name))
                {
                    m_Dic[m_ListMacroItem[i].Name] = true;
                }
            }
            foreach (var pair in m_DicMacroItem)
            {
                for (int k = 0; k < pair.Value.Items.Count; ++k)
                {
                    if (macro[j].Equals(pair.Value.Items[k].Name))
                    {
                        pair.Value.Index = k;
                        break;
                    }
                }
            }
        }
    }

    private void OnGUI()
    {
        GUILayout.BeginHorizontal();
        GUILayout.Label("Developer Name:", m_Style, GUILayout.Width(100));
        GUILayout.Space(10);
        string author = GUILayout.TextField(CustomMenu.AuthorName, GUILayout.Width(200));
        if (!author.Equals(CustomMenu.AuthorName))
        {
            CustomMenu.AuthorName = author;
        }
        GUILayout.EndHorizontal();
        GUILayout.Space(10);
        GUILayout.Label("Macro Setting:", GUILayout.Width(200));
        GUILayout.BeginVertical();
        for (int i = 0; i < m_ListMacroItem.Count; ++i)
        {
            if (!string.IsNullOrEmpty(m_ListMacroItem[i].Group)) continue;
            m_Dic[m_ListMacroItem[i].Name] = GUILayout.Toggle(m_Dic[m_ListMacroItem[i].Name], m_ListMacroItem[i].DisplayName, GUILayout.Width(200));
        }
        foreach (var pair in m_DicMacroItem)
        {
            m_DicMacroItem[pair.Key].Index = EditorGUILayout.Popup(pair.Key, pair.Value.Index, pair.Value.Content);
            for (int i = 0; i < pair.Value.Items.Count; ++i)
            {
                m_Dic[pair.Value.Items[i].Name] = i == pair.Value.Index;
            }
        }


        GUILayout.BeginHorizontal();
        if (GUILayout.Button("Save", GUILayout.Width(200)))
        {
            SaveMacro();
        }
        GUILayout.EndHorizontal();
    }

    private void SaveMacro()
    {
        m_strMacro = string.Empty;

        foreach (var item in m_Dic)
        {
            if (item.Value)
            {
                m_strMacro += string.Format("{0};", item.Key);
            }
            if (item.Key.Equals("DISABLE_ASSETBUNDLE"))
            {
                EditorBuildSettingsScene[] scenes = EditorBuildSettings.scenes;
                for (int i = 0; i < scenes.Length; ++i)
                {
                    if (scenes[i].path.IndexOf("Downloads/", System.StringComparison.CurrentCultureIgnoreCase) > -1)
                    {
                        if (scenes[i].enabled)
                        {
                            scenes[i].enabled = item.Value;
                        }
                    }
                }
                EditorBuildSettings.scenes = scenes;
            }
        }
        PlayerSettings.SetScriptingDefineSymbolsForGroup(BuildTargetGroup.Standalone, m_strMacro);
        PlayerSettings.SetScriptingDefineSymbolsForGroup(BuildTargetGroup.iOS, m_strMacro);
        PlayerSettings.SetScriptingDefineSymbolsForGroup(BuildTargetGroup.Android, m_strMacro);
    }
}
