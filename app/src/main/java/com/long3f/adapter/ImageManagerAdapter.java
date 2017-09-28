package com.long3f.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.group3f.gifmaker.R;
import com.long3f.Interface.OnDragedItem;
import com.long3f.Interface.OnListChangeSize;
import com.long3f.activity.EditGifActivity;
import com.long3f.helper.ItemTouchHelperAdapter;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by Admin on 25/9/2017.
 */

public class ImageManagerAdapter extends RecyclerView.Adapter<ImageManagerAdapter.RecyclerViewHolders>
        implements ItemTouchHelperAdapter {
    private OnDragedItem onDragedItem;
    public static OnListChangeSize onListChangeSize;
    private Context context;
    public static ArrayList<Bitmap> bitmapArrayList;

    public ImageManagerAdapter(Context context, ArrayList<Bitmap> arrayList,OnDragedItem onDragedItem, OnListChangeSize onListChangeSize) {
        this.onDragedItem = onDragedItem;
        this.onListChangeSize = onListChangeSize;
        this.context = context;
        ImageManagerAdapter.bitmapArrayList = arrayList;
    }

    @Override
    public RecyclerViewHolders onCreateViewHolder(ViewGroup parent, int viewType) {

        View layoutView = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_image_manager, null);
        RecyclerViewHolders rcv = new RecyclerViewHolders(layoutView);
        return rcv;
    }


    @Override
    public void onBindViewHolder(final RecyclerViewHolders holder, final int position) {
        holder.imgThumb.setImageBitmap(ImageManagerAdapter.bitmapArrayList.get(position));
    }

    @Override
    public int getItemCount() {
        return EditGifActivity.listFrame.size();
    }

    @Override
    public boolean onItemMove(int fromPosition, int toPosition) {
        Collections.swap(EditGifActivity.listFrame, fromPosition, toPosition);
        Collections.swap(ImageManagerAdapter.bitmapArrayList, fromPosition, toPosition);

        onDragedItem.onItemDragged(fromPosition,toPosition);//call back when moved item

        notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public void onItemDismiss(int position) {
        EditGifActivity.listFrame.remove(position);
        onListChangeSize.onListChangeSize(EditGifActivity.listFrame.size());
        notifyItemRemoved(position);
    }

    public class RecyclerViewHolders extends RecyclerView.ViewHolder implements View.OnClickListener {

        public ImageView imgThumb, imgDel;
        public RecyclerViewHolders(final View itemView) {
            super(itemView);
            imgThumb =  itemView.findViewById(R.id.image_item_view);
            imgDel =  itemView.findViewById(R.id.image_item_remove);
            imgDel.setOnClickListener(this);
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    itemView.setAlpha(0.7f);
                    return false;
                }
            });
        }
        @Override
        public void onClick(View view) {
            if(view.getId() == R.id.image_item_remove){
                EditGifActivity.listFrame.remove(getAdapterPosition());
                ImageManagerAdapter.bitmapArrayList.remove(getAdapterPosition());
                onListChangeSize.onListChangeSize(EditGifActivity.listFrame.size());
                notifyItemRemoved(getAdapterPosition());
            }
        }
    }
}
