package com.duong3f.module;

import android.content.Context;
import android.graphics.Bitmap;

import com.duong3f.config.Config;

import me.littlecheesecake.croplayout.model.ScalableBox;
import me.littlecheesecake.croplayout.util.ImageHelper;

/**
 * Created by d on 10/1/2017.
 */
public class MyEditableImage {
    public Bitmap originalImage;
    public ScalableBox originalBox;
    public int viewWidth;
    public int viewHeight;

    public void setOriginalImage(Bitmap originalImage) {
        this.originalImage = originalImage;
    }

    public void setOriginalBox(ScalableBox originalBox) {
        this.originalBox = originalBox;
    }

    public MyEditableImage(String localPath) {
        //load image from path to bitmap
        originalImage = ImageHelper.getBitmapFromPath(localPath);

        //init the search box
        originalBox = new ScalableBox();
    }

    public MyEditableImage(Context context, int id) {
        originalImage = ImageHelper.getBitmapFromResource(context.getResources(), id);

        //init the search box
        originalBox = new ScalableBox();
    }

    public void setViewSize(int viewWidth, int viewHeight) {
        this.viewWidth = viewWidth;
        this.viewHeight = viewHeight;
    }

    public Bitmap getOriginalImage() {
        return originalImage;
    }

    public void setBox(ScalableBox box) {
        this.originalBox = box;
    }

    public ScalableBox getBox() {
        return originalBox;
    }

    public void rotateOriginalImage(int degree) {
        originalImage = ImageHelper.rotateImage(originalImage, degree);
    }

    public int getViewWidth() {
        return viewWidth;
    }

    public void setViewWidth(int viewWidth) {
        this.viewWidth = viewWidth;
    }

    public int getViewHeight() {
        return viewHeight;
    }

    public void setViewHeight(int viewHeight) {
        this.viewHeight = viewHeight;
    }

    /**
     * get the size of the bitmap when it is fit to view display
     *
     * @return width and height as int[]
     */
    public int[] getFitSize() {
        int[] fitSize = new int[2];

        float ratio = originalImage.getWidth() / (float) originalImage.getHeight();
        float viewRatio = viewWidth / (float) viewHeight;

        //width dominate, fit w
        if (ratio > viewRatio) {
            float factor = viewWidth / (float) originalImage.getWidth();
            fitSize[0] = viewWidth;
            fitSize[1] = (int) (originalImage.getHeight() * factor);

        } else {
            //height dominate, fit h
            float factor = viewHeight / (float) originalImage.getHeight();
            fitSize[0] = (int) (originalImage.getWidth() * factor);
            fitSize[1] = viewHeight;
        }

        return fitSize;
    }

    /**
     * get actual size of the image
     *
     * @return int array size[0] is width, size[1] is height
     */
    public int[] getActualSize() {
        int[] actualSize = new int[2];
        actualSize[0] = originalImage.getWidth();
        actualSize[1] = originalImage.getHeight();
        return actualSize;
    }

    public String cropOriginalImage(String path, String imageName) {
        ScalableBox relativeBox = getBox();
        return ImageHelper.saveImageCropToPath(originalImage,
                relativeBox.getX1(), relativeBox.getY1(), relativeBox.getX2(), relativeBox.getY2(),
                path, imageName
        );
    }

    public String cropBitapAndSave(Bitmap bitmap, String path, String imageName, ScalableBox scalableBox) {
        return Config.saveImageCropToPath(bitmap,
                scalableBox.getX1(), scalableBox.getY1(), scalableBox.getX2(), scalableBox.getY2(),
                path, imageName
        );
    }

    public void saveEditedImage(String path) {
        ImageHelper.saveImageToPath(originalImage, path);
    }

}
