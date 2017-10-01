package com.duong3f.adaptor;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.duong3f.mvp.imagetogif.SelectImageFormFolderAcivity;
import com.duong3f.obj.AlbumImage;
import com.group3f.gifmaker.R;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by d on 9/21/2017.
 */

public class AdaptorFolderImageListView extends ArrayAdapter<AlbumImage> {
    ImageLoader imageLoader;
    Context context;
    SelectImageFormFolderAcivity selectImageFormFolderAcivity;
    ArrayList<AlbumImage> imageAlba;
    LayoutInflater inflater;

    public AdaptorFolderImageListView(@NonNull Context context, @LayoutRes int resource, @NonNull ArrayList<AlbumImage> objects) {
        super(context, resource, objects);
        this.context = context;
        selectImageFormFolderAcivity = (SelectImageFormFolderAcivity) context;
        imageAlba = objects;
        inflater = LayoutInflater.from(context);
        imageLoader = ImageLoader.getInstance();
    }

    ImageView imFirstImageInFolder;
    TextView folderName;

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        convertView = inflater.inflate(R.layout.item_folder_image, parent, false);
        folderName = convertView.findViewById(R.id.item_folder_image_txt_folder_name);
        imFirstImageInFolder = (ImageView) convertView.findViewById(R.id.item_folder_image_imv);
        folderName.setText(imageAlba.get(position).getName() + " (" + imageAlba.get(position).getImages().size() + ")");
        String pathFirstImage = imageAlba.get(position).getImages().get(0).getPath();
        Picasso.with(selectImageFormFolderAcivity)
                .load(new File(pathFirstImage)).resize(90, 90).centerCrop().placeholder(R.drawable.ic_btn_from_mutil_image)
                .into(imFirstImageInFolder);
//        imageLoader.displayImage("file://" + pathFirstImage, imFirstImageInFolder);
//        Glide.with(context)
//                .load(new File(pathFirstImage)).centerCrop()
////                .transition(GenericTransitionOptions.with(R.anim.fade_out))
//                .into(imFirstImageInFolder);
        return convertView;
    }
}
