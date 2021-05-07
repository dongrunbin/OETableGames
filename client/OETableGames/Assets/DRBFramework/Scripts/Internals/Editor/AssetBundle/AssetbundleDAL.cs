//===================================================
//Author      : DRB
//CreateTime  ：2021/5/6 23:36:54
//Description ：
//===================================================
using DrbFramework.Utility;
using System.Collections.Generic;
using System.Xml.Linq;

public class AssetbundleDAL
{
    private string m_Path;

    private List<AssetbundleEntity> m_List;

    public AssetbundleDAL(string path)
    {
        m_Path = path;
        m_List = new List<AssetbundleEntity>();
    }

    public List<AssetbundleEntity> GetList()
    {
        m_List.Clear();

        if (!IOUtil.FileExists(m_Path)) return null;

        XDocument xDoc = XDocument.Load(m_Path);

        XElement root = xDoc.Root;

        XElement assetBundleNode = root.Element("AssetBundle");

        IEnumerable<XElement> lst = assetBundleNode.Elements("Item");

        int index = 0;
        foreach (XElement item in lst)
        {
            AssetbundleEntity entity = new AssetbundleEntity();
            entity.Key = "key" + ++index;
            entity.Name = item.Attribute("Name").Value;
            entity.Game = item.Attribute("Game").Value;
            entity.Tag = item.Attribute("Tag").Value;
            entity.IsFolder = item.Attribute("IsFolder").Value.Equals("True", System.StringComparison.CurrentCultureIgnoreCase);
            entity.IsFirstData = item.Attribute("IsFirstData").Value.Equals("True", System.StringComparison.CurrentCultureIgnoreCase);

            IEnumerable<XElement> pathList = item.Elements("Path");
            foreach (XElement path in pathList)
            {
                entity.PathList.Add(path.Attribute("Value").Value);
            }

            m_List.Add(entity);
        }
        return m_List;
    }
}
