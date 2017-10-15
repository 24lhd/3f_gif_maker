package com.long3f.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

/**
 * Created by Admin on 15/9/2017.
 */

public class AppCache {
    private static Context ctx;
    private static AppCache instance;
    private static SharedPreferences pref;
    public static String savePath;
    private int maxSettingFrame = 150;
    private int maxSettingGifSize = 450;


    private AppCache(Context context) {
        this.ctx = context;
        this.pref = PreferenceManager.getDefaultSharedPreferences(ctx);
        initData();
    }

    public static AppCache getInstance(Context context) {
        try {
            if (instance == null) {
                instance = new AppCache(context);
            }
            AppCache localAppCache = instance;
            return localAppCache;
        } finally {
        }
    }

    private void initData() {
        if (this.savePath == null) {
            pref.edit().putString("save_path", "").apply();
        }
        else {
            pref.edit().putString("save_path", this.savePath).apply();
        }
        this.maxSettingGifSize = pref.getInt("max_gif_size",450);
        this.maxSettingFrame = pref.getInt("max_frame",150);
    }

    public static void setContext(Context paramContext) {
        ctx = paramContext;
    }
    public void setSavePath(String path) {
        this.savePath = path;
        initData();
    }
    public String getSavePath() {
       return this.savePath;
    }

    public void setMaxSettingFrame(int maxSettingFrame) {
        this.maxSettingFrame = maxSettingFrame;
        pref.edit().putInt("max_frame", this.maxSettingFrame).apply();
    }

    public int getMaxSettingFrame() {
        int maxF = pref.getInt("max_frame",150);
        return maxF;
    }

    public int getMaxSettingGifSize() {
        return maxSettingGifSize;
    }

    public void setMaxSettingGifSize(int maxSettingGifSize) {
        this.maxSettingGifSize = maxSettingGifSize;
        pref.edit().putInt("max_gif_size", this.maxSettingGifSize).apply();
    }
}


