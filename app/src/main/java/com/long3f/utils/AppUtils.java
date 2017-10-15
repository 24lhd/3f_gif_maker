package com.long3f.utils;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.media.ThumbnailUtils;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.NumberPicker;

import org.apache.commons.io.FileUtils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.Field;
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
                    Log.e("getFromSdcard: ", listFile[i].getAbsolutePath());
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
        synchronized (context.getContentResolver()) {
            context.getContentResolver().notify();
        }
    }

    public static ArrayList<Bitmap> cropListImageFromFolder(File fromFolder,
                                                        int width,
                                                        int height,
                                                        int bgcolor) {

        ArrayList<Bitmap> bitmaps = new ArrayList<>();

        for (final File fileEntry : fromFolder.listFiles()) {
            if (fileEntry.isDirectory()) {
                cropListImageFromFolder(fileEntry,width,height,bgcolor);
            } else {
                try{
                Bitmap bitmap = BitmapFactory.decodeFile(fileEntry.getAbsolutePath());
                Bitmap resize = scalePreserveRatio(bitmap, width, height, bgcolor);
                bitmaps.add(resize);
                Log.d("ListFrame", fileEntry.getName());
                }catch (Exception e){

                }
            }
        }
        return bitmaps;
    }

    public static void saveImageToFolderFromListBitmap(ArrayList<Bitmap> listImage,String toFolder,
                                                            boolean isRepeat) {

        String dir;
        if(toFolder == null || toFolder.equals("")){
            dir = Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_PICTURES) + "/GifFrames";
        }else {
            dir = toFolder;
        }
        File dirParen = new File(dir);
        if (dirParen.exists()) {
            dirParen.delete();
            dirParen.mkdir();
        } else {
            dirParen.mkdir();
        }



        int number = 1;
        for (int i = 0; i < listImage.size(); i++) {
            saveBitmap(listImage.get(i), dir + "/out_" + String.format("%03d", number) + ".png");
            number++;
        }
        if (isRepeat) {
            for (int i = listImage.size()-1; i >= 0; i--) {
                saveBitmap(listImage.get(i), dir + "/out_" + String.format("%03d", number) + ".png");
                number++;
            }
        }
    }

    public static void saveBitmap(Bitmap bitmap, String path) {
        try {
            File file = new File(path);
            OutputStream os = new BufferedOutputStream(new FileOutputStream(file));
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, os);
            os.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap scalePreserveRatio(Bitmap imageToScale,
                                            int destinationWidth,
                                            int destinationHeight,
                                            int color
    ) {
        if (destinationHeight > 0 && destinationWidth > 0 && imageToScale != null) {
            int width = imageToScale.getWidth();
            int height = imageToScale.getHeight();

            //Calculate the max changing amount and decide which dimension to use
            float widthRatio = (float) destinationWidth / (float) width;
            float heightRatio = (float) destinationHeight / (float) height;

            //Use the ratio that will fit the image into the desired sizes
            int finalWidth = (int) Math.floor(width * widthRatio);
            int finalHeight = (int) Math.floor(height * widthRatio);
            if (finalWidth > destinationWidth || finalHeight > destinationHeight) {
                finalWidth = (int) Math.floor(width * heightRatio);
                finalHeight = (int) Math.floor(height * heightRatio);
            }

            //Scale given bitmap to fit into the desired area
            imageToScale = Bitmap.createScaledBitmap(imageToScale, finalWidth, finalHeight, true);

            //Created a bitmap with desired sizes
            Bitmap scaledImage = Bitmap.createBitmap(destinationWidth, destinationHeight, Bitmap.Config.ARGB_8888);
            Canvas canvas = new Canvas(scaledImage);

            //Draw background color
            Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPaint.setColor(color);
            mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OUT));
            mPaint.setAntiAlias(true);

            canvas.drawRect(0, 0, canvas.getWidth(), canvas.getHeight(), mPaint);

            //Calculate the ratios and decide which part will have empty areas (width or height)
            float ratioBitmap = (float) finalWidth / (float) finalHeight;
            float destinationRatio = (float) destinationWidth / (float) destinationHeight;
            float left = ratioBitmap >= destinationRatio ? 0 : (float) (destinationWidth - finalWidth) / 2;
            float top = ratioBitmap < destinationRatio ? 0 : (float) (destinationHeight - finalHeight) / 2;
            canvas.drawBitmap(imageToScale, left, top, null);

            return scaledImage;
        } else {
            return imageToScale;
        }
    }

    public static ArrayList<Bitmap> listFilesForFolder(final File folder) {
        ArrayList<Bitmap> arr = new ArrayList<>();
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inPreferredConfig = Bitmap.Config.ARGB_8888;
        for (final File fileEntry : folder.listFiles()) {
            if (fileEntry.isDirectory()) {
                listFilesForFolder(fileEntry);
            } else {
                Bitmap bitmap = BitmapFactory.decodeFile(fileEntry.getAbsolutePath(),options);
                Bitmap resized = ThumbnailUtils.extractThumbnail(bitmap, 90, 90);
                arr.add(resized);
                Log.d("ListFrame", fileEntry.getName());
            }
        }
        return arr;
    }

    public static ArrayList<Bitmap> getListThumb(ArrayList<Bitmap> fromArrayList) {
        ArrayList<Bitmap> arr = new ArrayList<>();
        for (Bitmap bitmap : fromArrayList) {
                arr.add(getThumb(bitmap));
            }
        return arr;
    }
    public static Bitmap getThumb(Bitmap bitmap){
        return ThumbnailUtils.extractThumbnail(bitmap, 90, 90);
    }

    public static void clearFileInfolder(String dir){
        try {
            File d = new File(dir);
            if(d.exists()) {
                FileUtils.cleanDirectory(d);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static boolean setNumberPickerTextColor(NumberPicker numberPicker, int color)
    {
        final int count = numberPicker.getChildCount();
        for(int i = 0; i < count; i++){
            View child = numberPicker.getChildAt(i);
            if(child instanceof EditText){
                try{
                    Field selectorWheelPaintField = numberPicker.getClass()
                            .getDeclaredField("mSelectorWheelPaint");
                    selectorWheelPaintField.setAccessible(true);
                    ((Paint)selectorWheelPaintField.get(numberPicker)).setColor(color);
                    ((EditText)child).setTextColor(color);
                    numberPicker.invalidate();
                    return true;
                }
                catch(NoSuchFieldException e){
                }
                catch(IllegalAccessException e){
                }
                catch(IllegalArgumentException e){
                }
            }
        }
        return false;
    }

    public static void hideSoftKeyboard(Activity paramActivity)
    {
        try
        {
            ((InputMethodManager)paramActivity.getSystemService(Context.INPUT_METHOD_SERVICE))
                    .hideSoftInputFromWindow(paramActivity.getCurrentFocus().getWindowToken(), 0);
            return;
        }
        catch (Exception e) {}
    }
}
