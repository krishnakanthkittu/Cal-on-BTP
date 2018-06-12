
package cal_on.usbterminal.printer;

public enum PrtContrastLevel {
    CONTRAST_LEVEL_0(48),
    CONTRAST_LEVEL_1(49),
    CONTRAST_LEVEL_2(50),
    CONTRAST_LEVEL_3(51),
    CONTRAST_LEVEL_4(52),
    CONTRAST_LEVEL_5(53),
    CONTRAST_LEVEL_6(54),
    CONTRAST_LEVEL_7(55),
    CONTRAST_LEVEL_8(56),
    CONTRAST_LEVEL_9(57);
    
    private final int value;
    private static final PrtContrastLevel defaultEnum;

    static {
        defaultEnum = CONTRAST_LEVEL_6;
    }

    public final int getValue() {
        return this.value;
    }

    public static PrtContrastLevel getByValue(int value) {
        PrtContrastLevel[] arrprtContrastLevel = PrtContrastLevel.values();
        int n = arrprtContrastLevel.length;
        int n2 = 0;
        while (n2 < n) {
            PrtContrastLevel itm = arrprtContrastLevel[n2];
            if (itm.getValue() == value) {
                return itm;
            }
            ++n2;
        }
        return defaultEnum;
    }

    public static PrtContrastLevel fromInt(int value) {
        value = value < 0 ? 0 : value;
        value = value > 9 ? 9 : value;
        return PrtContrastLevel.getByValue(48 + value);
    }

    public int toInt() {
        return this.getValue() - 48;
    }

    private PrtContrastLevel(int val, int n2, int n3) {
        this.value = val;
    }
    private PrtContrastLevel(int val) {
        this.value = val;
    }
}

