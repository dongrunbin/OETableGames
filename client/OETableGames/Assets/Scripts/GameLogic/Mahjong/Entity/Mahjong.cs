//===================================================
//Author      : DRB
//CreateTime  ：2021/4/6 3:55:08
//Description ：
//===================================================

using System;

public class Mahjong : IComparable
{
    [UnityEngine.SerializeField]
    protected int m_Index;
    [UnityEngine.SerializeField]
    protected int m_Color;
    [UnityEngine.SerializeField]
    protected int m_Size;

    public const string DefaultName = "0_0";


    public int index { get { return m_Index; } set { m_Index = value; } }

    public int color { get { return m_Color; } set { m_Color = value; } }

    public int size { get { return m_Size; } set { m_Size = value; } }


    public Mahjong() { }

    public Mahjong(int color, int size)
    {
        m_Index = 0;
        m_Color = color;
        m_Size = size;
    }

    public Mahjong(int index, int color, int size)
    {
        m_Index = index;
        m_Color = color;
        m_Size = size;
    }

    public Mahjong(Mahjong poker)
    {
        if (poker != null)
        {
            m_Index = poker.index;
            m_Color = poker.color;
            m_Size = poker.size;
        }
    }

    public override string ToString()
    {
        return string.Format("{0}_{1}", color.ToString(), size.ToString());
    }

    public string ToLog()
    {
        return string.Format("{0}_{1}_{2}", index.ToString(), color.ToString(), size.ToString());
    }

    public virtual int CompareTo(object other)
    {
        if (other == null) return -1;
        if (!(other is Mahjong)) return -1;
        Mahjong otherPoker = other as Mahjong;
        if (color != otherPoker.color) return color - otherPoker.color;
        if (size != otherPoker.size) return size - otherPoker.size;
        return index - otherPoker.index;
    }

    public bool Equals(Mahjong poker)
    {
        if (poker == null) return false;
        return color == poker.color && size == poker.size;
    }

    public override int GetHashCode()
    {
        return ToString().GetHashCode();
    }
}
