package com.long3f.helper;

/**
 * Created by LONGG on 4/4/2017.
 */

public interface ItemTouchHelperAdapter {
    boolean onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
}
