package cal_on.usbterminal;

/**
 * Created by Home on 14-Mar-17.
 */

public class ConvertIntToHex {
    public byte convertIntToHex(int paramInt)
    {
        return Byte.valueOf(Integer.toHexString(paramInt)).byteValue();
    }
}
