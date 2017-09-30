package com.duong3f.config;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.duong3f.module.DuongLog;
import com.github.hiteshsondhi88.libffmpeg.ExecuteBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.FFmpeg;
import com.github.hiteshsondhi88.libffmpeg.LoadBinaryResponseHandler;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegCommandAlreadyRunningException;
import com.github.hiteshsondhi88.libffmpeg.exceptions.FFmpegNotSupportedException;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by d on 9/17/2017.
 */

public class Config {
    public static final String TAG = "leuleu";
    public static final int REQUEST_CODE_GET_MULTI_IMAGE = 1010;
    public static int onFailure = 0;
    public static int onSuccess = 1;
    public static int onProgress = 2;
    public static int onStart = 3;
    public static int onFinish = 4;
    public static int FFmpegCommandAlreadyRunningException = 5;

    public static FFmpeg initFFmpeg(Context context) {

        FFmpeg ffmpeg = null;
        try {
            ffmpeg = FFmpeg.getInstance(context);
            ffmpeg.loadBinary(new LoadBinaryResponseHandler() {
                @Override
                public void onFailure() {
//                    showUnsupportedExceptionDialog();
                }

                @Override
                public void onSuccess() {
                    Log.e(TAG, "ffmpeg : correct Loaded");
                }
            });
        } catch (FFmpegNotSupportedException e) {
            Log.e(TAG, "ffmpeg : FFmpegNotSupportedException");
        } catch (Exception e) {
        }
        return ffmpeg;
    }

    public static void runFFmpegCommand(final String[] command, Context context) {
        try {
            Config.initFFmpeg(context).execute(command, new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    DuongLog.e(getClass(), "FAILED with output : " + s);
                }

                @Override
                public void onSuccess(String s) {
                    DuongLog.e(getClass(), "SUCCESS with output : " + s);
                }

                @Override
                public void onProgress(String s) {
                    DuongLog.e(getClass(), "progress : " + s);
                }

                @Override
                public void onStart() {
                    DuongLog.e(getClass(), "Started command : ffmpeg " + command);
                }

                @Override
                public void onFinish() {
                    DuongLog.e(getClass(), command.toString());
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            DuongLog.e(context.getClass(), "FFmpegCommandAlreadyRunningException command : ffmpeg " + e.getMessage());
        }
    }

    public static void runFFmpegCommandCallback(final String command, Context context, final Handler handler) {
        DuongLog.e(context.getClass(), command);
        try {
            Config.initFFmpeg(context).execute(command.split(" "), new ExecuteBinaryResponseHandler() {
                @Override
                public void onFailure(String s) {
                    DuongLog.e(getClass(), "FAILED with output : " + s);
                    handler.sendEmptyMessage(Config.onFailure);
                }

                @Override
                public void onSuccess(String s) {
                    DuongLog.e(getClass(), "SUCCESS with output : " + s);
                    handler.sendEmptyMessage(Config.onSuccess);
                }

                @Override
                public void onProgress(String s) {
                    handler.sendEmptyMessage(Config.onProgress);
                    DuongLog.e(getClass(), "progress : " + s);
                }

                @Override
                public void onStart() {
                    handler.sendEmptyMessage(Config.onStart);
                    DuongLog.e(getClass(), "Started command : ffmpeg " + command);
                }

                @Override
                public void onFinish() {
                    handler.sendEmptyMessage(Config.onFinish);
                    DuongLog.e(getClass(), command.toString());
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            handler.sendEmptyMessage(Config.FFmpegCommandAlreadyRunningException);
            DuongLog.e(context.getClass(), "FFmpegCommandAlreadyRunningException command : ffmpeg " + e.getMessage());
        }
    }

    public static String getCommandCrop(String inputFileName, String outputFileName, String sizeCrop) {
        return "-y -i " + inputFileName + " -filter:v crop=" + sizeCrop + " " + outputFileName;
//        return "-y -i " + inputFileName + " crop=" + sizeCrop + " " + outputFileName;
    }

    public static String getCommandFlipVertical(String inputFileName, String outputFileName) {
        return "-y -i " + inputFileName + " -vf hflip " + outputFileName;
    }

    public static String getCommandFlipHolizontal(String inputFileName, String outputFileName) {
        return "-y -i " + inputFileName + " -vf vflip " + outputFileName;
    }

    public static String getCommandRotation(String inputFileName, String outputFileName) {
        return "-y -i " + inputFileName + " -vf transpose=1 " + outputFileName;
    }

    public static void copy(File src, File dst) throws IOException {
        try (InputStream in = new FileInputStream(src)) {
            try (OutputStream out = new FileOutputStream(dst)) {
                // Transfer bytes from in to out
                byte[] buf = new byte[1024];
                int len;
                while ((len = in.read(buf)) > 0) {
                    out.write(buf, 0, len);
                }
            }
        }

    }
}
