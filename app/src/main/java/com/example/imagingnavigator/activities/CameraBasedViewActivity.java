package com.example.imagingnavigator.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import com.example.imagingnavigator.function.CameraView;
import com.example.imagingnavigator.R;

/**
 * Created by zhangxi on 10/23/15.
 *
 * The camera based view.
 */
public class CameraBasedViewActivity extends Activity {

    private static final String TAG = CameraBasedViewActivity.class.getSimpleName();

    private Camera camera;
    private CameraView cameraView;


    private ImageView imageView;
    float angle = 0.0F;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_based_view);


        try{
            this.camera = Camera.open();
        }catch(Exception e){
            e.printStackTrace();
        }

        if(this.camera != null){
            this.cameraView = new CameraView(this, this.camera);
            FrameLayout camera_view = (FrameLayout)findViewById(R.id.camera_view);

            camera_view.addView(this.cameraView);
        }

        imageView = (ImageView)findViewById(R.id.camera_based_vew_navigation_arrow);

    }

    @Override
    public void onPause(){
//        sm.unregisterListener(myListener);
        super.onPause();
    }

    /**
     * Stop the camera based view navigator, and go back to the map based.
     */
    private void backMapBasedView() {
        setResult(RESULT_CANCELED);
        finish();
    }

    /**
     * Callback function for on-click map button
     * Will start map based router
     * */
    public void onClickRouterMap(View view){
        startMapBasedRouter();
    }

    // This function is just for image rotation test, need to be removed latter
    public void onClickNavigationArrow(View view){
        this.angle = (float)(Math.random()*360);
        Log.i(TAG, "will rotate arrow by angle: " + angle + " degree.");
        this.imageView.setRotation(angle);
    }

    public void startMapBasedRouter(){
        Intent intent = new Intent();
        intent.setClass(this, MapBasedRouterActivity.class);
        startActivity(intent);
    }



}