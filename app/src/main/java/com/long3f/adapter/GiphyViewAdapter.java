package com.long3f.adapter;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.group3f.gifmaker.R;
import com.long3f.Interface.OnGifPlayListener;
import com.long3f.Model.GiphyModel;
import com.long3f.views.TouchImageView;
import com.squareup.picasso.Picasso;
import com.xw.repo.BubbleSeekBar;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InterruptedIOException;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

import pl.droidsonroids.gif.GifDrawable;

/**
 * Created by Admin on 6/10/2017.
 */

public class GiphyViewAdapter extends PagerAdapter {

    private Activity _activity;
    private String rootDir = Environment.getExternalStorageDirectory()
            + File.separator + "GifMaker/.data";
    public ArrayList<GiphyModel> giphyModels;
    private LayoutInflater inflater;
    private int currentPositionFrame = 1;
    public OnGifPlayListener onGifPlayListener = null;

    // constructor
    public GiphyViewAdapter(Activity activity, ArrayList<GiphyModel> listUrl) {
        this._activity = activity;
        this.giphyModels = listUrl;
    }

    public void setOnPlayListener(OnGifPlayListener onPlayListener) {
        this.onGifPlayListener = onPlayListener;
    }


    @Override
    public int getCount() {
        return this.giphyModels.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == ((RelativeLayout) object);
    }


    @Override
    public Object instantiateItem(ViewGroup container, final int position) {

        final ViewHolder viewHolder = new ViewHolder();
        inflater = (LayoutInflater) _activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View viewLayout = inflater.inflate(R.layout.layout_detail_gif_view, container,
                false);

        viewHolder.imgDisplay = (TouchImageView) viewLayout.findViewById(R.id.imgDisplay);
        viewHolder.currentFrame = viewLayout.findViewById(R.id.current_frame);
        viewHolder.totalFrame = viewLayout.findViewById(R.id.total_frame);
        viewHolder.bubbleSeekBar = viewLayout.findViewById(R.id.seekbar_gif_detail);
        viewHolder.linearLayoutFrame = viewLayout.findViewById(R.id.frame);
        viewHolder.ic_gif = viewLayout.findViewById(R.id.ic_gif);
        viewHolder.imgbound = viewLayout.findViewById(R.id.splash_bound_button_gif);

        viewHolder.ic_gif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View view) {
                Animation rotate = AnimationUtils.loadAnimation(_activity, R.anim.clockwise);
                viewHolder.imgbound.startAnimation(rotate);
                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... voids) {
                        File file = new File(rootDir,giphyModels.get(position).getId() + ".gif");
                        if(file.exists()){
                            return null;
                        }
                        downloadGif(giphyModels.get(position).getDownloadUrl(), giphyModels.get(position).getId() + ".gif");
                        return null;
                    }

                    @Override
                    protected void onPreExecute() {
                        super.onPreExecute();
                    }

