package com.long3f.adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.group3f.gifmaker.R;
import com.long3f.Interface.OnGifPlayListener;
import com.long3f.views.TouchImageView;
import com.xw.repo.BubbleSeekBar;

import java.io.IOException;
import java.util.ArrayList;

import pl.droidsonroids.gif.GifDrawable;


/**
 * Created by Admin on 15/9/2017.
 */

public class DetailGifViewAdapter extends PagerAdapter {

    private Activity _activity;
    public ArrayList<String> _imagePaths;
    private LayoutInflater inflater;
    private Context context;
    private int currentPositionFrame = 1;
    public OnGifPlayListener onGifPlayListener = null;

    // constructor
    public DetailGifViewAdapter(Activity activity, ArrayList<String> imagePaths, Context context) {
        this._activity = activity;
        this._imagePaths = imagePaths;
        this.context = context;
    }

    public void removeView(int index) {
        this._imagePaths.remove(index);
        notifyDataSetChanged();
        YourGifGridAdapter.onYourGifAdapterChanged.OnChanged();
    }

    public void setOnPlayListener(OnGifPlayListener onPlayListener) {
        this.onGifPlayListener = onPlayListener;
    }


    @Override
    public int getCount() {
        return this._imagePaths.size();
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


        try {
            final GifDrawable gifFromPath = new GifDrawable(_imagePaths.get(position));
            viewHolder.bubbleSeekBar.getConfigBuilder()
                    .min(1)
                    .max(gifFromPath.getNumberOfFrames())
                    .sectionCount(1)
                    .build();
            viewHolder.totalFrame.setText(String.valueOf(gifFromPath.getNumberOfFrames()));
            viewHolder.imgDisplay.setImageDrawable(gifFromPath);

            play(viewHolder, gifFromPath, position);
            if (this.onGifPlayListener != null) {
                this.onGifPlayListener.onGifState(gifFromPath, _imagePaths.get(position));
            }
            viewHolder.imgDisplay.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    play(viewHolder, gifFromPath, position);
                }
            });
            viewHolder.bubbleSeekBar.setOnProgressChangedListener(new BubbleSeekBar.OnProgressChangedListener() {
                @Override
                public void onProgressChanged(int progress, float progressFloat) {
                    currentPositionFrame = gifFromPath.getCurrentFrameIndex();
                    viewHolder.currentFrame.setText(String.valueOf(progress));
                    gifFromPath.seekToFrame(progress);
//                    if(onGifPlayListener != null) {
//                        onGifPlayListener.onGifState(gifFromPath,_imagePaths.get(position));
//                    }
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
        ((ViewPager) container).addView(viewLayout);

        return viewLayout;
    }


    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        ((ViewPager) container).removeView((RelativeLayout) object);

    }

    @Override
    public int getItemPosition(Object object) {
        return PagerAdapter.POSITION_NONE;
    }

    void play(ViewHolder viewHolder, GifDrawable gifFromPath, int position) {
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
                this.onGifPlayListener.onGifState(gifFromPath, _imagePaths.get(position));
            }
        } else {
            viewHolder.isShow = !viewHolder.isShow;
            gifFromPath.start();
            gifFromPath.seekToFrame(currentPositionFrame);
            viewHolder.linearLayoutFrame.setVisibility(View.GONE);
            viewHolder.bubbleSeekBar.setVisibility(View.GONE);
            viewHolder.ic_gif.setVisibility(View.GONE);
            if (this.onGifPlayListener != null) {
                this.onGifPlayListener.onGifState(gifFromPath, _imagePaths.get(position));
            }
        }

    }


    public class ViewHolder {
        public TouchImageView imgDisplay;
        public LinearLayout linearLayoutFrame;
        public TextView currentFrame, totalFrame;
        public BubbleSeekBar bubbleSeekBar;
        public ImageView ic_gif;
        public boolean isShow = false;

    }
}