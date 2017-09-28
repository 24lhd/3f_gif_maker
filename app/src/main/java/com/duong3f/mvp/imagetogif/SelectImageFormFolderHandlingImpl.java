package com.duong3f.mvp.imagetogif;

import android.os.Handler;
import android.os.Message;

import com.duong3f.module.TaskGetFolderImage;
import com.duong3f.obj.AlbumImage;

import java.util.ArrayList;

/**
 * Created by d on 9/21/2017.
 */

public class SelectImageFormFolderHandlingImpl implements SelectImageFormFolderHandling {


    SelectImageFormFolderAcivity selectImageFormFolderAcivity;
    ArrayList<AlbumImage> folderImages;

    public SelectImageFormFolderHandlingImpl(SelectImageFormFolderAcivity selectImageFormFolderAcivity) {
        this.selectImageFormFolderAcivity = selectImageFormFolderAcivity;
        getFolderImage(new Handler() {
            @Override
            public void handleMessage(Message msg) {
                folderImages = (ArrayList<AlbumImage>) msg.obj;
                setListFolderImage(folderImages);
                showListFolderImage();
                setListImageInFolder(folderImages.get(0));
                showListImageSelect();
            }
        });
    }

    @Override
    public void getFolderImage(Handler handler) {
        new TaskGetFolderImage(selectImageFormFolderAcivity, handler).execute();
    }

    @Override
    public void showListFolderImage() {
        selectImageFormFolderAcivity.showListFolderImage();
    }

    @Override
    public void showListImageInFolder() {
    }

    @Override
    public void showListImageSelect() {
        selectImageFormFolderAcivity.showListImageSelect();
    }

    @Override
    public void setListFolderImage(ArrayList<AlbumImage> folderImage) {
        selectImageFormFolderAcivity.setListFolderImage(folderImage);
    }

    @Override
    public void setListImageInFolder(AlbumImage albumImage) {
        selectImageFormFolderAcivity.setListImageFromFolder(albumImage.getImages());
    }
}
