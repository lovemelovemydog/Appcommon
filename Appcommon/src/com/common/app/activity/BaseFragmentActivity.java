package com.common.app.activity;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.DisplayMetrics;

/**
 * Created by fangzhu on 2015/3/18.
 */
public class BaseFragmentActivity extends FragmentActivity {
    protected int screenWidth = 0, screenHeight = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initScreenWidthHeight();


    }


    private void initScreenWidthHeight () {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        screenWidth = displayMetrics.widthPixels;
        screenHeight = displayMetrics.heightPixels;
    }
}
