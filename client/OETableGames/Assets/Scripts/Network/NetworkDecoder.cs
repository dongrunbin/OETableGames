//===================================================
//Author      : DRB
//CreateTime  ：2021/3/6 22:23:43
//Description ：
//===================================================
using DrbFramework.Extensions;
using DrbFramework.Network;
using DrbFramework.Utility;
using System;
using System.IO;
using System.Text;

public class NetworkDecoder : INetworkDecoder
{
    private const int DATA_HEAD_LENGTH = 4;
    private byte[] m_HeadData = new byte[DATA_HEAD_LENGTH];
    private const string KEY = "w92rxtavrkr6c6ab";
    private const int CORRECTED = 7;

    public void Decode(INetworkChannel channel, Stream inData, out object outData)
    {
        outData = null;
        inData.Position = 0;
        inData.Read(m_HeadData, 0, DATA_HEAD_LENGTH);
        int currentMsgLen = ((m_HeadData[0] & 0xff) << 24) + ((m_HeadData[1] & 0xff) << 16) + ((m_HeadData[2] & 0xff) << 8) + (m_HeadData[3] & 0xff);
        int currentFullMsgLen = DATA_HEAD_LENGTH + currentMsgLen;
        if (inData.Length >= currentFullMsgLen)
        {
            byte[] buffer = new byte[currentMsgLen];
            inData.Position = DATA_HEAD_LENGTH + 1;
            inData.Read(buffer, 0, currentMsgLen);

            byte[] key2bytes = Encoding.Default.GetBytes(KEY);
            int keyLength = key2bytes.Length;
            int dataLength = buffer.Length;

            int index = 0;

            while (index < dataLength)
            {
                int currentKey = dataLength % keyLength;
                currentKey = CORRECTED + currentKey;
                currentKey = currentKey * currentKey % keyLength;

                int currentByte = key2bytes[currentKey];
                buffer[index] = Convert.ToByte(buffer[index] ^ currentByte);

                index++;
            }

            using (MemoryStream ms = new MemoryStream(buffer))
            {
                bool isCompress = ms.ReadBool();
                byte[] content = new byte[buffer.Length - 1];
                ms.Read(content, 0, content.Length);
                if (isCompress)
                {
                    content = GZipCompressUtil.DeCompress(content);
                }
                outData = buffer;
            }

            int remainLen = (int)inData.Length - currentFullMsgLen;
            if (remainLen > 0)
            {
                inData.Position = currentFullMsgLen;
                byte[] remainBuffer = new byte[remainLen];
                inData.Read(remainBuffer, 0, remainLen);
                inData.Position = 0;
                inData.SetLength(0);

                inData.Write(remainBuffer, 0, remainBuffer.Length);
            }
            else
            {
                inData.Position = 0;
                inData.SetLength(0);
            }
        }
    }
}
