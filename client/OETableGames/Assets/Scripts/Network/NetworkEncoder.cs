//===================================================
//Author      : DRB
//CreateTime  ：2021/3/6 22:23:50
//Description ：
//===================================================

using DrbFramework.Extensions;
using DrbFramework.Network;
using DrbFramework.Utility;
using System;
using System.IO;
using System.Text;

public class NetworkEncoder : INetworkEncoder
{
    private const int DATA_HEAD_LENGTH = 4;
    private const string KEY = "w92rxtavrkr6c6ab";
    private const int CORRECTED = 7;
    private const int COMPRESS_LENGTH = 20480;

    public void Encode(INetworkChannel channel, object inData, Stream outData)
    {
        byte[] data = (byte[])inData;
        BinaryWriter writer = new BinaryWriter(outData);
        byte[] length = BitConverter.GetBytes(data.Length);
        Array.Reverse(length);
        writer.Write(length);
        writer.Write(data);


        int leng;
        bool isCompress;
        if (inData == null)
        {
            leng = 7;
            isCompress = false;
        }
        else
        {
            byte[] buffer = new byte[DATA_HEAD_LENGTH];
            for (int i = buffer.Length - 1; i >= 0; --i)
            {
                buffer[i] = data[buffer.Length - 1 - i];
            }

            byte[] key2bytes = Encoding.Default.GetBytes(KEY);
            int keyLength = key2bytes.Length;
            int dataLength = data.Length;

            int index = 0;

            while (index < dataLength)
            {
                int currentKey = dataLength % keyLength;
                currentKey = CORRECTED + currentKey;
                currentKey = currentKey * currentKey % keyLength;

                int currentByte = key2bytes[currentKey];
                data[index] = Convert.ToByte(data[index] ^ currentByte);

                index++;
            }


            isCompress = data.Length >= COMPRESS_LENGTH;
            if (isCompress)
            {
                data = GZipCompressUtil.Compress(data);
            }
            leng = data.Length + 3;
        }

        ushort crc = EncryptUtil.CalculateCrc16(data);
        using (MemoryStream ms = new MemoryStream())
        {
            ms.WriteInt(leng);
            ms.WriteBool(isCompress);
            ms.WriteUShort(crc);
            if (data != null)
            {
                ms.Write(data, 0, data.Length);
            }
            outData = ms;
        }
    }
}
