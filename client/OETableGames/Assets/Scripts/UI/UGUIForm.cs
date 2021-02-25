//===================================================
//Author      : DRB
//CreateTime  ：2021/2/25 12:57:10
//Description ：
//===================================================

using DrbFramework.Internal.UI;
using DrbFramework.UI;
using UnityEngine;

public class UGUIForm : UIForm
{
    private Canvas m_Canvas;
    private Canvas Canvas
    {
        get
        {
            if (m_Canvas == null)
            {
                m_Canvas = GetComponent<Canvas>();
                m_Canvas.overrideSorting = true;
            }
            return m_Canvas;
        }
    }

    public override int Depth
    {
        get
        {
            return Canvas.sortingOrder;
        }

        set
        {
            Canvas.sortingOrder = value + Group.Depth;
        }
    }

    public override IUIGroup Group
    {
        get
        {
            return base.Group;
        }

        set
        {
            base.Group = value;
            Canvas.sortingLayerName = Group.Name;
        }
    }
}