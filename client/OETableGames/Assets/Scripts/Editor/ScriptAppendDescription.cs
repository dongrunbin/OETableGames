
using System.IO;
using System;
using UnityEditor;


public class ScriptAppendDescription : UnityEditor.AssetModificationProcessor
{
    private static void OnWillCreateAsset(string path)
    {
        path = path.Replace(".meta", "");
        if (path.EndsWith(".cs"))
        {
            string strContent = File.ReadAllText(path);
            System.Text.StringBuilder strNote1 = new System.Text.StringBuilder();
            strNote1.Append("//===================================================\r\n");
            strNote1.AppendFormat("//Author      : {0}\r\n", CustomMenu.AuthorName);
            strNote1.AppendFormat("//CreateTime  ：{0}\r\n", DateTime.Now.ToString());
            strNote1.Append("//Description ：\r\n");
            strNote1.Append("//===================================================\r\n");
            strNote1.Append(strContent);
            File.WriteAllText(path, strNote1.ToString());
            AssetDatabase.Refresh();
        }
    }
}