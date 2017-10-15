package com.duong3f.mvp.cropimage;

import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.Toast;

import com.duong3f.config.Config;
import com.duong3f.module.MyEditCrop;
import com.duong3f.module.MyEditableImage;
import com.group3f.gifmaker.R;
import com.long3f.activity.EditGifActivity;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.littlecheesecake.croplayout.handler.OnBoxChangedListener;
import me.littlecheesecake.croplayout.model.ScalableBox;
import me.littlecheesecake.croplayout.util.ImageHelper;


/**
 * Created by d on 9/27/2017.
 */

public class CropVideoActivity extends AppCompatActivity {
    //  compile 'me.littlecheesecake:croplayout:1.0.5'
    @Bind(R.id.layout_tool_flip)
    LinearLayout layoutToolFlip;
    @Bind(R.id.layout_tool_ratio)
    LinearLayout layoutToolRatio;
    @Bind(R.id.layout_crop_edit_cropview)
    MyEditCrop layoutCropEditCropview;
    @Bind(R.id.seek_bar_size)
    SeekBar seekBarSize;
    private String sizeCrop;
    private File currentFile;
    private File flagFile;
    private MyEditableImage editableImage;
    private ScalableBox scalableBox;

    /*
        - crop video, image, gif
        - command:
                ffmpeg.exe -i ic_app.png -filter:v "crop=223:264:233:131" out.png
             223:264 wight:height của đầu ra
             233:131 x:y của vị trí cắt




             1. Flip video  vertically:

ffmpeg -y -i INPUT -vf vflip  OUTPUT
2. Flip video horizontally:

ffmpeg -y  -i in-%03d.png -vf hflip  out-%03d.png
3. Rotate 90 degrees clockwise:

ffmpeg -y  -i INPUT -vf transpose=1 OUTPUT
4. Rotate 90 degrees counterclockwise:

ffmpeg -y  -i INPUT -vf transpose=2 OUTPUT
         */

    String pathFlag = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES) + "/GifEditorFlag/";
    String pathFile = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES) + "/GifEditor/";
    public ArrayList<Bitmap> arrBitmapFile;
    private int positionFile;
    private int size;
    private String scaleType = "free";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_crop_video);
        ButterKnife.bind(this);
        getSupportActionBar().hide();
        arrBitmapFile = new ArrayList<>();
