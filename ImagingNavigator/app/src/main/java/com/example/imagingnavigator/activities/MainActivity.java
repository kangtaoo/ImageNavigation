package com.example.imagingnavigator.activities;

import android.app.Activity;
import android.os.Bundle;

import com.example.imagingnavigator.imagingnavigator.R;

public class MainActivity extends Activity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }



}
