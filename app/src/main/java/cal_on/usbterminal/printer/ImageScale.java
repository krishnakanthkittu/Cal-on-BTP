
package cal_on.usbterminal.printer;

public enum ImageScale {
	   SCALE_ONE_TO_ONE(1),
	    SCALE_TWO_TO_ONE(2),
	    SCALE_FOUR_TO_ONE(4),
	    SCALE_FULL_IMAGE(0);
    
    private final int value;
    private static final ImageScale defaultEnum;

    static {
        defaultEnum = SCALE_FULL_IMAGE;
    }

    public final int getValue() {
        return this.value;
    }

    public static ImageScale getByValue(int value) {
        ImageScale[] arrimageScale = ImageScale.values();
        int n = arrimageScale.length;
        int n2 = 0;
        while (n2 < n) {
            ImageScale itm = arrimageScale[n2];
            if (itm.getValue() == value) {
                return itm;
            }
            ++n2;
        }
        return defaultEnum;
    }

    private ImageScale(int val, int n2, int n3) {
        value =val;
    }
    private ImageScale(int val) {
        value =val;
    }
}