//        currentFile = new File(EditGifActivity.indexPathFile);
//        flagFile = new File(pathFlag + "/flag-" + String.format("%03d",
//                (EditGifActivity.indexPathFile + 1)) + "." + FilenameUtils.getExtension(EditGifActivity.currentPathFile));
//        if (!flagFile.exists()) {
//            flagFile.getParentFile().mkdirs();
//            try {
//                flagFile.createNewFile();
//            } catch (IOException e) {
//            }
//        }
//        try {
//            //String.format("%03d", (imagesSelect.indexOf(image) + 1))
//            for (int i = 0; i < EditGifActivity.currentPathFiles.size(); i++) {
//                File fileCopy = new File(pathFlag + "/flag-" + String.format("%03d", (i + 1)) + "." + FilenameUtils.getExtension(EditGifActivity.currentPathFile));
//                Config.copy(new File(EditGifActivity.currentPathFiles.get(i)), fileCopy);
//                listFileFlag.add(fileCopy.getPath());
//            }
//            DuongLog.e(getClass(), flagFile.getPath());
//        } catch (IOException e) {
//            DuongLog.e(getClass(), e.getMessage());
//        }
        positionFile = EditGifActivity.indexPathFile;
        setImageToCropViewFromPath(EditGifActivity.arrPathFile.get(positionFile));
        int max = editableImage.getOriginalImage().getWidth();
        if (max > editableImage.getOriginalImage().getHeight())
            max = editableImage.getOriginalImage().getHeight();
        seekBarSize.setMax(max);
        seekBarSize.setProgress(50);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            seekBarSize.setMin(50);
        }
        seekBarSize.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                size = i;
                updateBox(scalableBox.getX1(),
                        scalableBox.getY1(), scalableBox.getX1(), scalableBox.getY1());
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        reset();
    }

    private void reset() {
        new Thread() {
            @Override
            public void run() {
                initArrayBitmap();
            }
        }.start();
//        layoutCropEditCropview.reset(EditGifActivity.arrPathFile.get(positionFile));
        layoutCropEditCropview.resetBitmap(EditGifActivity.arrPathFile.get(positionFile));
    }

    synchronized void initArrayBitmap() {
        new File(pathFlag).mkdirs();
        try {
            FileUtils.cleanDirectory(new File(pathFlag));
        } catch (IOException e) {
            e.printStackTrace();
        }
        arrBitmapFile.clear();
//        for (String pathFile : EditGifActivity.arrPathFile)
//            arrBitmapFile.add(Config.getBitmapFromPath(pathFile));
        arrBitmapFile.addAll(EditGifActivity.listFrame);
    }

    public void setImageToCropViewFromPath(Bitmap pathFile) {
        editableImage = new MyEditableImage(pathFile);
        scalableBox = new ScalableBox(0, 0, 300, 300);
        editableImage.setBox(scalableBox);
        layoutCropEditCropview.initView(this, editableImage);
        layoutCropEditCropview.setOnBoxChangedListener(new OnBoxChangedListener() {
            @Override
            public void onChanged(int x1, int y1, int x2, int y2) {
                scalableBox = new ScalableBox(x1, y1, x2, y2);
                updateBox(x1, y1, x2, y2);
            }
        });
    }

    private void updateBox(int x1, int y1, int x2, int y2) {
        switch (scaleType) {
            case "11":
                layoutCropEditCropview.refeshSelectionViewScale(x1, y1, size + x1, size + y1);
                break;
            case "34":
                layoutCropEditCropview.refeshSelectionViewScale(x1, y1, size + x1, (size * 3 / 4) + y1);
                break;
            case "43":
                layoutCropEditCropview.refeshSelectionViewScale(x1, y1, size + x1, (size * 4 / 3) + y1);
                break;
            case "169":
                layoutCropEditCropview.refeshSelectionViewScale(x1, y1, size + x1, (size * 16 / 9) + y1);
                break;
            case "916":
                layoutCropEditCropview.refeshSelectionViewScale(x1, y1, size + x1, (size * 9 / 16) + y1);
                break;
            default:
                layoutCropEditCropview.refeshSelectionViewScale(x1, y1, x2, y2);
        }
    }

    public void setImageToCropViewFromDrawable(int id) {
        MyEditableImage image = new MyEditableImage(this, id);
        ScalableBox box = new ScalableBox(0, 0, image.getViewWidth() / 2, image.getViewHeight() / 2);
        image.setBox(box);
        layoutCropEditCropview.initView(this, image);
        layoutCropEditCropview.setOnBoxChangedListener(new OnBoxChangedListener() {
            @Override
            public void onChanged(int x1, int y1, int x2, int y2) {
//                sizeCrop = "" + (x2 - x1) + ":" + (y2 - y1) + ":" + x1 + ":" + y1 + "";
//                DuongLog.e(getClass(), sizeCrop);
            }
        });
    }

    @OnClick({R.id.layout_crop_imv_ratio, R.id.layout_crop_layout_flip_horizontal, R.id.layout_crop_layout_flip_vertical, R.id.layout_crop_layout_rotate, R.id.layout_crop_layout_reset, R.id.layout_tool_flip, R.id.layout_crop_imv_ratio_free, R.id.layout_crop_imv_ratio_1_1, R.id.layout_crop_imv_ratio_3_4, R.id.layout_crop_imv_ratio_4_3, R.id.layout_crop_imv_ratio_9_16, R.id.layout_crop_imv_ratio_16_9, R.id.layout_tool_ratio, R.id.layout_crop_txt_cancel, R.id.layout_crop_txt_apply, R.id.layout_bottom, R.id.layout_crop_edit_cropview})
    public void onViewClicked(View view) {
        switch (view.getId()) {
            case R.id.layout_crop_imv_ratio:
                if (layoutToolFlip.getVisibility() == View.VISIBLE) {
                    layoutToolFlip.setVisibility(View.GONE);
                    layoutToolRatio.setVisibility(View.VISIBLE);
                } else {
                    layoutToolFlip.setVisibility(View.VISIBLE);
                    layoutToolRatio.setVisibility(View.GONE);
                }
                break;
            case R.id.layout_crop_layout_flip_horizontal:
                try {
                    FileUtils.cleanDirectory(new File(pathFlag));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                new Thread() {
                    @Override
                    public void run() {
                        runFlipHolizontal();
                    }
                }.start();
                layoutCropEditCropview.flipHolizontal();

                break;
            case R.id.layout_crop_layout_flip_vertical:
                try {
                    FileUtils.cleanDirectory(new File(pathFlag));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                layoutCropEditCropview.flipVertical();
                new Thread() {
                    @Override
                    public void run() {
                        runFlipVertical();
                    }
                }.start();
                break;
            case R.id.layout_crop_layout_rotate:
                try {
                    FileUtils.cleanDirectory(new File(pathFlag));
                } catch (IOException e) {
                    e.printStackTrace();
                }
                layoutCropEditCropview.rotateImageView();
                new Thread() {
                    @Override
                    public void run() {
                        runRotate();
                    }
                }.start();
                break;
            case R.id.layout_crop_layout_reset:
                reset();
                break;
            case R.id.layout_crop_imv_ratio_free:
                scaleType = "free";
                updateBox(scalableBox.getX1(),
                        scalableBox.getY1(),
                        editableImage.getOriginalImage().getWidth(),
                        editableImage.getOriginalImage().getHeight());
                break;
            case R.id.layout_crop_imv_ratio_1_1:
                scaleType = "11";
                size = 300;
                seekBarSize.setProgress(300);
                updateBox(scalableBox.getX1(),
                        scalableBox.getY1(), scalableBox.getX1(), scalableBox.getY1());
                break;
            case R.id.layout_crop_imv_ratio_3_4:
                scaleType = "34";
                seekBarSize.setProgress(300);
                size = 300;
                updateBox(scalableBox.getX1(),
                        scalableBox.getY1(), scalableBox.getX1(), scalableBox.getY1());
                break;
            case R.id.layout_crop_imv_ratio_4_3:
                scaleType = "43";
                seekBarSize.setProgress(300);
                size = 300;
                updateBox(scalableBox.getX1(),
                        scalableBox.getY1(), scalableBox.getX1(), scalableBox.getY1());
                break;
            case R.id.layout_crop_imv_ratio_9_16:
                seekBarSize.setProgress(300);
                scaleType = "916";
                size = 300;
                seekBarSize.setProgress(300);
                updateBox(scalableBox.getX1(),
                        scalableBox.getY1(), scalableBox.getX1(), scalableBox.getY1());
                break;
            case R.id.layout_crop_imv_ratio_16_9:
                scaleType = "169";
                seekBarSize.setProgress(300);
                size = 300;
                updateBox(scalableBox.getX1(),
                        scalableBox.getY1(), scalableBox.getX1(), scalableBox.getY1());
                break;
            case R.id.layout_crop_txt_cancel:
                finish();
                break;
            case R.id.layout_crop_txt_apply:
                new File(pathFlag).mkdirs();
                try {
                    FileUtils.cleanDirectory(new File(pathFlag));
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                new Thread() {
//                    @Override
//                    public void run() {
                try {
                    runCrop();
                } catch (Exception e) {
                    Toast.makeText(this, "error!!! " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }

//                saveEdit();
//                    }
//                }.start();
                break;
            case R.id.layout_crop_edit_cropview:
                break;
        }
    }

    public static boolean isRunning = false;


    synchronized void saveEdit() {
        EditGifActivity.arrBitmapCropped = new ArrayList<>();
        EditGifActivity.arrBitmapCropped.addAll(arrBitmapFile);
//        intent.putParcelableArrayListExtra(Config.ARRAY_BITMAP_CROPPED, arrBitmapFile);
        finish();
//        for (int i = 0; i < arrBitmapFile.size(); i++) {
//            Config.saveImageFromBitmap(
//                    arrBitmapFile.get(i)
//                    , pathFlag + "/index-" + String.format("%03d", (i + 1)) + "." + Bitmap.CompressFormat.PNG);
//
//            DuongLog.e(getClass(), "" + pathFlag + "index-" + String.format("%03d", (i + 1)) + "." + Bitmap.CompressFormat.PNG);
//        }
    }

    synchronized void runRotate() {
        for (int i = 0; i < arrBitmapFile.size(); i++)
            arrBitmapFile.set(i, ImageHelper.rotateImage(arrBitmapFile.get(i), 90));
    }

    synchronized void runFlipVertical() {
        for (int i = 0; i < arrBitmapFile.size(); i++)
            arrBitmapFile.set(i, Config.flipVERTICAL(arrBitmapFile.get(i)));
    }

    synchronized void runFlipHolizontal() {

        for (int i = 0; i < arrBitmapFile.size(); i++)
            arrBitmapFile.set(i, Config.flipHORIZONTAL(arrBitmapFile.get(i)));
    }

    synchronized void runCrop() {
        for (int i = 0; i < arrBitmapFile.size(); i++)
            arrBitmapFile.set(i, Config.cropBitmap(arrBitmapFile.get(i), scalableBox));
        saveEdit();
    }

}
