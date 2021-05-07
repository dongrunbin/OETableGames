//===================================================
//Author      : DRB
//CreateTime  ：2021/2/25 12:49:29
//Description ：
//===================================================
using DrbFramework;
using DrbFramework.Download;
using DrbFramework.Extensions;
using DrbFramework.Http;
using DrbFramework.Internal;
using DrbFramework.Procedure;
using DrbFramework.Utility;
using System;
using System.Collections.Generic;
using System.Text;
using UnityEngine;

public class CheckVersionProcedure : Procedure
{
    private class DownloadDataEntity
    {
        public string FullName;

        public string MD5;

        public int Size;

        public bool IsFirstData;
    }

    private InitForm m_InitForm;
    private int m_CurrentCount, m_TotalCount, m_CurrentSize, m_TotalSize;

    private const string VERSION_FILE_NAME = "VersionInfo.txt";
    private string m_DownloadUrl;
    private string m_Platform;

    private List<DownloadDataEntity> m_LocalList;
    private string m_LocalVersionFilePath;

    public override void OnEnter(object userData)
    {
        base.OnEnter(userData);

#if UNITY_EDITOR || UNITY_STANDALONE_WIN
        m_Platform = "StandaloneWindows";
#elif UNITY_ANDROID
        m_Platform = "Android";
#elif UNITY_IPHONE
        m_Platform = "IOS";
#else

#endif

        DrbComponent.DownloadSystem.OnDownloadSuccess += OnDownloadSuccess;
        DrbComponent.DownloadSystem.OnDownloadFailure += OnDownloadFailure;

        m_InitForm = (InitForm)DrbComponent.UISystem.OpenInternalForm("UI/Forms/InitForm", "BackGround");

        RequestDownloadURL();
    }

    public override void OnLeave()
    {
        base.OnLeave();
        DrbComponent.DownloadSystem.OnDownloadSuccess -= OnDownloadSuccess;
        DrbComponent.DownloadSystem.OnDownloadFailure -= OnDownloadFailure;

        //DrbComponent.UISystem.DestroyForm(m_InitForm);
    }

    private void RequestDownloadURL()
    {
        Dictionary<string, object> dic = new Dictionary<string, object>();
#if UNITY_EDITOR || UNITY_STANDALONE_WIN
        dic["platform"] = "windows";
#elif UNITY_ANDROID
        dic["platform"] = "android";
#elif UNITY_IPHONE
        dic["platform"] = "ios";
#endif
        DrbComponent.HttpSystem.EncryptedRequest(ConstDefine.WebUrl, "game/init", dic, OnRequestDownloadURLCallBack);
    }

    private void OnRequestDownloadURLCallBack(object sender, HttpRequestCompleteEventArgs args)
    {
        if (args.HasError)
        {
            DrbComponent.UISystem.ShowMessage("Error", "Connected fail", type: MessageForm.MessageViewType.OkAndCancel, okAction: RequestDownloadURL, cancelAction: Application.Quit);
        }
        else
        {
            LitJson.JsonData jsonData = LitJson.JsonMapper.ToObject(Encoding.UTF8.GetString(args.Data));
            if (jsonData["code"].ToString().ToInt() < 0)
            {
                DrbComponent.UISystem.ShowMessage("Error", jsonData["msg"].ToString(), type: MessageForm.MessageViewType.OkAndCancel, okAction: RequestDownloadURL, cancelAction: Application.Quit);
                return;
            }

            m_DownloadUrl = jsonData["data"]["downloadUrl"].ToString();

            CheckResources();
        }
    }

    private void CheckResources()
    {
#if !ASSETBUNDLE
        ChangeState<PreloadProcedure>();
#else
        m_LocalVersionFilePath = StringUtil.CombinePath(DrbComponent.ResourceSystem.PersistentPath, VERSION_FILE_NAME);
        DrbComponent.DownloadSystem.Download(m_DownloadUrl + m_Platform + "/" + VERSION_FILE_NAME, string.Empty);
#endif

    }

    private void OnDownloadSuccess(object sender, DownloadSuccessEventArgs e)
    {
        if (e.UserData == null)
        {
            if (e.Data == null || e.Data.Length == 0)
            {
                Log.Warn("server has no version file.");

                return;
            }

            List<DownloadDataEntity> serverList = PackDownloadData(System.Text.Encoding.UTF8.GetString(e.Data));
            DownloadResources(m_LocalVersionFilePath, serverList);
        }
        else
        {
            ++m_CurrentCount;
            m_CurrentSize += e.Data.Length;
            if (m_InitForm != null)
            {
                m_InitForm.SetUI(m_CurrentCount, m_TotalCount, m_CurrentSize / 1024, m_TotalSize / 1024);
            }

            ModifyLocalData((DownloadDataEntity)e.UserData);

            if (m_CurrentCount == m_TotalCount)
            {
                ChangeState<PreloadProcedure>();
            }
        }
    }

