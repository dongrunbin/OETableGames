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
    protected int m_Number;

    public const string DefaultName = "0_0";


    public int index { get { return m_Index; } set { m_Index = value; } }

    public int color { get { return m_Color; } set { m_Color = value; } }

    public int number { get { return m_Number; } set { m_Number = value; } }


    public Mahjong() { }

    public Mahjong(int color, int size)
    {
        m_Index = 0;
        m_Color = color;
        m_Number = size;
    }

    public Mahjong(int index, int color, int size)
    {
        m_Index = index;
        m_Color = color;
        m_Number = size;
    }

    public Mahjong(Mahjong poker)
    {
        if (poker != null)
        {
            m_Index = poker.index;
            m_Color = poker.color;
            m_Number = poker.number;
        }
    }

    public override string ToString()
    {
        return string.Format("{0}_{1}", color.ToString(), number.ToString());
    }

    public string ToLog()
    {
        return string.Format("{0}_{1}_{2}", index.ToString(), color.ToString(), number.ToString());
    }

    public virtual int CompareTo(object other)
    {
        if (other == null) return -1;
        if (!(other is Mahjong)) return -1;
        Mahjong otherPoker = other as Mahjong;
        if (color != otherPoker.color) return color - otherPoker.color;
        if (number != otherPoker.number) return number - otherPoker.number;
        return index - otherPoker.index;
    }

    public bool Equals(Mahjong poker)
    {
        if (poker == null) return false;
        return color == poker.color && number == poker.number;
    }

    public override int GetHashCode()
    {
        return ToString().GetHashCode();
    }
}
