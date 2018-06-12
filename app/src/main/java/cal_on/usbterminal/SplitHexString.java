package cal_on.usbterminal;

/**
 * Created by Home on 14-Mar-17.
 */

import java.util.ArrayList;
import java.util.List;

public class SplitHexString {
    public List<String> splitHexString(String hexStr, int splitLength) {
        ArrayList<String> resultList = new ArrayList<String>();
        try {
            int origLength = hexStr.length();
            int loopCount = origLength / splitLength;
            if (origLength % splitLength > 0) {
                ++loopCount;
            }
            int i = 0;
            while (i < loopCount) {
                int startPos = i * splitLength;
                int endPos = startPos + splitLength;
                if (endPos > origLength) {
                    endPos = origLength;
                }
                resultList.add(hexStr.substring(startPos, endPos));
                ++i;
            }
        }
        catch (Exception origLength) {
            // empty catch block
        }
        return resultList;
    }
}
