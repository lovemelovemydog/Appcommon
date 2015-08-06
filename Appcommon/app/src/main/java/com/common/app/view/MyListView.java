package com.common.app.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ListView;

/**
 * Created by fangzhu on 2015/3/18.
 */
public class MyListView extends ListView {
    /*isMeasure in scrollView to get height*/
    boolean isMeasure = false;

    public MyListView(Context context) {
        super(context);
    }

    public MyListView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyListView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /*get data size to measureSpec of listview height*/
    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (isMeasure) {
            int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,

                    MeasureSpec.AT_MOST);

            super.onMeasure(widthMeasureSpec, expandSpec);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public boolean isMeasure() {
        return isMeasure;
    }

    public void setIsMeasure(boolean isMeasure) {
        this.isMeasure = isMeasure;
    }
}
