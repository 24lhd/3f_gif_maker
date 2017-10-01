package com.duong3f.mvp.imagetogif;

import android.os.Handler;

import com.duong3f.obj.AlbumImage;

import java.util.ArrayList;

/**
 * Created by d on 9/21/2017.
 */

public interface SelectImageFormFolderHandling {
    public void getFolderImage(Handler handler);

    public void showListFolderImage();

    public void showListImageInFolder();

    public void showListImageSelect();

    public void setListFolderImage(ArrayList<AlbumImage> folderImage);

    public void setListImageInFolder(AlbumImage albumImage);
}
