package com.funapps.spainiptv

import android.os.Build
import android.os.Bundle
import android.view.Window
import android.view.WindowManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.funapps.spainiptv.util.SharedPrefs


class InfoActivity : AppCompatActivity() {
    private lateinit var webView: WebView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_info)

        /*val w: Window = window
        w.setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )*/

        val shared = SharedPrefs(this)
        val sharedURL = shared.getURL()
        val url = if(sharedURL.isEmpty()) "https://www.google.com" else sharedURL//"https://es.wikipedia.org/wiki/Televisi%C3%B3n_digital_terrestre_en_Espa%C3%B1a"
        webView = findViewById(R.id.webview)
        webView.webViewClient = WebViewClient()
        //webSettings.allowFileAccessFromFileURLs = true
        //webSettings.allowUniversalAccessFromFileURLs = true
        webView.loadUrl(url)
    }
}