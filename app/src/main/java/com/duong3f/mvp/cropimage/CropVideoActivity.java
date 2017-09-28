package com.duong3f.mvp.cropimage;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.group3f.gifmaker.R;

import me.littlecheesecake.croplayout.EditPhotoView;
import me.littlecheesecake.croplayout.EditableImage;
import me.littlecheesecake.croplayout.handler.OnBoxChangedListener;
import me.littlecheesecake.croplayout.model.ScalableBox;


/**
 * Created by d on 9/27/2017.
 */

public class CropVideoActivity extends AppCompatActivity {

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
        final EditPhotoView editPhotoView = (EditPhotoView) findViewById(R.id.layout_crop_edit_cropview);
        final EditableImage image = new EditableImage(this, R.drawable.out_001);
        ScalableBox box = new ScalableBox(50, 50, 100, 100);
        image.setBox(box);
        editPhotoView.initView(this, image);
        editPhotoView.setOnBoxChangedListener(new OnBoxChangedListener() {
            @Override
            public void onChanged(int x1, int y1, int x2, int y2) {
                Log.e("leuleu", "" + (x2 - x1) + ":" + (y2 - y1) + ":" + x1 + ":" + y1 + "");
            }
        });
    }
}
