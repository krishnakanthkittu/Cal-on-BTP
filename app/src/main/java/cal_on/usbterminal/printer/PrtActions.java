
package cal_on.usbterminal.printer;

public enum PrtActions {
    NOP(0),
    CANCEL(24),
    RESET(67),
    STATUS_UPDATE(83),
    DATA_XFER(68),
    CONTRAST(80),
    REV_SEEK(66),
    FWD_SEEK(70);
    
    private final int value;
    private static final PrtActions defaultEnum;

    static {
        defaultEnum = NOP;
    }

    public final int getValue() {
        return this.value;
    }

    public static PrtActions getByValue(int value) {
        PrtActions[] arrprtActions = PrtActions.values();
        int n = arrprtActions.length;
        int n2 = 0;
        while (n2 < n) {
            PrtActions itm = arrprtActions[n2];
            if (itm.getValue() == value) {
                return itm;
            }
            ++n2;
        }
        return defaultEnum;
    }

    private PrtActions(int val, int n2, int n3) {
        this.value = val;
    }
    private PrtActions(int val) {
        this.value = val;
    }
}

