package com.duong3f.module;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.duong3f.config.Config;
import com.group3f.gifmaker.R;

import me.littlecheesecake.croplayout.handler.OnBoxChangedListener;
import me.littlecheesecake.croplayout.model.ScalableBox;
import me.littlecheesecake.croplayout.util.ImageHelper;

/**
 * Created by d on 9/28/2017.
 */


public class MyEditCrop extends FrameLayout {

    public static final int LINE_WIDTH = 2;
    public static final int CORNER_LENGTH = 30;

    public Context context;

    public ImageView imageView;
    public MySelectView selectionView;
    public MyEditableImage editableImage;

    public float lineWidth;
    public float cornerWidth;
    public float cornerLength;
    public int lineColor;
    public int cornerColor;
    public int shadowColor;

    public MyEditCrop(Context context) {
        super(context);
        this.context = context;
    }

    public MyEditCrop(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        obtainAttributes(context, attrs);
    }

    public MyEditCrop(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.context = context;
        obtainAttributes(context, attrs);
    }

    public static int getLineWidth() {
        return LINE_WIDTH;
    }

    public float getCornerWidth() {
        return cornerWidth;
    }

    public static int getCornerLength() {
        return CORNER_LENGTH;
    }

    public int getLineColor() {
        return lineColor;
    }

    public int getCornerColor() {
        return cornerColor;
    }

    public int getShadowColor() {
        return shadowColor;
    }


    public ImageView getImageView() {
        return imageView;
    }

    public MySelectView getSelectionView() {
        return selectionView;
    }

    public MyEditableImage getEditableImage() {
        return editableImage;
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        //set the default image and selection view
        if (editableImage != null) {
            editableImage.setViewSize(w, h);
            imageView.setImageBitmap(editableImage.getOriginalImage());
            selectionView.setBoxSize(editableImage, editableImage.getBox(), w, h);
        }
        super.onSizeChanged(w, h, oldw, oldh);
    }

