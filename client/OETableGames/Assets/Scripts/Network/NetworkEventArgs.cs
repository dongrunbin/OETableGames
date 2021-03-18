//===================================================
//Author      : DRB
//CreateTime  ：2021/3/7 0:33:30
//Description ：
//===================================================
using DrbFramework.Event;

public class NetworkEventArgs : EventArgs<int>
{
    private int m_Key;
    public override int Key
    {
        get
        {
            return m_Key;
        }
        protected set
        {
            m_Key = value;
        }
    }

    public byte[] Data;

    public NetworkEventArgs(int key, byte[] data)
    {
        Key = key;
        Data = data;
    }
}
