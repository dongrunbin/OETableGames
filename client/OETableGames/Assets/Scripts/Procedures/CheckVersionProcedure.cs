//===================================================
//Author      : DRB
//CreateTime  ：2021/2/25 12:49:29
//Description ：
//===================================================
using DrbFramework;
using DrbFramework.Download;
using DrbFramework.Extensions;
using DrbFramework.Internal;
using DrbFramework.Procedure;
using DrbFramework.Utility;
using System;
using System.Collections.Generic;

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

    private const string VERSION_FILE_NAME= "VersionInfo.txt";
    private const string DOWNLOAD_URL = "";

    public override void OnEnter(object userData)
    {
        base.OnEnter(userData);
        DrbComponent.DownloadSystem.OnDownloadSuccess += OnDownloadSuccess;
        DrbComponent.DownloadSystem.OnDownloadUpdate += OnDownloadUpdate;
        DrbComponent.DownloadSystem.OnDownloadFailure += OnDownloadFailure;

        m_InitForm = (InitForm)DrbComponent.UISystem.OpenInternalForm("UI/Forms/InitForm", "BackGround");

        CheckResources();
    }

    public override void OnLeave()
    {
        base.OnLeave();
        DrbComponent.DownloadSystem.OnDownloadSuccess -= OnDownloadSuccess;
        DrbComponent.DownloadSystem.OnDownloadUpdate -= OnDownloadUpdate;
        DrbComponent.DownloadSystem.OnDownloadFailure -= OnDownloadFailure;

        DrbComponent.UISystem.DestroyForm(m_InitForm);
    }

    /// <summary>
    /// 检查资源更新
    /// </summary>
    private void CheckResources()
    {
#if UNITY_EDITOR && DISABLE_ASSETBUNDLE
        ChangeState<LoginProcedure>();
#else
        string versionPath = DrbComponent.ResourceSystem.PersistentPath + VERSION_FILE_NAME;
        DrbComponent.DownloadSystem.Download(DOWNLOAD_URL + VERSION_FILE_NAME, string.Empty, versionPath);
#endif

    }

    private void OnDownloadSuccess(object sender, DownloadSuccessEventArgs e)
    {
        if (e.UserData != null)
        {
            if (e.Data == null || e.Data.Length == 0)
            {
                Log.Warn("server has no version file.");

                return;
            }

            List<DownloadDataEntity> serverList = PackDownloadData(e.Data.ToString());
            DownloadResources(e.UserData.ToString(), serverList);
        }


    }

    private void OnDownloadUpdate(object sender, DownloadUpdateEventArgs e)
    {
        if (e.UserData != null)
            return;

        if (m_InitForm != null)
        {
            m_InitForm.SetUI(m_CurrentCount, m_TotalCount, m_CurrentSize, m_TotalSize);
        }

        if (m_CurrentCount == m_TotalCount)
        {
            
        }
    }

    private void OnDownloadFailure(object sender, DownloadFailureEventArgs e)
    {
        //DrbComponent.UISystem.ShowMessage("Tip", "download file failure.", MessageViewType.Ok, CheckResources);
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
        //如果本地有版本文件
        if (IOUtil.FileExists(versionPath))
        {
            Log.Info("exists local version file.");
            string content = IOUtil.GetFileText(versionPath);
            Dictionary<string, string> localDic = PackDownloadDataDic(content);
            List<DownloadDataEntity> localList = PackDownloadData(content);

            //更新资源
            for (int i = 0; i < serverList.Count; ++i)
            {
                if (!localDic.ContainsKey(serverList[i].FullName.Trim()))
                {
                    if (serverList[i].IsFirstData)
                    {
                        needDownloadDataList.Add(serverList[i]);
                    }
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
                if (serverList[i].IsFirstData)
                {
                    needDownloadDataList.Add(serverList[i]);
                }
            }
        }
        Log.Info(string.Format("need to download {0} files.", needDownloadDataList.Count.ToString()));
        if (needDownloadDataList.Count == 0)
        {
            //m_OnDownloadComplete(this, null);
            return;
        }
        for (int i = 0; i < needDownloadDataList.Count; ++i)
        {
            DrbComponent.DownloadSystem.Download(DOWNLOAD_URL + needDownloadDataList[i].FullName, DrbComponent.ResourceSystem.PersistentPath + needDownloadDataList[i].FullName);
        }
    }
}
