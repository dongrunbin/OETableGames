//===================================================
//Author      : DRB
//CreateTime  ：2021/4/6 4:18:22
//Description ：
//===================================================
using DrbFramework.Internal;
using System.Collections.Generic;
using System.Reflection;

public class RoomSetting
{
    private string SettingStr = "";
    public int cost = 0;
    public int loop = 8;
    public int player = 4;
    public int baseScore = 1;
    public bool isHu;
    public bool isPeng;


    public void Init(List<int> setting)
    {
        if (setting == null) return;

        Dictionary<string, string> dic = new Dictionary<string, string>();

        for (int i = 0; i < setting.Count; ++i)
        {
            MahjongSettingsDataEntity entity = DrbComponent.DataTableSystem.GetDataTable<MahjongSettingsDataEntity>().GetEntity(setting[i]);
            if (entity == null) continue;
            FieldInfo filed = GetType().GetField(entity.Tags);
            if (filed != null)
            {
                if (filed.FieldType == typeof(bool))
                {
                    filed.SetValue(this, entity.Value == 0 ? false : true);
                }
                else
                {
                    filed.SetValue(this, entity.Value);
                }
            }

            if (!dic.ContainsKey(entity.Label))
            {
                dic[entity.Label] = entity.Name;
            }
            else
            {
                dic[entity.Label] += "," + entity.Name;
            }
        }
        SettingStr = string.Empty;
        int index = 0;
        foreach (KeyValuePair<string, string> pair in dic)
        {
            SettingStr += pair.Key + ":" + pair.Value;
            if (index < dic.Count - 1)
            {
                SettingStr += "\n";
            }
            ++index;
        }
    }


    public override string ToString()
    {
        return SettingStr;
    }
}
