//===================================================
//Author      : DRB
//CreateTime  ：2021/3/6 22:23:50
//Description ：
//===================================================

using DrbFramework.Network;
using System.IO;

public class NetworkEncoder : INetworkEncoder
{
    public void Encode(INetworkChannel channel, object inData, Stream outData)
    {
        byte[] data = (byte[])inData;
        BinaryWriter writer = new BinaryWriter(outData);
        byte[] length = System.BitConverter.GetBytes(data.Length);
        System.Array.Reverse(length);
        writer.Write(length);
        writer.Write(data);
    }
}
