package com.duong3f.adaptor;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import com.duong3f.mvp.imagetogif.SelectImageFormFolderAcivity;
import com.duong3f.obj.Image;
import com.group3f.gifmaker.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by d on 9/21/2017.
 */

public class AdaptorImageSelected extends ArrayAdapter<Image> {
    SelectImageFormFolderAcivity selectImageFormFolderAcivity;
    ArrayList<Image> images;
    LayoutInflater inflater;

    public AdaptorImageSelected(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<Image> objects) {
        super(context, resource, objects);
        selectImageFormFolderAcivity = (SelectImageFormFolderAcivity) context;
        images = objects;
        inflater = LayoutInflater.from(context);
    }

    ImageView item_image_view_imv;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = inflater.inflate(R.layout.item_image_view_selected, parent, false);
        item_image_view_imv = (ImageView) convertView.findViewById(R.id.item_image_view_imv);
//        imFirstImageInFolder = convertView.findViewById(R.id.item_folder_image_imv);
//        folderName.setText(imageAlba.get(position).getName());
//        String pathFirstImage = imageAlba.get(position).getImages().get(0).getPath();
//        Picasso.with(selectImageFormFolderAcivity)
//                .load(new File(images.get(position).getPath()))
//                .resize(100, 100)
//                .centerCrop()
//                .noFade()
//                .placeholder(R.drawable.ic_btn_from_mutil_image)
//                .into(item_image_view_imv);
//        ImageLoader imageLoader = ImageLoader.getInstance();
//        imageLoader.displayImage("file://" + images.get(position).getPath(), item_image_view_imv);
//        item_image_view_imv.setImageBitmap(decodeScaledBitmapFromSdCard(images.get(position).getPath(), 100, 100));
        Picasso.with(selectImageFormFolderAcivity)
                .load("file://" + images.get(position).getPath())
                .resize(90, 90)
                .centerCrop().placeholder(R.drawable.ic_btn_from_mutil_image)
                .into(item_image_view_imv);
        return convertView;

    }

    public Bitmap decodeScaledBitmapFromSdCard(String filePath,
                                               int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(filePath, options);
    }

    public int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            // Choose the smallest ratio as inSampleSize value, this will guarantee
            // a final image with both dimensions larger than or equal to the
            // requested height and width.
            inSampleSize = heightRatio < widthRatio ? heightRatio : widthRatio;
        }

        return inSampleSize;
    }
}
