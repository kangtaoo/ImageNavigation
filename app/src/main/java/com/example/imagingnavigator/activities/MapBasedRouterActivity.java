package com.example.imagingnavigator.activities;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.example.imagingnavigator.imagingnavigator.R;

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


}
