//===================================================
//Author      : DRB
//CreateTime  ：2021/3/18 5:51:29
//Description ：
//===================================================
using DrbFramework;
using DrbFramework.Http;
using DrbFramework.Utility;
using System.Collections.Generic;
using System.Text;

public static class HttpSystemExtension
{
    public static void EncryptedRequest(this HttpSystem httpSystem, string url, string method, IDictionary<string, object> data, HttpRequestCompleteEventHandler onRequestComplete)
    {
        string stamp = TimeUtil.GetTimestampMS().ToString();
        StringBuilder signContent = new StringBuilder();
        foreach (KeyValuePair<string, object> pair in data)
        {
            string content = pair.Value.ToString();
            signContent.Append(content);
        }
        url = method.StartsWith("/") ? url + method : url + "/" + method + "/" + stamp + "/";
        int index = method.IndexOf('/');
        string sign = EncryptUtil.Md5(System.Text.Encoding.UTF8.GetBytes(string.Format("{0}{1}{2}{3}", method.Substring(index + 1, method.Length - index - 1), signContent.ToString(), stamp, "mj12321jm")));
        data["sign"] = sign;
        httpSystem.Request(url, data, Encoding.UTF8, 1000, onRequestComplete);
    }
}
