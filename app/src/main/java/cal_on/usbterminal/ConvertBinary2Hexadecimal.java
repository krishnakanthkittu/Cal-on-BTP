package cal_on.usbterminal;

/**
 * Created by Home on 14-Mar-17.
 */
public class ConvertBinary2Hexadecimal {
    public static String convertBinary2Hexadecimal(byte[] paramArrayOfByte)
    {
        StringBuffer localStringBuffer = new StringBuffer();
        for (int i = 0;; i++)
        {
            if (i >= paramArrayOfByte.length) {
                return localStringBuffer.toString();
            }
            localStringBuffer.append("0123456789ABCDEF".charAt((0xFF & paramArrayOfByte[i]) >> 4));
            localStringBuffer.append("0123456789ABCDEF".charAt(0xF & paramArrayOfByte[i]));
        }
    }
}
