using System.IO;
using ICSharpCode.SharpZipLib.GZip;

public class GZipCompressUtil
{
    public static byte[] Compress(byte[] bytes)
    {
        if (bytes == null)
        {
            return null;
        }

        MemoryStream ms = new MemoryStream();
        GZipOutputStream stream = new GZipOutputStream(ms);
        stream.Write(bytes, 0, bytes.Length);
        stream.Close();

        byte[] result = ms.ToArray();
        ms.Close();

        return result;
    }

    public static byte[] DeCompress(byte[] bytes)
    {
        if (bytes == null)
        {
            return null;
        }

        MemoryStream ms = new MemoryStream(bytes);
        GZipInputStream stream = new GZipInputStream(ms);

        MemoryStream buffer = new MemoryStream();
        int count;
        byte[] temp = new byte[1024];
        while ((count = stream.Read(temp, 0, temp.Length)) != 0)
        {
            buffer.Write(temp, 0, count);
        }
        ms.Close();
        stream.Close();

        byte[] result = buffer.ToArray();
        buffer.Close();

        return result;
    }

}
