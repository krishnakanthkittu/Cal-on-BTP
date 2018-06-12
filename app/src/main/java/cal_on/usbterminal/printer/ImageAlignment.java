
package cal_on.usbterminal.printer;

public enum ImageAlignment {
    IMAGE_CENTER(0),
    IMAGE_LEFT(1),
    IMAGE_RIGHT(2);
    
    private final int value;
    private static final ImageAlignment defaultEnum;

    static {
        defaultEnum = IMAGE_CENTER;
    }

    public final int getValue() {
        return this.value;
    }

    public static ImageAlignment getByValue(int value) {
        ImageAlignment[] arrimageAlignment = ImageAlignment.values();
        int n = arrimageAlignment.length;
        int n2 = 0;
        while (n2 < n) {
            ImageAlignment itm = arrimageAlignment[n2];
            if (itm.getValue() == value) {
                return itm;
            }
            ++n2;
        }
        return defaultEnum;
    }

    private ImageAlignment(int val, int n2, int n3) {
        this.value = val;
    }
    private ImageAlignment(int val) {
        this.value = val;
    }
}

