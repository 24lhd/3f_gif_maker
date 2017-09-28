package com.duong3f.mvp.cropimage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.LinearLayout;

import com.duong3f.module.DuongLog;
import com.group3f.gifmaker.R;
import com.long3f.activity.EditGifActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import me.littlecheesecake.croplayout.EditPhotoView;
import me.littlecheesecake.croplayout.EditableImage;
import me.littlecheesecake.croplayout.handler.OnBoxChangedListener;
import me.littlecheesecake.croplayout.model.ScalableBox;


/**
 * Created by d on 9/27/2017.
 */

public class CropVideoActivity extends AppCompatActivity {

    @Bind(R.id.layout_tool_flip)
    LinearLayout layoutToolFlip;
    @Bind(R.id.layout_tool_ratio)
    LinearLayout layoutToolRatio;
    @Bind(R.id.layout_crop_edit_cropview)
    EditPhotoView layoutCropEditCropview;

    /*
        - crop video, image, gif
        - command:
                ffmpeg.exe -i ic_app.png -filter:v "crop=223:264:233:131" out.png
             223:264 wight:height của đầu ra
             233:131 x:y của vị trí cắt
         */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_crop_video);
        ButterKnife.bind(this);
        setImageToCropViewFromPath(EditGifActivity.currentPathFile);
    }

    public void setImageToCropViewFromPath(String pathFile) {
        EditableImage image = new EditableImage(pathFile);
        ScalableBox box = new ScalableBox(0, 0, image.getViewWidth() / 2, image.getViewHeight() / 2);
        image.setBox(box);
        layoutCropEditCropview.initView(this, image);
        layoutCropEditCropview.setOnBoxChangedListener(new OnBoxChangedListener() {
            @Override
            public void onChanged(int x1, int y1, int x2, int y2) {
                DuongLog.loge(getClass(), "" + (x2 - x1) + ":" + (y2 - y1) + ":" + x1 + ":" + y1 + "");
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
                DuongLog.loge(getClass(), "" + (x2 - x1) + ":" + (y2 - y1) + ":" + x1 + ":" + y1 + "");
            }
        });
    }

    @OnClick({R.id.layout_crop_imv_ratio, R.id.layout_crop_layout_flip_horizontal, R.id.layout_crop_layout_flip_vertical, R.id.layout_crop_layout_flip_rotate, R.id.layout_crop_layout_reset, R.id.layout_tool_flip, R.id.layout_crop_imv_ratio_free, R.id.layout_crop_imv_ratio_1_1, R.id.layout_crop_imv_ratio_3_4, R.id.layout_crop_imv_ratio_4_3, R.id.layout_crop_imv_ratio_9_16, R.id.layout_crop_imv_ratio_16_9, R.id.layout_tool_ratio, R.id.layout_crop_txt_cancel, R.id.layout_crop_txt_apply, R.id.layout_bottom, R.id.layout_crop_edit_cropview})
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
                break;
            case R.id.layout_crop_layout_flip_vertical:
                break;
            case R.id.layout_crop_layout_flip_rotate:
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
                break;
            case R.id.layout_crop_edit_cropview:
                break;
        }
    }
}
