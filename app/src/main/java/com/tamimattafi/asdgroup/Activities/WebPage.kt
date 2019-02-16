package com.tamimattafi.asdgroup.Activities

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v4.content.res.ResourcesCompat
import android.view.MenuItem
import android.view.View
import android.webkit.WebResourceError
import com.tamimattafi.asdgroup.R
import kotlinx.android.synthetic.main.activity_web_page.*
import android.webkit.WebView
import android.webkit.WebResourceRequest
import android.webkit.WebViewClient

class WebPage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_web_page)
        setSupportActionBar(web_toolbar)
        supportActionBar!!.title = ""
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        web_toolbar.navigationIcon = ResourcesCompat.getDrawable(resources,R.drawable.ic_back,null)

        //Get news category
        web_category.text = intent.getStringExtra("category")
        //Get news link
        web_webView.loadUrl(intent.getStringExtra("link"))
        //Enable javaScript for webView
        web_webView.settings.javaScriptEnabled = true
        web_webView.webViewClient = object : WebViewClient() {
            override fun onReceivedError(view: WebView?, request: WebResourceRequest?, error: WebResourceError?) {
                super.onReceivedError(view, request, error)
                web_error.visibility = View.VISIBLE
                web_webView.visibility = View.GONE
            }
            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)
                web_progressBar.visibility = View.GONE
            }
            override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
                view.loadUrl(request.url.toString())
                return false
            }
        }
    }

    //Toolbar Back button
    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        if (item != null) {
            onBackPressed()
            finish()
        }
        return super.onOptionsItemSelected(item)
    }

    //Phone back button
    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }
}
