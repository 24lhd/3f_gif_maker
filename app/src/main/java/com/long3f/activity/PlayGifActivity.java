package com.long3f.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.group3f.gifmaker.R;
import com.xw.repo.BubbleSeekBar;

import java.io.File;
import java.util.ArrayList;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


/**
 * Created by LONGG on 10/9/2017.
 */

public class PlayGifActivity extends AppCompatActivity {

    private ImageView gifViewer;
    private static BubbleSeekBar seekbar_play_controler;
    private static final String FILEPATH = "filepath";
    private ArrayList<String> f;
    private ScheduledExecutorService scheduler;
    Future<?> future;
    static int i = 1;
    static float delay;
    private Handler mHandler = new Handler();
    private Runnable mUpdateTimeTask;
    private TextView currentFrame;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test);
        gifViewer = (ImageView) findViewById(R.id.gifViewer);
        seekbar_play_controler = (BubbleSeekBar) findViewById(R.id.seekbar_play_controler);
        currentFrame = (TextView) findViewById(R.id.currentFrame);
        String filePath = getIntent().getStringExtra(FILEPATH);
        int duration = getIntent().getIntExtra("duration", 50);
        f = new ArrayList<String>();

        File dir = new File(filePath);
        File[] listFile;

        listFile = dir.listFiles();

        for (File e : listFile) {
            f.add(e.getAbsolutePath());
            Log.e("path", e.getAbsolutePath());
        }
        if (f.size() != 0) {
            delay = duration / f.size();
        }

        seekbar_play_controler.getConfigBuilder()
                .min(1)
                .max(f.size())
                .sectionCount(1)
                .progress(i)
                .build();
        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    seekbar_play_controler.setProgress(i);
                    Bitmap bmp = BitmapFactory.decodeFile(f.get(i - 1));
                    gifViewer.setImageBitmap(bmp);

//                    Log.e("i = ",i+"");
                    if (i == f.size()) {
                        i = 1;
                    }
                    i++;
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };

        initScheduler(handler, (int) delay);

        seekbar_play_controler.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
            @Override
            public void onProgressChanged(int progress, float progressFloat) {
                i = progress;
                currentFrame.setText(String.valueOf(i));
            }

            @Override
            public void getProgressOnActionUp(int progress, float progressFloat) {

            }

            @Override
            public void getProgressOnFinally(int progress, float progressFloat) {

                i = progress;
                currentFrame.setText(String.valueOf(i));
                resumScheduler(handler, (int) delay);
            }

        });
    }

    void initScheduler(final Handler handler, long miniSecond) {
        scheduler = Executors.newScheduledThreadPool(1);
        future = scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Message ms = new Message();
                ms.obj = i;
                handler.sendMessage(ms);
            }
        }, 1, miniSecond, TimeUnit.MILLISECONDS);


    }

    void pauseScheduler() {
        future.cancel(true);
    }

    void resumScheduler(final Handler handler, long miniSecond) {

        future = scheduler.scheduleAtFixedRate(new Runnable() {
            @Override
            public void run() {
                Message ms = new Message();
                ms.obj = i;
                handler.sendMessage(ms);
            }
        }, 1, miniSecond, TimeUnit.MILLISECONDS);
    }

}
