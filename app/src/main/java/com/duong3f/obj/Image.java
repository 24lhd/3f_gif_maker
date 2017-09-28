package com.duong3f.obj;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by d on 9/21/2017.
 */

public class Image implements Parcelable {
    private int mId;
    private String mPath;

    public Image(int id, String path) {
        mId = id;
        mPath = path;
    }

    public static final Creator<Image> CREATOR = new Creator<Image>() {
        @Override
        public Image createFromParcel(Parcel in) {
            return new Image(in);
        }

        @Override
        public Image[] newArray(int size) {
            return new Image[size];
        }
    };

    public int getId() {
        return mId;
    }

    public String getPath() {
        return mPath;
    }

    protected Image(Parcel in) {
        mId = in.readInt();
        mPath = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(mId);
        dest.writeString(mPath);
    }


    @Override
    public String toString() {
        return "Image{" +
                "mId=" + mId +
                ", mPath='" + mPath + '\'' +
                '}';
    }
}