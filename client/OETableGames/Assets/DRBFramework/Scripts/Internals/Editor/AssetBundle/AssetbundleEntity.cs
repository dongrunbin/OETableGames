//===================================================
//Author      : DRB
//CreateTime  ：2021/5/6 23:35:40
//Description ：
//===================================================
using System.Collections.Generic;

public class AssetbundleEntity
{
    public string Key { get; set; }

    public string Name { get; set; }

    public string Game { get; set; }

    public string Tag { get; set; }

    public bool IsFolder { get; set; }

    public bool IsFirstData { get; set; }

    /// <summary>
    /// 是否被选中
    /// </summary>
    public bool IsChecked { get; set; }

    public List<string> PathList = new List<string>();
}