    private void OnDownloadFailure(object sender, DownloadFailureEventArgs e)
    {
        DrbComponent.UISystem.ShowMessage("Tip", "download file failure.", type: MessageForm.MessageViewType.Ok, okAction: CheckResources);
    }

    private List<DownloadDataEntity> PackDownloadData(string content)
    {
        List<DownloadDataEntity> lst = new List<DownloadDataEntity>();

        string[] arrLines = content.Split('\n');
        for (int i = 0; i < arrLines.Length; ++i)
        {
            string[] arrData = arrLines[i].Split(';');
            if (arrData.Length == 4)
            {
                DownloadDataEntity entity = new DownloadDataEntity()
                {
                    FullName = arrData[0],
                    MD5 = arrData[1],
                    Size = arrData[2].ToInt(),
                    IsFirstData = arrData[3].ToBool()
                };
                lst.Add(entity);

            }
        }
        return lst;
    }

    private Dictionary<string, string> PackDownloadDataDic(List<DownloadDataEntity> lst)
    {
        Dictionary<string, string> dic = new Dictionary<string, string>();

        for (int i = 0; i < lst.Count; ++i)
        {
            dic[lst[i].FullName] = lst[i].MD5;
        }

        return dic;
    }

    private Dictionary<string, string> PackDownloadDataDic(string content)
    {
        if (string.IsNullOrEmpty(content)) return null;
        Dictionary<string, string> dic = new Dictionary<string, string>();

        string[] arrLines = content.Split('\n');
        for (int i = 0; i < arrLines.Length; ++i)
        {
            string[] arrData = arrLines[i].Split(';');
            if (arrData.Length == 4)
            {
                string FullName = arrData[0];
                string MD5 = arrData[1];
                dic[FullName] = MD5;
            }
        }
        return dic;
    }

    private void DownloadResources(string versionPath, List<DownloadDataEntity> serverList)
    {
        List<DownloadDataEntity> needDownloadDataList = new List<DownloadDataEntity>();
        if (IOUtil.FileExists(versionPath))
        {
            Log.Info("exists local version file.");
            string content = IOUtil.GetFileText(versionPath);
            Dictionary<string, string> localDic = PackDownloadDataDic(content);
            m_LocalList = PackDownloadData(content);

            for (int i = 0; i < serverList.Count; ++i)
            {
                if (!localDic.ContainsKey(serverList[i].FullName.Trim()))
                {
                    //if (serverList[i].IsFirstData)
                    //{
                    needDownloadDataList.Add(serverList[i]);
                    //}
                    continue;
                }
                if (localDic[serverList[i].FullName.Trim()] != serverList[i].MD5)
                {
                    needDownloadDataList.Add(serverList[i]);
                }
            }
        }
        else
        {
            Log.Info("no exists local version file.");
            for (int i = 0; i < serverList.Count; ++i)
            {
                //if (serverList[i].IsFirstData)
                //{
                needDownloadDataList.Add(serverList[i]);
                //}
            }
        }
        Log.Info(string.Format("need to download {0} files.", needDownloadDataList.Count.ToString()));
        if (needDownloadDataList.Count == 0)
        {
            ChangeState<PreloadProcedure>();
            return;
        }
        m_TotalCount = needDownloadDataList.Count;
        for (int i = 0; i < needDownloadDataList.Count; ++i)
        {
            DrbComponent.DownloadSystem.Download(m_DownloadUrl + m_Platform + "/" + needDownloadDataList[i].FullName, StringUtil.CombinePath(DrbComponent.ResourceSystem.PersistentPath, needDownloadDataList[i].FullName), needDownloadDataList[i]);
            m_TotalSize += needDownloadDataList[i].Size;
        }
    }

    private void ModifyLocalData(DownloadDataEntity entity)
    {
        if (m_LocalList == null)
        {
            m_LocalList = new List<DownloadDataEntity>();
        }
        bool isExists = false;
        for (int i = 0; i < m_LocalList.Count; ++i)
        {
            if (m_LocalList[i].FullName.Equals(entity.FullName, StringComparison.CurrentCultureIgnoreCase))
            {
                m_LocalList[i].MD5 = entity.MD5;
                m_LocalList[i].Size = entity.Size;
                m_LocalList[i].IsFirstData = entity.IsFirstData;
                isExists = true;
                break;
            }
        }

        if (!isExists)
        {
            m_LocalList.Add(entity);
        }

        SaveLocalVersion();
    }

    private void SaveLocalVersion()
    {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < m_LocalList.Count; ++i)
        {
            sb.AppendLine(string.Format("{0};{1};{2};{3}", m_LocalList[i].FullName, m_LocalList[i].MD5, m_LocalList[i].Size, m_LocalList[i].IsFirstData ? 1 : 0));
        }

        IOUtil.CreateTextFile(m_LocalVersionFilePath, sb.ToString());
    }
}
