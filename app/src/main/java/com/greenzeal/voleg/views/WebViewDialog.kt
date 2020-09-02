package com.greenzeal.voleg.views

import android.annotation.TargetApi
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.ProgressBar
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.greenzeal.voleg.R

class WebViewDialog : DialogFragment() {
    private var toolbar: Toolbar? = null
    private var webView: WebView? = null
    private var progressBar: ProgressBar? = null
    private var url: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL, R.style.AppTheme_FullScreenDialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        super.onCreateView(inflater, container, savedInstanceState)
        val view = inflater.inflate(R.layout.webview_dialog, container, false)
        toolbar = view.findViewById(R.id.toolbar)
        webView = view.findViewById(R.id.web_view)
        progressBar = view.findViewById(R.id.progress_bar)
        webView?.settings?.javaScriptEnabled = true
        loadPage()
        return view
    }

    private fun loadPage() {
        webView!!.loadUrl(url)
        webView!!.webViewClient = webViewClient
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        toolbar!!.setNavigationOnClickListener { v: View? -> dismiss() }
        toolbar!!.setTitle(R.string.news)
        toolbar!!.setOnMenuItemClickListener { item: MenuItem? ->
            dismiss()
            true
        }
    }

    override fun onStart() {
        super.onStart()
        val dialog = dialog
        if (dialog != null) {
            val width = ViewGroup.LayoutParams.MATCH_PARENT
            val height = ViewGroup.LayoutParams.MATCH_PARENT
            if (dialog.window != null) {
                dialog.window!!.setLayout(width, height)
                dialog.window!!.setWindowAnimations(R.style.AppTheme_Slide)
            }
        }
    }

    private val webViewClient: WebViewClient = object : WebViewClient() {
        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
            view.loadUrl(url)
            return true
        }

        @TargetApi(Build.VERSION_CODES.N)
        override fun shouldOverrideUrlLoading(view: WebView, request: WebResourceRequest): Boolean {
            view.loadUrl(request.url.toString())
            return true
        }

        override fun onPageFinished(view: WebView, url: String) {
            super.onPageFinished(view, url)
            if (progressBar != null) progressBar!!.visibility = View.GONE
        }
    }

    companion object {
        private const val TAG = "webview_dialog"
        fun display(fragmentManager: FragmentManager?, url: String?) {
            val agreementDialog = WebViewDialog()
            agreementDialog.url = url
            agreementDialog.show(fragmentManager!!, TAG)
        }
    }
}