    /**
     * update view with editable image
     *
     * @param context       activity
     * @param editableImage image to be edited
     */
    public void initView(Context context, MyEditableImage editableImage) {
        this.editableImage = editableImage;
        selectionView = new MySelectView(context,
                lineWidth, cornerWidth, cornerLength,
                lineColor, cornerColor, shadowColor, editableImage);
        imageView = new ImageView(context);
        imageView.setLayoutParams(new FrameLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        selectionView.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        addView(imageView, 0);
        addView(selectionView, 1);
    }

    public void setOnBoxChangedListener(OnBoxChangedListener onBoxChangedListener) {
        selectionView.setOnBoxChangedListener(onBoxChangedListener);
    }

    /**
     * rotate image
     */
    public void rotateImageView() {
        //rotate bitmap
        editableImage.rotateOriginalImage(90);
        //re-calculate and draw selection box
        editableImage.getBox().setX1(0);
        editableImage.getBox().setY1(0);
        editableImage.getBox().setX2(editableImage.getActualSize()[0]);
        editableImage.getBox().setY2(editableImage.getActualSize()[1]);
        selectionView.setBoxSize(editableImage, editableImage.getBox(), editableImage.getViewWidth(), editableImage.getViewHeight());

        //set bitmap as view
        imageView.setImageBitmap(editableImage.getOriginalImage());
    }

    public void flipVertical() {
        //rotate bitmap
        editableImage.setOriginalImage(Config.flipVERTICAL(editableImage.getOriginalImage()));
        //re-calculate and draw selection box
        editableImage.getBox().setX1(0);
        editableImage.getBox().setY1(0);
        editableImage.getBox().setX2(editableImage.getActualSize()[0]);
        editableImage.getBox().setY2(editableImage.getActualSize()[1]);
        selectionView.setBoxSize(editableImage, editableImage.getBox(), editableImage.getViewWidth(), editableImage.getViewHeight());
        //set bitmap as view
        imageView.setImageBitmap(editableImage.getOriginalImage());
    }

    public void flipHolizontal() {
        //rotate bitmap
        editableImage.setOriginalImage(Config.flipHORIZONTAL(editableImage.getOriginalImage()));
        //re-calculate and draw selection box
        editableImage.getBox().setX1(0);
        editableImage.getBox().setY1(0);
        editableImage.getBox().setX2(editableImage.getActualSize()[0]);
        editableImage.getBox().setY2(editableImage.getActualSize()[1]);
        selectionView.setBoxSize(editableImage, editableImage.getBox(), editableImage.getViewWidth(), editableImage.getViewHeight());
        //set bitmap as view
        imageView.setImageBitmap(editableImage.getOriginalImage());
    }

    public void refeshView(String localPath) {
        MyEditableImage editableImage = new MyEditableImage(localPath);
//        selectionView = new MySelectView(context,
//                lineWidth, cornerWidth, cornerLength,
//                lineColor, cornerColor, shadowColor, editableImage);
        imageView.setImageBitmap(editableImage.getOriginalImage());
    }

    public void reset(String localPath) {
        editableImage.setOriginalImage(ImageHelper.getBitmapFromPath(localPath));
        editableImage.getBox().setX1(10);
        editableImage.getBox().setY1(10);
        editableImage.getBox().setX2(editableImage.getActualSize()[0]);
        editableImage.getBox().setY2(editableImage.getActualSize()[1]);
        selectionView.setBoxSize(editableImage, editableImage.getBox(), editableImage.getViewWidth(), editableImage.getViewHeight());
        selectionView.setBoxSize(editableImage, editableImage.getBox(), editableImage.getViewWidth(), editableImage.getViewHeight());
        imageView.setImageBitmap(editableImage.getOriginalImage());

    }

    public void refeshView() {
        editableImage.getBox().setX2(200);
        editableImage.getBox().setY2(300);
        selectionView.setBoxSize(editableImage, editableImage.getBox(), editableImage.getViewWidth(), editableImage.getViewHeight());
    }

    public void refeshSelectionView(int x, int y, int w, int h) {
        ScalableBox scalableBox = new ScalableBox(x, y, w, h);
        editableImage.setBox(scalableBox);
//        editableImage.getBox().setX2(w);
//        editableImage.getBox().setY2(h);
        selectionView.setBoxSize(editableImage, editableImage.getBox(), editableImage.getViewWidth(), editableImage.getViewHeight());
    }

    public void refeshSelectionViewScale(int x, int y, int w, int h) {
        editableImage.getBox().setX1(x);
        editableImage.getBox().setY1(y);
        editableImage.getBox().setX2(w);
        editableImage.getBox().setY2(h);
        selectionView.setBoxSize(editableImage, editableImage.getBox(), editableImage.getViewWidth(), editableImage.getViewHeight());
    }

    public void refeshSelectionView(int w, int h) {
        editableImage.getBox().setX2(w);
        editableImage.getBox().setY2(h);
        selectionView.setBoxSize(editableImage, editableImage.getBox(), editableImage.getViewWidth(), editableImage.getViewHeight());
    }

    private void obtainAttributes(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.CropLayout);

        lineWidth = ta.getDimension(R.styleable.CropLayout_crop_line_width, dp2px(LINE_WIDTH));
        lineColor = ta.getColor(R.styleable.CropLayout_crop_line_color, Color.parseColor("#ffffff"));
        cornerWidth = ta.getDimension(R.styleable.CropLayout_crop_corner_width, dp2px(LINE_WIDTH * 2));
        cornerLength = ta.getDimension(R.styleable.CropLayout_crop_corner_length, dp2px(CORNER_LENGTH));
        cornerColor = ta.getColor(R.styleable.CropLayout_crop_corner_color, Color.parseColor("#ffffff"));
        shadowColor = ta.getColor(R.styleable.CropLayout_crop_shadow_color, Color.parseColor("#aa111111"));
    }

    protected int dp2px(float dp) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }
}
