package com.example.imagingnavigator.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.FrameLayout;

import com.example.imagingnavigator.function.CameraView;
import com.example.imagingnavigator.imagingnavigator.R;

/**
 * Created by zhangxi on 10/23/15.
 *
 * The camera based view.
 */
public class CameraBasedViewActivity extends Activity {

    private static final String TAG = CameraBasedViewActivity.class.getSimpleName();

    private Camera camera;
    private CameraView cameraView;
//    private

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
     * Will start map based view
     * */
    public void onClickMap(View view){
        startMapBasedView();
    }

    public void startMapBasedView(){
        Intent intent = new Intent();
        intent.setClass(this, MapBasedViewActivity.class);
        startActivity(intent);
    }

}