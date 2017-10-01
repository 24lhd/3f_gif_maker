package com.long3f.utils;

import android.content.ContentValues;
import android.content.Context;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

/**
 * Created by Long on 13/9/2017.
 */

public class AppUtils {
    final static String MEDIA_PATH = Environment.getExternalStorageDirectory().getPath() + "/";
    private static String gif = ".gif";

    public static ArrayList<String> getFromSdcard() {
        File file = new File(android.os.Environment.getExternalStorageDirectory(), "/VideoToGif");
        ArrayList<String> paths = new ArrayList<>();

        if (file.isDirectory()) {
            File[] listFile = file.listFiles();
            Arrays.sort(listFile, new Comparator<File>() {
                @Override
                public int compare(File f1, File f2) {
                    return Long.valueOf(f2.lastModified()).compareTo(f1.lastModified());
                }
            });
            for (int i = 0; i < listFile.length; i++) {
                if (listFile[i].getName().endsWith(gif)) {
                    paths.add(listFile[i].getAbsolutePath());
                    Log.e("getFromSdcard: ",listFile[i].getAbsolutePath());
                }

            }
        }
        return paths;
    }

    public static void addImageToGallery(final String filePath, final Context context) {

        ContentValues values = new ContentValues();

        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/gif");
        values.put(MediaStore.MediaColumns.DATA, filePath);
        context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
    }

    public static void deleteImageToGallery(final String filePath, final Context context) {
        synchronized(context.getContentResolver()){
            context.getContentResolver().notify();
        }
//        context.getContentResolver().notify();
//        Uri.parse(filePath),null,null);
    }

}
