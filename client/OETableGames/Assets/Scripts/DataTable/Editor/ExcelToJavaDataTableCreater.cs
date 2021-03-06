//===================================================
//Author      : DRB
//CreateTime  ：2021/4/6 0:28:31
//Description ：
//===================================================

using DrbFramework.DataTable;
using Excel;
using System;
using System.Data;
using System.IO;
using System.Text;

public class ExcelToJavaDataTableCreater : IDataTableCreater
{
    public void CreateDataTable(string dataSourcePath, string outputPath)
    {
        if (string.IsNullOrEmpty(dataSourcePath)) return;
        if (!File.Exists(dataSourcePath)) return;

        FileStream stream = File.Open(dataSourcePath, FileMode.Open, FileAccess.Read);
        IExcelDataReader excelReader = ExcelReaderFactory.CreateOpenXmlReader(stream);

        DataSet result = excelReader.AsDataSet();

        string fileName = Path.GetFileNameWithoutExtension(dataSourcePath);
        int columns = result.Tables[0].Columns.Count;

        for (int i = 0; i < columns; ++i)
        {
            if (string.IsNullOrEmpty(result.Tables[0].Rows[0][i].ToString().Trim()))
            {
                columns = i;
                break;
            }
        }

        string[,] dataArr = new string[columns, 3];
        for (int i = 0; i < 3; ++i)
        {
            for (int j = 0; j < columns; j++)
            {
                dataArr[j, i] = result.Tables[0].Rows[i][j].ToString().Trim();
                if (i == 0)
                {
                    dataArr[j, i] = dataArr[j, i].ToLower()[0] + dataArr[j, i].Substring(1, dataArr[j, i].Length - 1);
                }
            }
        }
        CreateJavaEntity(outputPath, fileName, dataArr);
        CreateJavaDBModel(outputPath, fileName, dataArr);
    }

    private void CreateJavaEntity(string filePath, string fileName, string[,] dataArr)
    {
        if (dataArr == null) return;

        StringBuilder sbr = new StringBuilder();
        sbr.Append("\r\n");
        sbr.Append("//===================================================\r\n");
        sbr.Append("//Author：DRB\r\n");
        sbr.AppendFormat("//CreateTime：{0}\r\n", DateTime.Now.ToString());
        sbr.Append("//Remark：This code is generated by the tool\r\n");
        sbr.Append("//===================================================\r\n");
        sbr.Append("package com.oegame.tablegames.model.local.gen;\r\n");
        sbr.Append("import com.oegame.tablegames.model.local.*;\r\n");
        sbr.Append("\r\n");
        sbr.AppendFormat("// {0}实体\r\n", fileName);
        sbr.AppendFormat("public class {0}Entity extends AbstractEntity\r\n", fileName);
        sbr.Append("{\r\n");

        for (int i = 0; i < dataArr.GetLength(0); i++)
        {
            if (i == 0) continue;
            sbr.AppendFormat("    // {0}\r\n", dataArr[i, 2]);
            sbr.AppendFormat("    public {0} {1};\r\n", ChangeJavaType(dataArr[i, 1]), dataArr[i, 0]);
            sbr.Append("\r\n");
        }

        sbr.Append("}\r\n");

        string path = filePath + "/java";
        if (!Directory.Exists(path))
        {
            Directory.CreateDirectory(path);
        }
        using (FileStream fs = new FileStream(string.Format("{0}/{1}Entity.java", path, fileName), FileMode.Create))
        {
            using (StreamWriter sw = new StreamWriter(fs))
            {
                sw.Write(sbr.ToString());
            }
        }
    }

    private void CreateJavaDBModel(string filePath, string fileName, string[,] dataArr)
    {
        if (dataArr == null) return;

        StringBuilder sbr = new StringBuilder();
        sbr.Append("\r\n");
        sbr.Append("//===================================================\r\n");
        sbr.Append("//Author：DRB\r\n");
        sbr.AppendFormat("//CreateTime：{0}\r\n", DateTime.Now.ToString());
        sbr.Append("//Remark：This code is generated by the tool\r\n");
        sbr.Append("//===================================================\r\n");
        sbr.Append("package com.oegame.tablegames.model.local.gen;\r\n");
        sbr.Append("import com.oegame.tablegames.common.util.StringUtil;\r\n");
        sbr.Append("import com.oegame.tablegames.model.local.*;\r\n");
        sbr.Append("\r\n");
        sbr.AppendFormat("// {0}数据管理\r\n", fileName);
        sbr.AppendFormat("public class {0}DBModel extends AbstractDBModel<{0}Entity>\r\n", fileName);
        sbr.Append("{\r\n");
        sbr.AppendFormat("    private static {0}DBModel dbModel = new {0}DBModel();\r\n", fileName);
        sbr.AppendFormat("    public static {0}DBModel singleton()\r\n", fileName);
        sbr.Append("    {\r\n");
        sbr.Append("        return dbModel;\r\n");
        sbr.Append("    }\r\n");
        sbr.AppendFormat("    private {0}DBModel() {{ }}\r\n", fileName);
        sbr.Append("    // 文件名称\r\n");
        sbr.AppendFormat("    protected String getFileName() {{return \"{0}.drb\";}}\r\n", fileName);
        sbr.Append("\r\n");
        sbr.Append("    /// 创建实体\r\n");
        sbr.AppendFormat("    protected {0}Entity makeEntity(LocalDataParser parse)\r\n", fileName);
        sbr.Append("    {\r\n");
        sbr.AppendFormat("        {0}Entity entity = new {0}Entity();\r\n", fileName);

        for (int i = 0; i < dataArr.GetLength(0); i++)
        {
            sbr.AppendFormat("        entity.{0} = {1}(parse.getFieldValue(\"{0}\"));\r\n", dataArr[i, 0], ChangeJavaTypeName(dataArr[i, 1]));
        }
        sbr.Append("        return entity;\r\n");
        sbr.Append("    }\r\n");
        sbr.Append("}\r\n");

        using (FileStream fs = new FileStream(string.Format("{0}/java/{1}DBModel.java", filePath, fileName), FileMode.Create))
        {
            using (StreamWriter sw = new StreamWriter(fs))
            {
                sw.Write(sbr.ToString());
            }
        }
    }

    private string ChangeJavaTypeName(string type)
    {
        string str = string.Empty;

        switch (type)
        {
            case "int":
                str = "StringUtil.toInt";
                break;
            case "long":
                str = "StringUtil.toLong";
                break;
            case "float":
                str = "StringUtil.toFloat";
                break;
            case "bool":
                str = "StringUtil.toBoolean";
                break;
        }

        return str;
    }

    private string ChangeJavaType(string type)
    {
        switch (type)
        {
            case "string":
                type = "String";
                break;
            case "bool":
                type = "boolean";
                break;
        }

        return type;
    }
}
