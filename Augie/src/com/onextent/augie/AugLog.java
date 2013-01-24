package com.onextent.augie;

import com.onextent.android.log.ALog;
import com.onextent.android.log.ALog.ATag;

public class AugLog {
    
    private static class AugTag extends ATag {

        protected AugTag() {
            super("AUGIE");
        }
    }

    private static AugTag AUGIE = new AugTag();

    public static void i(String msg) {
        ALog.i(AUGIE, msg);
    }
    
    public static void d(String msg) {
        ALog.d(AUGIE, msg);
    }
    
    public static void e(String msg) {
        ALog.e(AUGIE, msg);
    }
    public static void e(String msg, Throwable err) {
        ALog.e(AUGIE, msg, err);
    }
    public static void e(Throwable err) {
        if (err == null) return;
        ALog.e(AUGIE, err.toString(), err);
    }
    
    public static void w(String msg) {
        ALog.w(AUGIE, msg);
    }
    public static void w(String msg, Throwable err) {
        ALog.w(AUGIE, msg, err);
    }
    public static void w(Throwable err) {
        if (err == null) return;
        ALog.w(AUGIE, err.toString(), err);
    }
}
