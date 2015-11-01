package com.example.imagingnavigator.activities;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

import com.example.imagingnavigator.imagingnavigator.R;

public class MapBasedRouterActivity extends Activity {

    private static final String TAG = MapBasedViewActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map_based_router);
        setupLayout();

    }

    private void setupLayout() {
        WebView myWebView = (WebView) findViewById(R.id.map_based_view_webview);
        myWebView.loadUrl("http://www.example.com");
    }


}
