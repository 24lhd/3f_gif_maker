package com.duong3f.module;

import android.content.Context;
import android.util.Log;

/**
 * Created by d on 9/21/2017.
 */

public class DuongLog {

    public static void loge(Class t, String log) {
        Log.e(t.getName(), log);
    }

    public static void logv(Context context, String log) {
        Log.v(context.getClass().getName(), log);
    }

    public static void logd(Context context, String log) {
        Log.d(context.getClass().getName(), log);
    }

    public static void logw(Context context, String log) {
        Log.w(context.getClass().getName(), log);
    }
}
