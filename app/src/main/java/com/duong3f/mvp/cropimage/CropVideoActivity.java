package com.duong3f.mvp.cropimage;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.duong3f.config.Config;
import com.duong3f.module.DuongLog;
import com.duong3f.module.MyEditCrop;
import com.group3f.gifmaker.R;
import com.long3f.activity.EditGifActivity;

import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.littlecheesecake.croplayout.EditableImage;
import me.littlecheesecake.croplayout.handler.OnBoxChangedListener;
import me.littlecheesecake.croplayout.model.ScalableBox;


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
    private String sizeCrop;
    private File currentFile;
    private File flagFile;
    private EditableImage image;

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
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_crop_video);
        ButterKnife.bind(this);
        currentFile = new File(EditGifActivity.currentPathFile);
        flagFile = new File(currentFile.getParent() + "/flag." + FilenameUtils.getExtension(EditGifActivity.currentPathFile));
        try {
            Config.copy(currentFile, flagFile);
            DuongLog.e(getClass(), flagFile.getPath());
        } catch (IOException e) {
            DuongLog.e(getClass(), e.getMessage());
        }
        setImageToCropViewFromPath(flagFile.getPath());
    }

    public void setImageToCropViewFromPath(String pathFile) {
        image = new EditableImage(pathFile);
        ScalableBox box = new ScalableBox(0, 0, 200,200);
//        image.setBox(box);
//        layoutCropEditCropview.removeAllViews();
        layoutCropEditCropview = findViewById(R.id.layout_crop_edit_cropview);
//        layoutCropEditCropview = new MyEditCrop(this);
        layoutCropEditCropview.initView(this, image);
        layoutCropEditCropview.setOnBoxChangedListener(new OnBoxChangedListener() {
            @Override
            public void onChanged(int x1, int y1, int x2, int y2) {
                sizeCrop = "" + (x2 - x1) + ":" + (y2 - y1) + ":" + x1 + ":" + y1 + "";
                DuongLog.e(getClass(), sizeCrop);
            }
        });
    }

    public void setImageToCropViewFromDrawable(int id) {
        EditableImage image = new EditableImage(this, id);
        ScalableBox box = new ScalableBox(0, 0, image.getViewWidth() / 2, image.getViewHeight() / 2);
        image.setBox(box);
        layoutCropEditCropview.initView(this, image);
        layoutCropEditCropview.setOnBoxChangedListener(new OnBoxChangedListener() {
            @Override
            public void onChanged(int x1, int y1, int x2, int y2) {
                sizeCrop = "" + (x2 - x1) + ":" + (y2 - y1) + ":" + x1 + ":" + y1 + "";
                DuongLog.e(getClass(), sizeCrop);
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
//                DuongLog.e(CropVideoActivity.this.getClass(), sizeCrop);
                Config.runFFmpegCommandCallback(
                        Config.getCommandFlipHolizontal(flagFile.getPath(), flagFile.getPath()),
                        this,
                        new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (Config.onSuccess == msg.what) {
                                    DuongLog.e(CropVideoActivity.this.getClass(), "done getCommandFlipHolizontal");
                                    Toast.makeText(CropVideoActivity.this, "done getCommandFlipHolizontal", Toast.LENGTH_SHORT).show();
//                                    Picasso.with(CropVideoActivity.this).load(new File(flagFile.getPath())).into(layoutCropEditCropview.getImageView());
                                    layoutCropEditCropview.editableImage = new EditableImage(flagFile.getPath());
                                    layoutCropEditCropview.refeshView();
                                }


                            }
                        }
                );

                break;
            case R.id.layout_crop_layout_flip_vertical:
                Config.runFFmpegCommandCallback(
                        Config.getCommandFlipVertical(flagFile.getPath(), flagFile.getPath()),
                        this,
                        new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (Config.onSuccess == msg.what) {
//                                    Picasso.with(CropVideoActivity.this).load(new File(flagFile.getPath())).into(layoutCropEditCropview.getImageView());
//                                    me.littlecheesecake.croplayout.EditableImage editableImage = new EditableImage(flagFile.getPath());
//                                    //re-calculate and draw selection box
//                                    editableImage.getBox().setX1(10);
//                                    editableImage.getBox().setY1(10);
//                                    editableImage.getBox().setX2(editableImage.getActualSize()[0]);
//                                    editableImage.getBox().setY2(editableImage.getActualSize()[1]);
//                                    layoutCropEditCropview.selectionView.setBoxSize(editableImage, editableImage.getBox(), editableImage.getViewWidth(), editableImage.getViewHeight());
//                                    layoutCropEditCropview.getImageView().setImageBitmap(editableImage.getOriginalImage());
                                    EditableImage image = new EditableImage(flagFile.getPath());
                                    ScalableBox box = new ScalableBox(0, 0, 100, 100);
                                    image.setBox(box);
                                    layoutCropEditCropview.editableImage = image;
                                    layoutCropEditCropview.refeshView();
                                    layoutCropEditCropview.selectionView.updateOriginalBox();
                                    Toast.makeText(CropVideoActivity.this, "done getCommandFlipVertical", Toast.LENGTH_SHORT).show();
                                }
                            }

                        }
                );
                break;
            case R.id.layout_crop_layout_rotate:
