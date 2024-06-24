package com.example.fasdd_android

import android.annotation.SuppressLint
import android.os.Bundle
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import java.util.Objects

class NewsDetailActivity : AppCompatActivity() {

    companion object {
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_CONTENT = "extra_content"
        const val EXTRA_IMAGE = "extra_image"
        const val EXTRA_URL = "extra_url"
    }
    private var webView: WebView? = null

    @SuppressLint("SetJavaScriptEnabled", "MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_news)

        webView = findViewById(R.id.webView)
        webView?.webViewClient = WebViewClient()
        webView?.settings?.javaScriptEnabled = true

        val url = intent.getStringExtra(EXTRA_URL)
        webView?.loadUrl(url.toString())
    }
}
