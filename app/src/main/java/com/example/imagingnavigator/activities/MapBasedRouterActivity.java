package com.example.imagingnavigator.activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.imagingnavigator.R;

/**
 *
 */
public class MapBasedRouterActivity extends Activity {

    private static final String TAG = MapBasedViewActivity.class.getSimpleName();

    private WebView webView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_based_router);
        setupLayout();
        System.out.print("1");
    }

    private void setupLayout() {
        webView = (WebView) findViewById(R.id.map_based_view_webview);

        WebSettings webSettings = webView.getSettings();
        // allow file access
        webSettings.setAllowFileAccess(true);
        // allow zoom setting
        webSettings.setBuiltInZoomControls(true);
        webSettings.setJavaScriptEnabled(true);

        webView.setWebViewClient(new WebViewClient());

        webView.loadUrl("http://maps.google.com/maps?" + "saddr=43.0054446,-87.9678884" + "&daddr=42.9257104,-88.0508355");

    }

    /**
     *  Callback function for on-click camera button
     *  Will open camera based view
     */
    public void onClickCamera(View view){
        startCameraBasedView();
    }

    /**
     * Start the Camera based navigator
     */
    private void startCameraBasedView(){
        Intent intent = new Intent();
        intent.setClass(MapBasedRouterActivity.this, CameraBasedViewActivity.class);
        startActivity(intent);
    }

    /**
     *  Callback function for on-click map button
     *  Will return map based view
     */
    public void onClickMap(View view){
        startMapBasedView();
    }

    /**
     * Start the Camera based navigator
     */
    private void startMapBasedView(){
        Intent intent = new Intent();
        intent.setClass(MapBasedRouterActivity.this,MapBasedViewActivity.class);
        startActivity(intent);
    }



}
