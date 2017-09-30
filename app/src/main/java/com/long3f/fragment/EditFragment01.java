package com.long3f.fragment;


import android.app.Fragment;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.SeekBar;
import android.widget.TextView;

import com.duong3f.mvp.cropimage.CropVideoActivity;
import com.group3f.gifmaker.R;
import com.long3f.activity.EditGifActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;


/**
 * A simple {@link Fragment} subclass.
 */
public class EditFragment01 extends Fragment {

    TextView btnImageManager, currentFrame;
    SeekBar seekBar;
    public static RunSeekBar runSeekBar;
    private int i = 0;
    private LinearLayout btnForward, btnRepeat;

    public EditFragment01() {
        // Required empty public constructor
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
                Animation slide_up = AnimationUtils.loadAnimation(getContext(),
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
        EditGifActivity.currentPathFiles = EditGifActivity.listFrame;
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int position, boolean b) {
                currentFrame.setText(String.valueOf(position + 1));
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                Bitmap bitmap = BitmapFactory.decodeFile(EditGifActivity.listFrame.get(position), options);
                EditGifActivity.mGLImageView.setImageBitmap(bitmap);
                EditGifActivity.currentPathFile = EditGifActivity.listFrame.get(position);
                EditGifActivity.indexPathFile = position;

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
    }

    @OnClick(R.id.txt_crop)
    public void onViewClicked() {
        startActivity(new Intent(getActivity(), CropVideoActivity.class));
//        EditGifActivity.layoutCrop.setVisibility(View.VISIBLE);
//        Animation slide_up = AnimationUtils.loadAnimation(getContext(),
//                R.anim.slide_up);
//        EditGifActivity.layoutCrop.startAnimation(slide_up);
    }


    public class RunSeekBar implements Runnable {
        int miniSecond = 100;
        boolean isRepeat = false;
        boolean turningLeft = true;
        boolean isRunning = false;
        Handler mHandler = new Handler();

        @Override
        public void run() {
            seekBar.setProgress(i);
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
