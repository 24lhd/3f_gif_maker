package com.long3f.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.duong3f.config.Config;
import com.duong3f.module.DuongLog;
import com.duong3f.mvp.cropimage.CropVideoActivity;
import com.group3f.gifmaker.R;
import com.long3f.activity.EditGifActivity;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment01 extends Fragment {
    int miniSecond = 100;

    TextView btnImageManager;
    SeekBar seekBar;
    public static RunSeekBar runSeekBar;
    @Bind(R.id.btn_manager_images)
    TextView btnManagerImages;
    @Bind(R.id.view_between_btn_manager_images_btn_adjustment)
    View viewBetweenBtnManagerImagesBtnAdjustment;
    @Bind(R.id.txt_crop)
    TextView txtCrop;
    @Bind(R.id.view_between_btn_manager_images_btn_remove_bg)
    View viewBetweenBtnManagerImagesBtnRemoveBg;
    @Bind(R.id.action_bar_control_play_fragment)
    RelativeLayout actionBarControlPlayFragment;
    @Bind(R.id.currentFrame)
    TextView currentFrame;
    @Bind(R.id.seekbar_play_controler)
    SeekBar seekbarPlayControler;
    @Bind(R.id.btn_forward_mode)
    LinearLayout btnForwardMode;
    @Bind(R.id.btn_repeat_mode)
    LinearLayout btnRepeatMode;

    private int i = 0;
    private LinearLayout btnForward, btnRepeat;

    public EditFragment01() {
    }

    public EditFragment01(int miniSecondDelay) {
        this.miniSecond = miniSecondDelay;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_edit_fragment01, container, false);
        btnImageManager = view.findViewById(R.id.btn_manager_images);
        currentFrame = view.findViewById(R.id.currentFrame);
        seekBar = view.findViewById(R.id.seekbar_play_controler);
        btnForward = view.findViewById(R.id.btn_forward_mode);
        btnRepeat = view.findViewById(R.id.btn_repeat_mode);
        btnForward.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setForwardOrRepeat(1);
            }
        });
        btnRepeat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setForwardOrRepeat(2);
            }
        });

        btnImageManager.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                EditGifActivity.imageManager.setVisibility(View.VISIBLE);
                Animation slide_up = AnimationUtils.loadAnimation(getActivity(),
                        R.anim.slide_up);

                EditGifActivity.imageManager.startAnimation(slide_up);
            }
        });

        seekBar.getProgressDrawable().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        seekBar.getThumb().setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_IN);
        seekBar.setMax(EditGifActivity.listFrame.size() - 1);

        runSeekBar = new RunSeekBar();
        runSeekBar.start();
        setForwardOrRepeat(1);
        EditGifActivity.arrPathFile = EditGifActivity.listFrame;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean b) {
                if (EditGifActivity.arrBitmapCropped != null && EditGifActivity.arrBitmapCropped.size() > 0) {
                    if (position < EditGifActivity.arrBitmapCropped.size()) {
                        currentFrame.setText(String.valueOf(position + 1));
                        EditGifActivity.mGLImageView.setImageBitmap(EditGifActivity.arrBitmapCropped.get(position));
                        EditGifActivity.currentPathFile = EditGifActivity.arrBitmapCropped.get(position);
                        EditGifActivity.indexPathFile = position;
                    } else {
                        currentFrame.setText(String.valueOf(1));
                        EditGifActivity.mGLImageView.setImageBitmap(EditGifActivity.arrBitmapCropped.get(0));
                    }
                } else {
                    if (position < EditGifActivity.listFrame.size()) {
                        currentFrame.setText(String.valueOf(position + 1));
//                    Bitmap bitmap = BitmapFactory.decodeFile(EditGifActivity.listFrame.get(position), options);
                        EditGifActivity.mGLImageView.setImageBitmap(EditGifActivity.listFrame.get(position));
                        EditGifActivity.currentPathFile = EditGifActivity.listFrame.get(position);
                        EditGifActivity.indexPathFile = position;
                    } else {
                        currentFrame.setText(String.valueOf(1));
//                    Bitmap bitmap = BitmapFactory.decodeFile(EditGifActivity.listFrame.get(0), options);
                        EditGifActivity.mGLImageView.setImageBitmap(EditGifActivity.listFrame.get(0));
                    }
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                runSeekBar.pause();
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                i = seekBar.getProgress();
                if (runSeekBar.isRunning()) {
                    runSeekBar.start();
                }

            }
        });
        ButterKnife.bind(this, view);
        return view;
    }

    public void setForwardOrRepeat(int i) {
        if (this.btnForward != null && this.btnRepeat != null) {
            switch (i) {
                case 1:
                    this.btnForward.setSelected(true);
                    this.btnRepeat.setSelected(false);
                    runSeekBar.setRepeat(false);
                    return;
                case 2:
                    this.btnForward.setSelected(false);
                    this.btnRepeat.setSelected(true);
                    runSeekBar.setRepeat(true);
                    return;
                default:
                    return;
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        ButterKnife.unbind(this);
        runSeekBar.stop();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();
        runSeekBar.start();
    }

    @OnClick(R.id.txt_crop)
    public void onViewClicked() {
        startActivityForResult(new Intent(getActivity(), CropVideoActivity.class), Config.REQUEST_CODE_CROP_BITMAP);
    }

    public class RunSeekBar implements Runnable {
        boolean isRepeat = false;
        boolean turningLeft = true;
        boolean isRunning = false;
        Handler mHandler = new Handler();
        int miniSecond = 100;

        @Override
        public void run() {
            seekBar.setProgress(i);
            seekBar.setMax(EditGifActivity.listFrame.size() - 1);
            isRunning = true;
            if (!isRepeat) {
                i++;
                if (i >= EditGifActivity.listFrame.size()) {
                    i = 0;
                }
            } else {
                if (turningLeft) {
                    i++;
                    if (i >= EditGifActivity.listFrame.size() - 1) {
                        turningLeft = false;
                    }

                } else {
                    i--;
                    if (i == 0) {
                        turningLeft = true;
                    }
                }
            }
            mHandler.postDelayed(this, miniSecond);
        }

        public void stop() {
            mHandler.removeCallbacks(this);
            isRunning = false;
        }
        public void pause() {
            mHandler.removeCallbacks(this);
        }

        public void start() {
            mHandler.post(this);
            isRunning = true;

        }

        public void setDelay(int miniSecond) {
            this.miniSecond = miniSecond;
        }

        public boolean isRepeat() {
            return isRepeat;
        }

        public void setRepeat(boolean repeat) {
            isRepeat = repeat;
            turningLeft = true;
        }

        public boolean isRunning() {
            return isRunning;
        }
    }

}
