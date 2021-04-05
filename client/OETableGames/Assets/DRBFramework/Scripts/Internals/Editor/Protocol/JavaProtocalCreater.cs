
using System;
using System.Collections.Generic;
using System.IO;
using System.Linq;
using System.Text;

namespace DrbFramework.Internal.Editor
{
    public class JavaProtocalCreater : IProtocolCreater
    {
        public void CreateProtocol(Menu menu, Protocol protocol, string outputPath)
        {
            List<Field> lst = protocol.FieldInfos;
            List<string> existsCustom = new List<string>();

            StringBuilder sbr = new StringBuilder();

            string protoName = string.Format("{0}_{1}Proto", menu.Name, protocol.EnName);

            List<Field> baseList = new List<Field>();
            for (int i = 0; i < lst.Count; ++i)
            {
                if (string.IsNullOrEmpty(lst[i].AttachToCustom))
                {
                    baseList.Add(lst[i]);
                }
            }

            //生成头部信息
            sbr.AppendFormat("//===================================================\r\n");
            sbr.AppendFormat("//作    者：DRB\r\n");
            sbr.AppendFormat("//创建时间：{0}\r\n", DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss"));
            sbr.AppendFormat("//备    注：\r\n");
            sbr.AppendFormat("//===================================================\r\n");
            sbr.AppendFormat("package com.oegame.tablegames.protocol.gen;\r\n");
            sbr.AppendFormat("import java.util.ArrayList;\r\n");
            sbr.AppendFormat("import com.oegame.tablegames.common.io.*;\r\n");
            sbr.AppendFormat("import java.io.ByteArrayOutputStream;\r\n");
            sbr.AppendFormat("import java.io.ByteArrayInputStream;\r\n");
            sbr.AppendFormat("import java.io.IOException;\r\n");
            sbr.Append("\r\n");
            sbr.AppendFormat("/// <summary>\r\n");
            sbr.AppendFormat("/// {0}\r\n", protocol.CnName);
            sbr.AppendFormat("/// </summary>\r\n");
            sbr.AppendFormat("public class {0}\r\n", protoName);
            sbr.AppendFormat("{{\r\n");
            sbr.AppendFormat("    public static final int CODE = {0}; \r\n", protocol.Code);
            sbr.Append("\r\n");
            //生成变量
            foreach (var item in lst)
            {
                if (string.IsNullOrEmpty(item.AttachToCustom))
                {
                    sbr.Append(CreateVariable(item));
                }
            }
            //生成get、set
            foreach (var item in lst)
            {
                if (string.IsNullOrEmpty(item.AttachToCustom))
                {
                    sbr.Append(CreateGetSet(item));
                }
            }
            sbr.Append("\r\n");
            //生成自定义结构
            foreach (var item in lst)
            {
                //如果是自定义属性类型
                if (!IsCommonType(AttTypeToJava(item.Type)))
                {
                    if (existsCustom.Contains(AttTypeToJava(item.Type))) continue;
                    AppendSpace(sbr, 1);
                    sbr.AppendFormat("/// <summary>\r\n");
                    AppendSpace(sbr, 1);
                    sbr.AppendFormat("/// {0}\r\n", item.CnName);
                    AppendSpace(sbr, 1);
                    sbr.AppendFormat("/// </summary>\r\n");
                    AppendSpace(sbr, 1);
                    sbr.AppendFormat("public static class {0}\r\n", AttTypeToJava(item.Type));
                    AppendSpace(sbr, 1);
                    sbr.AppendFormat("{{\r\n");
                    //====================隶属于此自定义项的属性=================
                    List<Field> list = lst.Where(p => p.AttachToCustom.Equals(AttTypeToJava(item.Type), StringComparison.CurrentCultureIgnoreCase)).ToList();

                    if (list != null && list.Count > 0)
                    {
                        for (int j = 0; j < list.Count; j++)
                        {
                            sbr.Append(CreateVariable(list[j], 2));
                        }
                        for (int j = 0; j < list.Count; j++)
                        {
                            sbr.Append(CreateGetSet(list[j], 2));
                        }
                    }

                    //生成ToArray
                    sbr.Append(CreateToArrayFunction(list, true, 2));
                    //生成GetProto
                    sbr.Append(CreateGetProtoFunction(list, AttTypeToJava(item.Type), 2));

                    AppendSpace(sbr, 1);
                    sbr.AppendFormat("}}\r\n");

                    existsCustom.Add(AttTypeToJava(item.Type));
                }
            }

            baseList.Clear();
            for (int i = 0; i < lst.Count; ++i)
            {
                if (string.IsNullOrEmpty(lst[i].AttachToCustom))
                {
                    baseList.Add(lst[i]);
                }
            }
            //生成ToArray
            sbr.Append(CreateToArrayFunction(baseList));
            //生成GetProto
            sbr.Append(CreateGetProtoFunction(baseList, protoName));

            sbr.AppendFormat("}}\r\n");
            //生成代码文件
            CreateFile(menu.Name, sbr, protocol, outputPath);
        }

        private static bool IsCommonType(string type)
        {
            if (type.Equals("byte") || type.Equals("short") || type.Equals("int") || type.Equals("long") || type.Equals("String") || type.Equals("char") || type.Equals("float") || type.Equals("decimal") || type.Equals("boolean") || type.Equals("ushort") || type.Equals("double") || type.Equals("byte[]"))
            {
                return true;
            }
            return false;
        }

        private static string ChangeTypeName(string type, int language = 0)
        {
            string str = string.Empty;
            switch (type.ToLower())
            {
                case "int":
                    str = "Int";
                    break;
                case "long":
                    str = "Long";
                    break;
                case "float":
                    str = "Float";
                    break;
                case "string":
                    str = "UTF";
                    break;
                case "boolean":
                    str = "Boolean";
                    break;
                case "ushort":
                    str = "UShort";
                    break;
                case "byte":
                    str = "Byte";
                    break;
                case "short":
                    str = "Short";
                    break;
                case "double":
                    str = "Double";
                    break;
                case "byte[]":
                    str = "Bytes";
                    break;
            }

            return str;
        }

        public static string ValueToObject(string type)
        {
            switch (type.ToLower())
            {
                case "int":
                    return "Integer";
                case "bool":
                    return "Boolean";
                case "short":
                    return "Short";
                case "byte":
                    return "Byte";
                case "string":
                    return "String";
                case "float":
                    return "Float";
                case "double":
                    return "Double";
                case "long":
                    return "Long";
                default:
                    return type;
            }
        }

        private static List<Field> GetListByToBoolName(List<Field> lst, string boolName, bool isTrue)
        {
            return lst.Where(p => p.AttachToCondition.Equals(boolName, StringComparison.CurrentCultureIgnoreCase)).Where(p => p.AttachToResult == (isTrue ? 1 : 0)).ToList();
        }

        private static Dictionary<int, List<Field>> GetDicByToByteName(List<Field> lst, string byteName)
        {
            lst = lst.Where(p => p.AttachToCondition.Equals(byteName, StringComparison.CurrentCultureIgnoreCase)).ToList();
            Dictionary<int, List<Field>> dic = new Dictionary<int, List<Field>>();
            foreach (var p in lst)
            {
                if (!dic.ContainsKey(p.AttachToResult))
                {
                    dic[p.AttachToResult] = new List<Field>();
                }
                dic[p.AttachToResult].Add(p);
            }
            return dic;
        }

        private static StringBuilder CreateVariable(Field item, int deep = 1)
        {
            StringBuilder sbr = new StringBuilder();
            if (item.IsLoop)
            {
                AppendSpace(sbr, deep);
                sbr.AppendFormat("private ArrayList<{0}> {1} = new ArrayList<{0}>(); //{2}\r\n", ValueToObject(AttTypeToJava(item.Type)), item.EnName, item.CnName);
            }
            else
            {
                AppendSpace(sbr, deep);
                bool isString = AttTypeToJava(item.Type).Equals("String", StringComparison.CurrentCultureIgnoreCase);
                sbr.AppendFormat("private {0} {1}; //{2}\r\n", AttTypeToJava(item.Type), isString ? item.EnName + " = \"\"" : item.EnName, item.CnName);
            }
            return sbr;
        }

        private static StringBuilder CreateGetSet(Field item, int deep = 1)
        {
            StringBuilder sbr = new StringBuilder();
            if (item.IsLoop)
            {
                AppendSpace(sbr, deep);
                sbr.AppendFormat("public ArrayList<{0}> get{1}List(){{\r\n", ValueToObject(AttTypeToJava(item.Type)), item.EnName);
                AppendSpace(sbr, deep + 1);
                sbr.AppendFormat("return this.{0};\r\n", item.EnName);
                AppendSpace(sbr, deep);
                sbr.Append("};\r\n");
                sbr.AppendLine();
                AppendSpace(sbr, deep);
                sbr.AppendFormat("public {0} get{1}(int index){{\r\n", AttTypeToJava(item.Type), ToUpperFirst(item.EnName));
                AppendSpace(sbr, deep + 1);
                sbr.AppendFormat("return this.{0}.get(index);\r\n", item.EnName);
                AppendSpace(sbr, deep);
                sbr.Append("};\r\n");
                sbr.AppendLine();
                AppendSpace(sbr, deep);
                sbr.AppendFormat("public int {1}Count(){{\r\n", AttTypeToJava(item.Type), item.EnName);
                AppendSpace(sbr, deep + 1);
                sbr.AppendFormat("return this.{0}.size();\r\n", item.EnName);
                AppendSpace(sbr, deep);
                sbr.Append("};\r\n");
                sbr.AppendLine();
                AppendSpace(sbr, deep);
                sbr.AppendFormat("public void add{0}({1} value){{\r\n", ToUpperFirst(item.EnName), AttTypeToJava(item.Type));
                AppendSpace(sbr, deep + 1);
                sbr.AppendFormat("this.{0}.add(value);\r\n", item.EnName);
                AppendSpace(sbr, deep);
                sbr.Append("};\r\n");
                sbr.AppendLine();
            }
            else
            {
                AppendSpace(sbr, deep);
                sbr.AppendFormat("public {0} get{1}(){{\r\n", AttTypeToJava(item.Type), ToUpperFirst(item.EnName));
                AppendSpace(sbr, deep + 1);
                sbr.AppendFormat("return this.{0};\r\n", item.EnName);
                AppendSpace(sbr, deep);
                sbr.Append("}\r\n");
                sbr.AppendLine();
                AppendSpace(sbr, deep);
                sbr.AppendFormat("public void set{0}({1} value){{\r\n", ToUpperFirst(item.EnName), AttTypeToJava(item.Type));
                AppendSpace(sbr, deep + 1);
                sbr.AppendFormat("this.{0} = value;\r\n", item.EnName);
                AppendSpace(sbr, deep);
                sbr.Append("}\r\n");
                sbr.AppendLine();
            }
            return sbr;
        }

        private static string ToUpperFirst(string str)
        {
            char f = str[0];
            string l = str.Substring(1);
            return char.ToUpper(f) + l;
        }

        private static StringBuilder CreateToArrayFunction(List<Field> lst, bool isCus = false, int deep = 1)
        {
            StringBuilder sbr = new StringBuilder();
            AppendSpace(sbr, deep);
            sbr.AppendFormat("public byte[] toArray()\r\n");
            AppendSpace(sbr, deep);
            sbr.AppendFormat("{{\r\n");
            AppendSpace(sbr, deep + 1);
            sbr.AppendFormat("ByteArrayOutputStream baos = new ByteArrayOutputStream();\r\n");
            AppendSpace(sbr, deep + 1);
            sbr.AppendFormat("DataOutputStreamExt dos = new DataOutputStreamExt(baos);\r\n");
            AppendSpace(sbr, deep + 1);
            sbr.AppendFormat("byte[] ret = null;\r\n");
            AppendSpace(sbr, deep + 1);
            sbr.Append("try{\r\n");
            if (!isCus)
            {
                AppendSpace(sbr, deep + 2);
                sbr.AppendFormat("dos.writeInt(CODE);\r\n");
            }

            //写入数据流
            foreach (var item in lst)
            {
                if (string.IsNullOrEmpty(item.AttachToCondition))
                {
                    sbr.Append(CreateVariableToArray(item, deep + 2));
                }
                if (AttTypeToJava(item.Type) == "bool")
                {
                    bool isHasSuccess = false;
                    //查找隶属于这个bool成功的
                    List<Field> list = GetListByToBoolName(lst, item.EnName, true);
                    if (list != null && list.Count > 0)
                    {
                        isHasSuccess = true;
                        AppendSpace(sbr, deep + 1);
                        sbr.AppendFormat("if({0})\r\n", item.EnName);
                        AppendSpace(sbr, deep + 1);
                        sbr.Append("{\r\n");
                        for (int j = 0; j < list.Count; j++)
                        {
                            sbr.Append(CreateVariableToArray(list[j], deep + 3));
                        }
                        AppendSpace(sbr, deep + 1);
                        sbr.Append("}\r\n");
                    }
                    //查找隶属于这个bool失败的
                    list = GetListByToBoolName(lst, item.EnName, false);
                    if (list != null && lst.Count > 0)
                    {
                        if (isHasSuccess)
                        {
                            AppendSpace(sbr, deep + 1);
                            sbr.AppendFormat("else\r\n", item.EnName);
                        }
                        else
                        {
                            //如果没有成功项
                            AppendSpace(sbr, deep + 1);
                            sbr.AppendFormat("if(!{0})\r\n", item.EnName);
                        }
                        AppendSpace(sbr, deep + 1);
                        sbr.Append("{\r\n");
                        for (int j = 0; j < list.Count; j++)
                        {
                            sbr.Append(CreateVariableToArray(list[j], deep + 3));
                        }
                        AppendSpace(sbr, deep + 1);
                        sbr.Append("}\r\n");
                    }
                }
                if (AttTypeToJava(item.Type) == "byte")
                {
                    Dictionary<int, List<Field>> dic = GetDicByToByteName(lst, item.EnName);
                    int count = 0;
                    foreach (var pair in dic)
                    {
                        ++count;
                        AppendSpace(sbr, deep + 2);
                        if (count > 1)
                        {
                            sbr.Append("else ");
                        }
                        sbr.AppendFormat("if({0} == {1})\r\n", item.EnName, pair.Key);
                        AppendSpace(sbr, deep + 2);
                        sbr.Append("{\r\n");
                        for (int j = 0; j < pair.Value.Count; j++)
                        {
                            sbr.Append(CreateVariableToArray(pair.Value[j], deep + 3));
                        }
                        AppendSpace(sbr, deep + 2);
                        sbr.Append("}\r\n");
                    }
                }
            }
            AppendSpace(sbr, deep + 2);
            sbr.AppendFormat("ret = baos.toByteArray();\r\n");
            AppendSpace(sbr, deep + 2);
            sbr.AppendFormat("dos.close();\r\n");
            AppendSpace(sbr, deep + 2);
            sbr.AppendFormat("baos.close();\r\n");
            AppendSpace(sbr, deep + 1);
            sbr.Append("}\r\n");
            AppendSpace(sbr, deep + 1);
            sbr.Append("catch(IOException e){\r\n");
            AppendSpace(sbr, deep + 2);
            sbr.Append("e.printStackTrace();\r\n");
            AppendSpace(sbr, deep + 1);
            sbr.Append("}\r\n");
            AppendSpace(sbr, deep + 1);
            sbr.AppendFormat("return ret;\r\n");
            AppendSpace(sbr, deep);
            sbr.AppendFormat("}}\r\n");
            sbr.Append("\r\n");
            return sbr;
        }

        private static StringBuilder CreateGetProtoFunction(List<Field> lst, string typeName, int deep = 1)
        {
            StringBuilder sbr = new StringBuilder();
            AppendSpace(sbr, deep);
            sbr.AppendFormat("public static {0} getProto(byte[] buffer)\r\n", typeName);
            AppendSpace(sbr, deep);
            sbr.AppendFormat("{{\r\n");
            AppendSpace(sbr, deep + 1);
            sbr.AppendFormat("if(buffer == null) return null;\r\n", typeName);
            AppendSpace(sbr, deep + 1);
            sbr.AppendFormat("{0} proto = new {0}();\r\n", typeName);

            if (lst != null && lst.Count > 0)
            {
                AppendSpace(sbr, deep + 1);
                sbr.AppendFormat("ByteArrayInputStream bais = new ByteArrayInputStream(buffer);\r\n");
                AppendSpace(sbr, deep + 1);
                sbr.AppendFormat("DataInputStreamExt dis = new DataInputStreamExt(bais);\r\n");
                AppendSpace(sbr, deep + 1);
                sbr.Append("try{\r\n");

                //从数据流读取
                foreach (var item in lst)
                {

                    if (string.IsNullOrEmpty(item.AttachToCondition))
                    {
                        sbr.Append(CreateVariableGetProto(item, deep + 2));
                    }

                    if (AttTypeToJava(item.Type) == "bool")
                    {
                        bool isFather = false;
                        foreach (var i in lst)
                        {
                            if (i.AttachToCondition.Equals(item.EnName))
                            {
                                isFather = true;
                                break;
                            }
                        }
                        if (!isFather) continue;

                        bool isHasSuccess = false;
                        //查找隶属于这个bool成功的
                        List<Field> list = GetListByToBoolName(lst, item.EnName, true);
                        if (list != null && list.Count > 0)
                        {
                            isHasSuccess = true;
                            AppendSpace(sbr, deep + 2);
                            sbr.AppendFormat("if(proto.{0})\r\n", item.EnName);
                            sbr.Append("{\r\n");
                            for (int j = 0; j < list.Count; j++)
                            {
                                sbr.Append(CreateVariableGetProto(list[j], deep + 3));
                            }
                            AppendSpace(sbr, deep + 2);
                            sbr.Append("}\r\n");
                        }

                        //查找隶属于这个bool失败的
                        list = GetListByToBoolName(lst, item.EnName, false);
                        if (list != null && lst.Count > 0)
                        {
                            if (isHasSuccess)
                            {
                                AppendSpace(sbr, deep + 2);
                                sbr.AppendFormat("else\r\n", item.EnName);
                            }
                            else
                            {
                                //如果没有成功项
                                AppendSpace(sbr, deep + 2);
                                sbr.AppendFormat("if(!proto.{0})\r\n", item.EnName);
                            }
                            AppendSpace(sbr, deep + 2);
                            sbr.Append("{\r\n");
                            for (int j = 0; j < list.Count; j++)
                            {
                                sbr.Append(CreateVariableGetProto(list[j], deep + 3));
                            }
                            AppendSpace(sbr, deep + 2);
                            sbr.Append("}\r\n");
                        }
                    }

                    if (AttTypeToJava(item.Type) == "byte")
                    {
                        Dictionary<int, List<Field>> dic = GetDicByToByteName(lst, item.EnName);
                        int count = 0;
                        foreach (var pair in dic)
                        {
                            ++count;
                            AppendSpace(sbr, deep + 2);
                            if (count > 1)
                            {
                                sbr.Append("else ");
                            }
                            sbr.AppendFormat("if(proto.{0} == {1})\r\n", item.EnName, pair.Key);
                            AppendSpace(sbr, deep + 2);
                            sbr.Append("{\r\n");
                            for (int j = 0; j < pair.Value.Count; j++)
                            {
                                sbr.Append(CreateVariableGetProto(pair.Value[j], deep + 3));
                            }
                            AppendSpace(sbr, deep + 2);
                            sbr.Append("}\r\n");
                        }
                    }
                }
                AppendSpace(sbr, deep + 2);
                sbr.Append("dis.close();\r\n");
                AppendSpace(sbr, deep + 2);
                sbr.Append("bais.close();\r\n");
                AppendSpace(sbr, deep + 1);
                sbr.Append("}\r\n");
                AppendSpace(sbr, deep + 1);
                sbr.Append("catch(IOException e){\r\n");
                AppendSpace(sbr, deep + 2);
                sbr.Append("e.printStackTrace();\r\n");
                AppendSpace(sbr, deep + 1);
                sbr.Append("}\r\n");
            }
            AppendSpace(sbr, deep + 1);
            sbr.AppendFormat("return proto;\r\n");
            AppendSpace(sbr, deep);
            sbr.AppendFormat("}}\r\n");
            return sbr;
        }

        private static void CreateFile(string menuName, StringBuilder sbr, Protocol proto, string path)
        {
            path += "/java/";
            if (!Directory.Exists(path))
            {
                Directory.CreateDirectory(path);
            }

            using (FileStream fs = new FileStream(string.Format("{0}/{1}_{2}Proto.java", path, menuName, proto.EnName), FileMode.Create))
            {
                using (StreamWriter sw = new StreamWriter(fs))
                {
                    sw.Write(sbr.ToString());
                }
            }
        }

        private static void AppendSpace(StringBuilder sbr, int deep)
        {
            for (int i = 0; i < deep; ++i) { sbr.Append("    "); }
        }

        private static StringBuilder CreateVariableToArray(Field item, int deep = 3)
        {
            StringBuilder sbr = new StringBuilder();
            if (item.IsLoop)
            {
                AppendSpace(sbr, deep);
                sbr.AppendFormat("dos.writeShort({0}.size());\r\n", item.EnName);
                AppendSpace(sbr, deep);
                sbr.AppendFormat("for (int i = 0; i < {0}.size(); ++i)\r\n", item.EnName);
                AppendSpace(sbr, deep);
                sbr.Append("{\r\n");
                if (IsCommonType(AttTypeToJava(item.Type)))
                {
                    AppendSpace(sbr, deep + 1);
                    sbr.AppendFormat("dos.write{0}({1}.get(i));\r\n", ChangeTypeName(AttTypeToJava(item.Type)), item.EnName);
                }
                else
                {
                    AppendSpace(sbr, deep + 1);
                    sbr.AppendFormat("if({0} != null)\n", item.EnName);
                    AppendSpace(sbr, deep + 1);
                    sbr.Append("{\n");
                    AppendSpace(sbr, deep + 2);
                    sbr.AppendFormat("dos.writeBytes({0}.get(i).toArray());\r\n", item.EnName);
                    AppendSpace(sbr, deep + 1);
                    sbr.Append("}\n");
                    AppendSpace(sbr, deep + 1);
                    sbr.Append("else\n");
                    AppendSpace(sbr, deep + 1);
                    sbr.Append("{\n");
                    AppendSpace(sbr, deep + 2);
                    sbr.Append("dos.writeInt(0);\r\n");
                    AppendSpace(sbr, deep + 1);
                    sbr.Append("}\n");
                }
                AppendSpace(sbr, deep);
                sbr.Append("}\r\n");
            }
            else
            {

                if (IsCommonType(AttTypeToJava(item.Type)))
                {
                    AppendSpace(sbr, deep);
                    sbr.AppendFormat("dos.write{0}({1});\r\n", ChangeTypeName(AttTypeToJava(item.Type)), item.EnName);
                }
                else
                {
                    AppendSpace(sbr, deep);
                    sbr.AppendFormat("if({0} != null)\n", item.EnName);
                    AppendSpace(sbr, deep);
                    sbr.Append("{\n");
                    AppendSpace(sbr, deep + 1);
                    sbr.AppendFormat("dos.writeBytes({0}.toArray());\r\n", item.EnName);
                    AppendSpace(sbr, deep);
                    sbr.Append("}\n");
                    AppendSpace(sbr, deep);
                    sbr.Append("else\n");
                    AppendSpace(sbr, deep);
                    sbr.Append("{\n");
                    AppendSpace(sbr, deep + 1);
                    sbr.Append("dos.writeInt(0);\r\n");
                    AppendSpace(sbr, deep);
                    sbr.Append("}\n");
                }
            }
            return sbr;
        }

        private static StringBuilder CreateVariableGetProto(Field item, int deep = 3)
        {
            StringBuilder sbr = new StringBuilder();
            if (item.IsLoop)
            {
                AppendSpace(sbr, deep);
                sbr.AppendFormat("short {0}Length = dis.readShort();\r\n", item.EnName);
                AppendSpace(sbr, deep);
                sbr.AppendFormat("for (int i = 0; i < {0}Length; ++i)\r\n", item.EnName);
                AppendSpace(sbr, deep);
                sbr.Append("{\r\n");
                AppendSpace(sbr, deep + 1);
                if (IsCommonType(AttTypeToJava(item.Type)))
                {
                    sbr.AppendFormat("proto.{0}.add(dis.read{1}());\r\n", item.EnName, ChangeTypeName(AttTypeToJava(item.Type)));
                }
                else
                {
                    sbr.AppendFormat("proto.{0}.add({1}.getProto(dis.readBytes()));\r\n", item.EnName, AttTypeToJava(item.Type));
                }
                AppendSpace(sbr, deep);
                sbr.Append("}\r\n");
            }
            else
            {
                AppendSpace(sbr, deep);
                if (IsCommonType(AttTypeToJava(item.Type)))
                {
                    sbr.AppendFormat("proto.{0} = dis.read{1}();\r\n", item.EnName, ChangeTypeName(AttTypeToJava(item.Type)));
                }
                else
                {
                    sbr.AppendFormat("proto.{0} = {1}.getProto(dis.readBytes());\r\n", item.EnName, AttTypeToJava(item.Type));
                }
            }
            return sbr;
        }

        public static string AttTypeToJava(string attType)
        {
            if (attType.Equals("string"))
            {
                return "String";
            }
            else if (attType.Equals("bool"))
            {
                return "boolean";
            }
            return attType;
        }

        public void CreateCodeDef(List<Menu> menus, string path)
        {
            StringBuilder sbr = new StringBuilder();

            sbr.AppendFormat("//===================================================\r\n");
            sbr.AppendFormat("//作    者：DRB\r\n");
            sbr.AppendFormat("//创建时间：{0}\r\n", DateTime.Now.ToString("yyyy-MM-dd HH:mm:ss"));
            sbr.AppendFormat("//备    注：\r\n");
            sbr.AppendFormat("//===================================================\r\n");
            sbr.AppendFormat("package com.oegame.tablegames.protocol.gen;\r\n");
            sbr.AppendFormat("import java.util.HashMap;\r\n");
            sbr.AppendFormat("\r\n");
            sbr.AppendFormat("/// <summary>\r\n");
            sbr.AppendFormat("/// 协议编号定义\r\n");
            sbr.AppendFormat("/// </summary>\r\n");
            sbr.AppendFormat("public class ProtoCodeDef\r\n");
            sbr.AppendFormat("{{\r\n");

            foreach (var menu in menus)
            {
                foreach (var proto in menu.ProtocolInfos)
                {
                    string protoName = string.Format("{0}_{1}Proto", menu.Name, proto.EnName);
                    sbr.AppendFormat("    /// <summary>\r\n");
                    sbr.AppendFormat("    /// {0}\r\n", proto.CnName);
                    sbr.AppendFormat("    /// </summary>\r\n");
                    sbr.AppendFormat("    public static final int {0} = {1};\r\n", protoName, proto.Code);
                    sbr.AppendFormat("\r\n");
                }
            }

            sbr.AppendFormat("    private static final HashMap<Integer,String> DicCn = new HashMap<Integer,String>();\r\n");
            sbr.AppendFormat("    private static final HashMap<Integer,String> DicEn = new HashMap<Integer,String>();\r\n");
            sbr.AppendLine();

            sbr.AppendFormat("    static\r\n");
            sbr.Append("    {\r\n");
            foreach (var menu in menus)
            {
                foreach (var proto in menu.ProtocolInfos)
                {
                    string protoName = string.Format("{0}_{1}Proto", menu.Name, proto.EnName);
                    sbr.AppendFormat("        DicCn.put({0},\"{1}\");\r\n", protoName, proto.CnName);
                    sbr.AppendFormat("        DicEn.put({0},\"{1}\");\r\n", protoName, protoName);
                }
            }
            sbr.Append("    }\r\n");

            sbr.AppendFormat("    public static String getCn(int code)\r\n");
            sbr.Append("    {\r\n");
            sbr.AppendFormat("        if (!DicCn.containsKey(code)) return \"未知消息\";\r\n");
            sbr.AppendFormat("        return DicCn.get(code);\r\n");
            sbr.Append("    }\r\n");

            sbr.AppendFormat("    public static String getEn(int code)\r\n");
            sbr.Append("    {\r\n");
            sbr.AppendFormat("        if (!DicCn.containsKey(code)) return \"Unknown protocol\";\r\n");
            sbr.AppendFormat("        return DicEn.get(code);\r\n");
            sbr.Append("    }\r\n");

            sbr.Append("}\r\n");

            path += "/java/";
            if (!Directory.Exists(path))
            {
                Directory.CreateDirectory(path);
            }

            using (FileStream fs = new FileStream(string.Format("{0}/ProtoCodeDef.java", path), FileMode.Create))
            {
                using (StreamWriter sw = new StreamWriter(fs))
                {
                    sw.Write(sbr.ToString());
                }
            }
        }
    }
}