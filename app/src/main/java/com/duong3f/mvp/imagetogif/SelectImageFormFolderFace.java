package com.duong3f.mvp.imagetogif;

import android.view.View;

import com.duong3f.obj.AlbumImage;
import com.duong3f.obj.Image;

import java.util.ArrayList;

/**
 * Created by d on 9/19/2017.
 */

public interface SelectImageFormFolderFace {


    public void hideButtomActionBar();

    public void showButtomActionBar();

    public void setListFolderImage(ArrayList<AlbumImage> folderImage);

    public void showListFolderImage();

    public void showListImageSelect();

    public void setListImageFromFolder(ArrayList<Image> images);

    public void setListImageSelcet();

    public void setTextTitleActionBar(String str);

    public void addListImageSelcet(Image image);

    public void selectDone();

    public void backToMain(View view);


}