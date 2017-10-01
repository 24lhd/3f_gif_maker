package com.long3f.views;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by Long on 13/9/2017.
 */

public class ExpandableHeightGridView extends GridView {
    public ExpandableHeightGridView(Context context) {
        super(context);
    }

    public ExpandableHeightGridView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
    }

    public ExpandableHeightGridView(Context context, AttributeSet attributeSet, int i) {
        super(context, attributeSet, i);
    }

    @Override
    public void onMeasure(int i, int i2) {
        super.onMeasure(i, MeasureSpec.makeMeasureSpec(MEASURED_SIZE_MASK, MeasureSpec.AT_MOST));
        getLayoutParams().height = getMeasuredHeight();
    }
}