                    @Override
                    protected void onPostExecute(Void aVoid) {
                        super.onPostExecute(aVoid);
                        viewHolder.imgbound.clearAnimation();
                        viewHolder.ic_gif.setVisibility(View.GONE);
                        playDownloaded(viewHolder,rootDir + "/" +giphyModels.get(position).getId()+".gif" );
                    }
                }.execute();
            }
        });

        Picasso.with(_activity).load(giphyModels.get(position).getPreviewUrl()).into(viewHolder.imgDisplay);
        Log.e("url_gif", giphyModels.get(position).getPreviewUrl());
        viewHolder.ic_gif.setVisibility(View.VISIBLE);
        viewHolder.bubbleSeekBar.setVisibility(View.GONE);

        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }

    private void playDownloaded(final ViewHolder viewHolder, final String pathGif) {
        try {
            final GifDrawable gifFromPath = new GifDrawable(pathGif);
            viewHolder.bubbleSeekBar.getConfigBuilder()
                    .min(1)
                    .max(gifFromPath.getNumberOfFrames())
                    .sectionCount(1)
                    .build();
            viewHolder.totalFrame.setText(String.valueOf(gifFromPath.getNumberOfFrames()));
            viewHolder.imgDisplay.setImageDrawable(gifFromPath);
            play(viewHolder, gifFromPath, pathGif);
            if (this.onGifPlayListener != null) {
                this.onGifPlayListener.onGifState(gifFromPath, pathGif);
            }
            viewHolder.imgDisplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    play(viewHolder, gifFromPath,pathGif);
                }
            });
            viewHolder.bubbleSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
                @Override
                public void onProgressChanged(int progress, float progressFloat) {
                    currentPositionFrame = gifFromPath.getCurrentFrameIndex();
                    viewHolder.currentFrame.setText(String.valueOf(progress));
                    gifFromPath.seekToFrame(progress);
                    if (onGifPlayListener != null) {
                        onGifPlayListener.onGifState(gifFromPath, pathGif);
                    }
                }

                @Override
                public void getProgressOnActionUp(int progress, float progressFloat) {

                }

                @Override
                public void getProgressOnFinally(int progress, float progressFloat) {

                }
            });


        } catch (IOException e) {
            e.printStackTrace();
        }

    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    void play(ViewHolder viewHolder, GifDrawable gifFromPath, String gifpath) {
        if (viewHolder.isShow) {
            viewHolder.isShow = !viewHolder.isShow;
            if (gifFromPath.canPause()) {
                currentPositionFrame = gifFromPath.getCurrentFrameIndex();
                gifFromPath.pause();
                gifFromPath.seekToFrame(currentPositionFrame);
            }
            viewHolder.currentFrame.setText(String.valueOf(gifFromPath.getCurrentFrameIndex()));
            viewHolder.bubbleSeekBar.setProgress(gifFromPath.getCurrentFrameIndex());
            viewHolder.bubbleSeekBar.setVisibility(View.VISIBLE);
            viewHolder.linearLayoutFrame.setVisibility(View.VISIBLE);
            viewHolder.ic_gif.setVisibility(View.VISIBLE);
            if (this.onGifPlayListener != null) {
                this.onGifPlayListener.onGifState(gifFromPath,gifpath);
            }
        } else {
            viewHolder.isShow = !viewHolder.isShow;
            gifFromPath.start();
            gifFromPath.seekToFrame(currentPositionFrame);
            viewHolder.linearLayoutFrame.setVisibility(View.GONE);
            viewHolder.bubbleSeekBar.setVisibility(View.GONE);
            viewHolder.ic_gif.setVisibility(View.GONE);
            if (this.onGifPlayListener != null) {
                this.onGifPlayListener.onGifState(gifFromPath, gifpath);
            }
        }

    }


    public class ViewHolder {
        public TouchImageView imgDisplay;
        public LinearLayout linearLayoutFrame;
        public TextView currentFrame, totalFrame;
        public BubbleSeekBar bubbleSeekBar;
        public RelativeLayout ic_gif;
        public ImageView imgbound;
        public boolean isShow = false;

    }

    void downloadGif(String urlgif, String filename) {
        int count;
        try {
            File rootFile = new File(rootDir);
            if (rootFile.exists()) {

            } else {
                rootFile.mkdir();
            }

            URL url = new URL(urlgif);
            URLConnection conection = url.openConnection();
            conection.connect();
            InputStream input = new BufferedInputStream(url.openStream(), 8192);
            OutputStream output = new FileOutputStream(new File(rootDir, filename));
            Log.e("Download file", rootDir + "/" + filename);
            byte data[] = new byte[1024];
            long total = 0;
            while ((count = input.read(data)) != -1) {
                total += count;
                output.write(data, 0, count);
            }
            output.flush();
            output.close();
            input.close();


        } catch (InterruptedIOException e) {

        } catch (Exception e) {
            Log.e("Error: ", e.getMessage());
        }
    }


}