//                DuongLog.e(CropVideoActivity.this.getClass(), sizeCrop);
                layoutCropEditCropview.rotateImageView();
//                Config.runFFmpegCommandCallback(
//                        Config.getCommandRotation(flagFile.getPath(), flagFile.getPath()),
//                        this,
//                        new Handler() {
//                            @Override
//                            public void handleMessage(Message msg) {
//                                if (Config.onSuccess == msg.what) {
//                                    Toast.makeText(CropVideoActivity.this, "done getCommandRotation", Toast.LENGTH_SHORT).show();
////                                    Picasso.with(CropVideoActivity.this).load(new File(flagFile.getPath())).into(layoutCropEditCropview.getImageView());
//                                    layoutCropEditCropview.editableImage = new EditableImage(flagFile.getPath());
//                                    layoutCropEditCropview.refeshView();
//                                }
////                                    DuongLog.e(CropVideoActivity.this.getClass(), "done getCommandRotation");
//
//                            }
//
//                        }
//                );
                break;
            case R.id.layout_crop_layout_reset:
                break;
            case R.id.layout_tool_flip:
                break;
            case R.id.layout_crop_imv_ratio_free:
                break;
            case R.id.layout_crop_imv_ratio_1_1:
                break;
            case R.id.layout_crop_imv_ratio_3_4:
                break;
            case R.id.layout_crop_imv_ratio_4_3:
                break;
            case R.id.layout_crop_imv_ratio_9_16:
                break;
            case R.id.layout_crop_imv_ratio_16_9:
                break;
            case R.id.layout_tool_ratio:

                break;
            case R.id.layout_crop_txt_cancel:
                finish();
                break;
            case R.id.layout_crop_txt_apply:
                DuongLog.e(CropVideoActivity.this.getClass(), sizeCrop);
                Config.runFFmpegCommandCallback(
                        Config.getCommandCrop(flagFile.getPath(), flagFile.getPath(), sizeCrop),
                        this,
                        new Handler() {
                            @Override
                            public void handleMessage(Message msg) {
                                if (Config.onSuccess == msg.what) {
                                    DuongLog.e(CropVideoActivity.this.getClass(), "done CommandCrop");
                                    Toast.makeText(CropVideoActivity.this, "done CommandCrop", Toast.LENGTH_SHORT).show();
//                                    layoutCropEditCropview.removeAllViews();
//                                    layoutCropEditCropview.imageView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
//                                    layoutCropEditCropview.selectionView.setLayoutParams(new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));
//                                    layoutCropEditCropview.addView(layoutCropEditCropview.imageView, 0);
//                                    layoutCropEditCropview.addView(layoutCropEditCropview.selectionView, 1);
//                                    layoutCropEditCropview.initView(CropVideoActivity.this, image);
//                                    image = new EditableImage(flagFile.getPath());
//                                    layoutCropEditCropview.selectionView.setBoxSize(image, image.getBox(), 10, 10);
//                                    Picasso.with(CropVideoActivity.this).load(new File(flagFile.getPath())).into(layoutCropEditCropview.getImageView());
//                                    me.littlecheesecake.croplayout.EditableImage editableImage = new EditableImage(flagFile.getPath());
//                                    //re-calculate and draw selection box
//                                    editableImage.getBox().setX1(10);
//                                    editableImage.getBox().setY1(10);
//                                    editableImage.getBox().setX2(editableImage.getActualSize()[0]);
//                                    editableImage.getBox().setY2(editableImage.getActualSize()[1]);
//                                    layoutCropEditCropview.selectionView.setBoxSize(editableImage, editableImage.getBox(), editableImage.getViewWidth(), editableImage.getViewHeight());
//                                    layoutCropEditCropview.getImageView().setImageBitmap(editableImage.getOriginalImage());
                                    EditableImage image = new EditableImage(flagFile.getPath());
//                                    ScalableBox box = new ScalableBox(0, 0, 100, 100);
//                                    image.setBox(box);
                                    layoutCropEditCropview.editableImage = image;
                                    layoutCropEditCropview.refeshView();
                                    layoutCropEditCropview.selectionView.updateOriginalBox();

                                }
                            }
                        }
                );
                break;
            case R.id.layout_crop_edit_cropview:
                break;
        }
    }
}
