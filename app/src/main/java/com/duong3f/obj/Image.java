package com.duong3f.obj;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by d on 9/21/2017.
 */

public class Image implements Parcelable {
    private int mId;
    private String mPath;
    private boolean isSelect;

    public int getId() {
        return mId;
    }

    public void setId(int mId) {
        this.mId = mId;
    }

    public String getPath() {
        return mPath;
    }

    public void setPath(String mPath) {
        this.mPath = mPath;
    }

    public boolean isSelect() {
        return isSelect;
    }

    public void setSelect(boolean select) {
        isSelect = select;
    }

    public static Creator<Image> getCREATOR() {
        return CREATOR;
    }

    public Image(int mId, String mPath, boolean isSelect) {

        this.mId = mId;
        this.mPath = mPath;
        this.isSelect = isSelect;
    }

    protected Image(Parcel in) {
        mId = in.readInt();
        mPath = in.readString();
        isSelect = in.readByte() != 0;
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeInt(mId);
        parcel.writeString(mPath);
        parcel.writeByte((byte) (isSelect ? 1 : 0));
    }


//    public static final Creator<Image> CREATOR = new Creator<Image>() {
//        @Override
//        public Image createFromParcel(Parcel in) {
//            return new Image(in);
//        }
//
//        @Override
//        public Image[] newArray(int size) {
//            return new Image[size];
//        }
//    };

}