package com.long3f.activity;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.group3f.gifmaker.R;
import com.long3f.utils.AppCache;
import com.long3f.utils.AppUtils;

public class SettingActivity extends AppCompatActivity {

    ImageView btnBack;
    RelativeLayout btnGifSize, btnNumberFrame;
    TextView txtGifSize, txtNumberFrame;
    AlertDialog alert = null;
    RelativeLayout linearLayout;
    NumberPicker aNumberPicker ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().hide();
        txtGifSize = (TextView) findViewById(R.id.txt_gif_size_summary);
        txtNumberFrame = (TextView) findViewById(R.id.txt_gif_number_frame_summary);
        btnBack = (ImageView) findViewById(R.id.btn_back);
        btnGifSize = (RelativeLayout) findViewById(R.id.size_of_gif);
        btnNumberFrame = (RelativeLayout) findViewById(R.id.number_of_frame);

        String max_size_summary = getResources().getString(R.string.max_size_summary)
                .replace("%d",
                        String.valueOf(AppCache
                                .getInstance(getApplicationContext())
                                .getMaxSettingGifSize()).toString());
        String max_frame_summary = getResources().getString(R.string.max_frame_summary)
                .replace("%d",
                        String.valueOf(AppCache
                                .getInstance(getApplicationContext())
                                .getMaxSettingFrame()).toString());

        txtGifSize.setText(max_size_summary);
        txtNumberFrame.setText(max_frame_summary);

        btnGifSize.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectGifSize();
            }
        });
        btnNumberFrame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectNumberFrame();
            }
        });
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }

    private void selectNumberFrame() {
        aNumberPicker = new NumberPicker(getApplicationContext());
        aNumberPicker.setMaxValue(300);
        aNumberPicker.setMinValue(1);
        aNumberPicker.setValue(AppCache
                .getInstance(getApplicationContext())
                .getMaxSettingFrame());
        AppUtils.setNumberPickerTextColor(aNumberPicker, Color.BLACK);
        final FrameLayout layout = new FrameLayout(getApplicationContext());
        layout.addView(aNumberPicker, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT,
                Gravity.CENTER));
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(SettingActivity.this);
        alertDialogBuilder.setTitle(R.string.max_frame_of_gif);
        alertDialogBuilder.setView(layout);
        alertDialogBuilder
                .setCancelable(true)
                .setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                AppCache.getInstance(getApplicationContext()).setMaxSettingFrame(aNumberPicker.getValue());
                                txtNumberFrame.setText(getResources().getString(R.string.max_frame_summary)
                                        .replace("%d", String.valueOf(aNumberPicker.getValue())));
                            }
                        })
                .setNegativeButton(R.string.cancel,
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                alert.dismiss();
                            }
                        });
        alert = alertDialogBuilder.create();
        alert.show();
    }

    private void selectGifSize() {
        final int size = AppCache.getInstance(getApplicationContext()).getMaxSettingGifSize();
        int check;
        if (size == 350) {
            check = 0;
        } else if (size == 400) {
            check = 1;
        } else {
            check = 2;
        }
        AlertDialog.Builder builder = new AlertDialog.Builder(SettingActivity.this);
        builder.setTitle(R.string.out_size)
                .setSingleChoiceItems(R.array.outputSize, check, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch (i){
                            case 0:
                                AppCache.getInstance(getApplicationContext()).setMaxSettingGifSize(350);
                                txtGifSize.setText(getResources().getString(R.string.max_size_summary)
                                        .replace("%d", String.valueOf(350)));
                                break;
                            case 1:
                                AppCache.getInstance(getApplicationContext()).setMaxSettingGifSize(400);
                                txtGifSize.setText(getResources().getString(R.string.max_size_summary)
                                        .replace("%d", String.valueOf(400)));
                                break;
                            case 2:
                                AppCache.getInstance(getApplicationContext()).setMaxSettingGifSize(450);
                                txtGifSize.setText(getResources().getString(R.string.max_size_summary)
                                        .replace("%d", String.valueOf(450)));
                                break;
                        }
                        alert.dismiss();
                    }
                })

                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                });

        alert = builder.create();
        alert.show();

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.slide_out_left, R.anim.slide_in_left);
    }
}
