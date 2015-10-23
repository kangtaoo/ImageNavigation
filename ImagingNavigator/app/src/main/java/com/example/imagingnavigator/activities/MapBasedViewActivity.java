package com.example.imagingnavigator.activities;

import android.app.Activity;
import android.os.Bundle;

import com.example.imagingnavigator.imagingnavigator.R;

/**
 * Created by zhangxi on 10/23/15.
 *
 * The map based view.
 */
public class MapBasedViewActivity extends Activity{

    private static final String TAG = MapBasedViewActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

}
