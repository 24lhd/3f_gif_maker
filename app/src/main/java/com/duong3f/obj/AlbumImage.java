package com.duong3f.obj;

import java.util.ArrayList;

/**
 * Created by d on 9/21/2017.
 */

public class AlbumImage {

    private String mName;
    private ArrayList<Image> mImages;// = new ArrayList<>();

    public AlbumImage(String name) {
        mName = name;
        mImages = new ArrayList<>();
    }

    public String getName() {
        return mName;
    }

    public ArrayList<Image> getImages() {
        return mImages;
    }

    @Override
    public String toString() {
        return "ImageAlbum{" +
                "mName='" + mName + '\'' +
                ", mImages=" + mImages.toString() +
                '}';
    }

}
