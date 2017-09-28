package com.duong3f.module;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;

import com.duong3f.obj.AlbumImage;
import com.duong3f.obj.Image;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by d on 9/21/2017.
 */

public class TaskGetFolderImage extends AsyncTask<Void, Void, List<AlbumImage>> {
    Handler handler;
    private Context mContext;
    private List<AlbumImage> mFoldersList;

    private final String[] mProjection = new String[]{
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.BUCKET_DISPLAY_NAME,
    };


    public TaskGetFolderImage(Context context, Handler handler) {
        mContext = context;
        this.handler = handler;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected List<AlbumImage> doInBackground(Void... params) {
        Cursor cursor = mContext.getContentResolver()
                .query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, mProjection,
                        null, null, MediaStore.Images.Media.DATE_ADDED);

        Map<String, AlbumImage> folderMap = new HashMap<>();

        if (cursor != null) {
            if (cursor.moveToLast())
                do {
                    int id = cursor.getInt(cursor.getColumnIndex(mProjection[0]));
                    String path = cursor.getString(cursor.getColumnIndex(mProjection[1]));
                    String folderName = cursor.getString(cursor.getColumnIndex(mProjection[2]));
                    File file = new File(path);
                    if (file.exists()) {
                        Image image = new Image(id, path);
                        AlbumImage imageAlbum = folderMap.get(folderName);
                        if (imageAlbum == null) {
                            imageAlbum = new AlbumImage(folderName);
                            folderMap.put(folderName, imageAlbum);
                        }
                        imageAlbum.getImages().add(image);
                    }
                } while (cursor.moveToPrevious());
            cursor.close();


        }

        if (!folderMap.isEmpty())
            mFoldersList = new ArrayList<>(folderMap.values());

        return mFoldersList;
    }

    @Override
    protected void onPostExecute(List<AlbumImage> imageAlba) {
        Message ems = new Message();
        ems.obj = imageAlba;
        handler.sendMessage(ems);
        for (AlbumImage imageAlbum : imageAlba) {
            DuongLog.loge(getClass(), imageAlbum.toString());
//            Log.e("leuleu", imageAlbum.toString());

        }
    }
}
