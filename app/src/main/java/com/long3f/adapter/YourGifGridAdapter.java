package com.long3f.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.group3f.gifmaker.R;
import com.long3f.Interface.OnYourGifAdapterChanged;
import com.long3f.activity.GifDetailActivity;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by Long on 13/9/2017.
 */

public class YourGifGridAdapter extends RecyclerView.Adapter<YourGifGridAdapter.ViewHolder> {
    private Context mContext;
    public ArrayList<String> paths;
    public static OnYourGifAdapterChanged onYourGifAdapterChanged;

    public YourGifGridAdapter(Context activity, boolean isYourGif) {
        this.mContext = activity;
        this.paths = getAllShownImagesPath((Activity) activity, isYourGif, ".gif");
    }

    public void setOnYourGifAdapterChanged(OnYourGifAdapterChanged onYourGifAdapterChanged) {
        this.onYourGifAdapterChanged = onYourGifAdapterChanged;
    }

    public void updateList(Context activity, boolean isYourGif) {
        this.paths = getAllShownImagesPath((Activity) activity, isYourGif, ".gif");
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_grid_your_gif, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        File file = new File(paths.get(position));
        GifDrawable gifFromPath = null;
        try {
            gifFromPath = new GifDrawable(file);
            viewHolder.imageView.setImageDrawable(gifFromPath);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Date lastModDate = new Date(file.lastModified());
        DateTimeFormatter dateTimeFormatter = DateTimeFormat.shortDate();
        viewHolder.date.setText(new DateTime(lastModDate).toString(dateTimeFormatter));
        final int pos = position;
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, GifDetailActivity.class);
                intent.putExtra("position", pos);
                intent.putStringArrayListExtra("list", paths);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    @Override
    public long getItemId(int paramInt) {
        return 0L;
    }

    @Override
    public int getItemCount() {
        return paths.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.txt_date);
            imageView = itemView.findViewById(R.id.img_gif);

        }
    }


    private ArrayList<String> getAllShownImagesPath(Activity activity, boolean isYourGif, String typeFile) {
        Uri uri;
        Cursor cursor;
        int column_index_data, column_index_folder_name;
        ArrayList<String> listOfAllImages = new ArrayList<String>();
        String absolutePathOfImage = null;
        File file = null;
        uri = android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA,
                MediaStore.Images.Media.BUCKET_DISPLAY_NAME};

        cursor = activity.getContentResolver().query(uri, projection, null,
                null, null);

        column_index_data = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
        column_index_folder_name = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.BUCKET_DISPLAY_NAME);
        while (cursor.moveToNext()) {
            file = new File(cursor.getString(column_index_data));
            absolutePathOfImage = file.getAbsolutePath();
            if (file.isFile() && absolutePathOfImage.endsWith(typeFile)) {
                if (isYourGif) {
                    if (absolutePathOfImage.contains("videotogif")) {
                        listOfAllImages.add(file.getAbsolutePath());
                        Log.e("isYourGif: ", absolutePathOfImage);
                    }
                } else {
                    if (!absolutePathOfImage.contains("videotogif")) {
                        listOfAllImages.add(file.getAbsolutePath());
                        Log.e("getAllShownImagesPath: ", absolutePathOfImage);
                    }
                }
            }

        }
        Collections.reverse(listOfAllImages);
        return listOfAllImages;
    }
}