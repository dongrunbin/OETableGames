//===================================================
//Author      : DRB
//CreateTime  ：2021/4/14 2:09:58
//Description ：
//===================================================
using System.Collections.Generic;
using UnityEngine;

[ExecuteInEditMode]
public class Grid3D : MonoBehaviour
{
    public enum Aligment
    {
        UpperLeft,
        UpperCenter,
        UpperRight,
        CenterLeft,
        Center,
        CenterRight,
        LowerLeft,
        LowerCenter,
        LowerRight,
    }

    public enum Constraint
    {
        FixedRowCount,
        FixedColumnCount,
        FixedDeepCount,
    }
    /// <summary>
    /// 子物体间距
    /// </summary>
    public Vector3 spacing;

    /// <summary>
    /// 物体偏移
    /// </summary>
    public Vector3 offset;

    public Aligment childAligment;

    public Constraint constraint;

    public int constraintCount;




    private Vector3 distance;

    [SerializeField]
    private bool isCenter = true;
    [SerializeField]
    private bool isBack = false;

    public Vector3 GetNextPos()
    {
        return GetPos(transform.childCount) + transform.position;
    }

    public Vector3 GetLocalPos(Transform trans)
    {
        return GetPos(trans.GetSiblingIndex());
    }


#if UNITY_EDITOR
    private void Update()
    {
        if (Application.isPlaying) return;
        Sort();
    }
#endif

    /// <summary>
    /// 排列
    /// </summary>
    public void Sort()
    {
        if (constraintCount == 0) return;


        int childCount = transform.childCount;

        if (isCenter)
        {
            distance = childCount / constraintCount * spacing / 2;
            if (childCount / constraintCount % constraintCount != 0)
            {
                Vector3 o = spacing / constraintCount;
                distance = distance - new Vector3(o.x, o.y, o.z);
            }
            switch (constraint)
            {
                case Constraint.FixedColumnCount:
                    distance.x = 0;
                    distance.z = 0;
                    break;
                case Constraint.FixedRowCount:
                    distance.y = 0;
                    distance.z = 0;
                    break;
                case Constraint.FixedDeepCount:
                    distance.x = 0;
                    distance.y = 0;
                    break;
            }
        }
        else
        {
            distance = Vector3.zero;
        }

        List<Transform> lst = new List<Transform>();
        for (int i = 0; i < childCount; ++i)
        {
            lst.Add(transform.GetChild(i).GetComponent<Transform>());
        }
        if (isBack)
        {
            for (int i = 0; i < lst.Count; ++i)
            {
                lst[i].localPosition = GetPos(lst.Count - 1 - i);
            }
        }
        else
        {
            for (int i = 0; i < lst.Count; ++i)
            {
                lst[i].localPosition = GetPos(i);
            }
        }
    }


    public Vector3 GetPos(int index)
    {
        Vector3 ret = Vector3.zero;

        int columnIndex = 0;
        int rowIndex = 0;
        switch (constraint)
        {
            case Constraint.FixedColumnCount:
                columnIndex = index % constraintCount;
                rowIndex = index / constraintCount;
                break;
            case Constraint.FixedRowCount:
                columnIndex = index / constraintCount;
                rowIndex = index % constraintCount;
                break;
        }

        switch (childAligment)
        {
            case Aligment.LowerLeft:
                ret = new Vector3(columnIndex * spacing.x + (rowIndex * offset.x), rowIndex * spacing.y + (columnIndex * offset.y), rowIndex * spacing.z + (columnIndex * offset.z));
                break;
            case Aligment.LowerRight:
                ret = new Vector3(columnIndex * -spacing.x + (rowIndex * offset.x), rowIndex * spacing.y + (columnIndex * offset.y), rowIndex * spacing.z + (columnIndex * offset.z));
                break;
            case Aligment.UpperLeft:
                ret = new Vector3(columnIndex * spacing.x + (rowIndex * offset.x), rowIndex * -spacing.y + (columnIndex * offset.y), rowIndex * spacing.z + (columnIndex * offset.z));
                break;
            case Aligment.UpperRight:
                ret = new Vector3(columnIndex * -spacing.x + (rowIndex * offset.x), rowIndex * -spacing.y + (columnIndex * offset.y), rowIndex * spacing.z + (columnIndex * offset.z));
                break;
        }
        ret -= distance;
        return ret;
    }
}
