package com.autilite.weightlifttracker.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 *  Solution from https://stackoverflow.com/questions/18997729/listview-same-height-as-content/24186596#24186596
 */

public class ExtendableListView extends ListView {

    public ExtendableListView(Context context) {
        super(context);
    }

    public ExtendableListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ExtendableListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
