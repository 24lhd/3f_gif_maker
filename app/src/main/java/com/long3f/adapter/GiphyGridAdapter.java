package com.long3f.adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.group3f.gifmaker.R;
import com.long3f.Model.GiphyModel;
import com.long3f.activity.GifDetailActivity;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by Admin on 6/10/2017.
 */

public class GiphyGridAdapter extends RecyclerView.Adapter<GiphyGridAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<GiphyModel> arrUrl;

    public GiphyGridAdapter(Context mContext, ArrayList<GiphyModel> arrUrl) {
        this.mContext = mContext;
        this.arrUrl = arrUrl;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.item_grid_your_gif, parent, false);

        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {

        viewHolder.date.setVisibility(View.GONE);
        Picasso.with(mContext).load(arrUrl.get(position)
                .getPreviewUrl())
                .resize(100,100)
                .placeholder(R.drawable.backgroundgradient)
                .into(viewHolder.imageView);
        final int pos = position;

        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(mContext, GifDetailActivity.class);
                intent.putExtra("position",pos);
                mContext.startActivity(intent);
                ((Activity) mContext).overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
            }
        });
    }

    @Override
    public long getItemId(int paramInt) {
        return 0L;
    }

    @Override
    public int getItemCount() {
        if(arrUrl != null)
            return arrUrl.size();
        else return 0;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView date;
        public ImageView imageView;

        public ViewHolder(View itemView) {
            super(itemView);
            date = itemView.findViewById(R.id.txt_date);
            imageView = itemView.findViewById(R.id.img_gif);

        }
    }

}