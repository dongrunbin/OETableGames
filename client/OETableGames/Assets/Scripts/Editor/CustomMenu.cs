//===================================================
//Author      : DRB
//CreateTime  ：2021/2/25 12:22:45
//Description ：
//===================================================

using DrbFramework.Utility;
using System;
using System.IO;
using UnityEditor;
using UnityEngine;
public class CustomMenu
{
    public const string DEFAULT_AUTHOR_NAME = "DRB";

    public static string AuthorName
    {
        get
        {
            return PlayerPrefs.GetString("AuthorName", DEFAULT_AUTHOR_NAME);
        }
        set
        {
            PlayerPrefs.SetString("AuthorName", value);
        }
    }

    [MenuItem("OETableGames/Setting")]
    public static void OpenSettingTool()
    {
        SettingWindow win = EditorWindow.GetWindow<SettingWindow>();
        win.titleContent.text = "Setting";
        win.minSize = new Vector2(635, 200);
        win.maxSize = new Vector2(635, 300);
        win.Show();
    }

    [MenuItem("OETableGames/Clear Cache")]
    public static void ClearCache()
    {
        PlayerPrefs.DeleteAll();
    }

    [MenuItem("OETableGames/Run Game")]
    public static void RunGame()
    {
        EditorApplication.OpenScene("Assets/Scenes/InitScene.unity");
        EditorApplication.isPlaying = true;
    }

    [MenuItem("OETableGames/Calculate Code Lines")]
    public static void CalculateCount()
    {
        int count = 0;

        string[] path = new string[1] { Application.dataPath + "/Scripts/" };
        CalculateLine(path, ref count);

        EditorWindow.mouseOverWindow.ShowNotification(new GUIContent(string.Format("It is boring to write {0} lines of code", count.ToString())));
    }

    private static void CalculateLine(string[] arrFolder, ref int count)
    {
        if (arrFolder != null && arrFolder.Length > 0)
        {
            foreach (string folderPath in arrFolder)
            {
                if (!Directory.Exists(folderPath)) continue;
                string[] arrFile = Directory.GetFiles(folderPath);
                if (arrFile != null && arrFile.Length > 0)
                {
                    foreach (string filePath in arrFile)
                    {
                        if (filePath.IndexOf(".meta") > -1) continue;
                        string content = IOUtil.GetFileText(filePath);
                        string[] result = content.Split(new string[1] { "\n" }, StringSplitOptions.None);
                        count += result.Length;
                    }
                }

                string[] subFolders = Directory.GetDirectories(folderPath);
                CalculateLine(subFolders, ref count);
            }
        }
    }
}