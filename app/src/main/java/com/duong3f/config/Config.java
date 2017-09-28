package com.duong3f.config;

import android.content.Context;
import android.util.Log;

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
                    Log.d(TAG, "FAILED with output : " + s);
                }

                @Override
                public void onSuccess(String s) {
                    Log.d(TAG, "SUCCESS with output : " + s);
//                        Intent intent = new Intent(MainActivity.this, PreviewGifActivity.class);
//                        intent.putExtra("filepath", filePath);
//                        startActivity(intent);
//                    AppUtils.addImageToGallery(filePath, getApplicationContext());
                }

                @Override
                public void onProgress(String s) {
//                    Log.d(TAG, "Started command : ffmpeg " + command);
//                    progressDialog.setMessage("progress : " + s);
                    Log.d(TAG, "progress : " + s);
                }

                @Override
                public void onStart() {
                    Log.d(TAG, "Started command : ffmpeg " + command);
//                    progressDialog.setMessage("Processing...");
//                    progressDialog.show();
                }

                @Override
                public void onFinish() {
                    Log.d(TAG, command.toString());
//                    progressDialog.dismiss();
                }
            });
        } catch (FFmpegCommandAlreadyRunningException e) {
            // do nothing for now
            Log.d(TAG, "FFmpegCommandAlreadyRunningException command : ffmpeg " + e.getMessage());
        }
    }

    public static void runFFmpegCommandCrop(Context context, String inputFileName, String outputFileName, String sizeCrop) {
        String command = "-i " + inputFileName + " -filter:v \"crop=" + sizeCrop + "\" " + outputFileName;
        runFFmpegCommand(command.split(" "), context);
    }

    public static void runFFmpegCommandFlipVertical(Context context, String inputFileName, String outputFileName, String sizeCrop) {
        String command = "-i " + inputFileName + " -filter:v \"crop=" + sizeCrop + "\" " + outputFileName;
        runFFmpegCommand(command.split(" "), context);
    }

    public static void runFFmpegCommandFlipHolizontal(Context context, String inputFileName, String outputFileName, String sizeCrop) {
        String command = "-i " + inputFileName + " -filter:v \"crop=" + sizeCrop + "\" " + outputFileName;
        runFFmpegCommand(command.split(" "), context);
    }

    public static void runFFmpegCommandRotation(Context context, String inputFileName, String outputFileName, String sizeCrop) {
        String command = "-i " + inputFileName + " -filter:v \"crop=" + sizeCrop + "\" " + outputFileName;
        runFFmpegCommand(command.split(" "), context);
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
