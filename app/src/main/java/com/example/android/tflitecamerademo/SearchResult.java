package com.example.android.tflitecamerademo;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class SearchResult extends Activity {
    WebView webView;
    static final String url = "https://www.google.com/search?q=";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.search_layout);
        Intent mIntent = getIntent();
        String keyword = mIntent.getStringExtra("result");
        String url_path = url + keyword +" wikipedia";
        webView = (WebView) findViewById(R.id.webView);
        WebSettings webSettings= webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.loadUrl(url_path);
        webView.setWebViewClient(new WebViewClient());

    }
}
