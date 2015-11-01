package com.example.imagingnavigator.activities;

import android.app.Activity;
import android.content.Context;
import android.hardware.Camera;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.example.imagingnavigator.imagingnavigator.R;

/**
 * Created by zhangxi on 10/23/15.
 *
 * The camera based view.
 */
public class CameraBasedViewActivity extends Activity {

    private static final String TAG = CameraBasedViewActivity.class.getSimpleName();

    private Camera camera;
//    private

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Stop the camera based view navigator, and go back to the map based.
     */
    private void backMapBasedView() {
        setResult(RESULT_CANCELED);
        finish();
    }

    private boolean safeCameraOpen(int camId){
        boolean camOpened = false;
        try{
            releaseCameraAndPreview();

        }catch (Exception e){

        }

        return camOpened;
    }


    private void releaseCameraAndPreview(){

    }

    public class Preview implements SurfaceHolder.Callback{
        SurfaceView surView;
        SurfaceHolder surHolder;

        Preview(Context context){
//        super(context);

            surView = new SurfaceView(context);

            surHolder = surView.getHolder();
            surHolder.addCallback(this);
            surHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        }

        @Override
        public void surfaceCreated(SurfaceHolder holder){

        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int w, int h){
            // do nothing
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder){

        }

    }

}