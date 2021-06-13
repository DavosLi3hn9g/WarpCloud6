package com.haibin.httpnet;

import ohos.hiviewdfx.HiLog;
import ohos.hiviewdfx.HiLogLabel;

public final class Log {
    static final HiLogLabel label = new HiLogLabel(HiLog.LOG_APP, 0x01, "HOS_LOG");

    public static void e(String msg){
        HiLog.error(label,"%{public}s",msg);
    }
}
