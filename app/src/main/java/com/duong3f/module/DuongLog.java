package com.duong3f.module;

import android.util.Log;

/**
 * Created by d on 9/21/2017.
 */

public class DuongLog {
    public static void e(Class t, String log) {
        Log.e("DuongLog " + t.getSimpleName(), log);
    }

    public static void d(Class t, String log) {
        Log.d("DuongLog " + t.getSimpleName(), log);
    }

    public static void v(Class t, String log) {
        Log.v("DuongLog " + t.getSimpleName(), log);
    }

    public static void w(Class t, String log) {
        Log.w("DuongLog " + t.getSimpleName(), log);
    }
}
