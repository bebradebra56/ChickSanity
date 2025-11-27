package com.chickisa.sofskil.zxcvb.presentation.ui.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Message
import android.util.Log
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.webkit.CookieManager
import android.webkit.PermissionRequest
import android.webkit.URLUtil
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.FrameLayout
import android.widget.Toast
import com.chickisa.sofskil.zxcvb.presentation.app.ChickSanityApplication

class ChickSanityVi(
    private val chickSanityContext: Context,
    private val chickSanityCallback: ChickSanityCallBack,
    private val chickSanityWindow: Window
) : WebView(chickSanityContext) {
    private var chickSanityFileChooserHandler: ((ValueCallback<Array<Uri>>?) -> Unit)? = null
    fun chickSanitySetFileChooserHandler(handler: (ValueCallback<Array<Uri>>?) -> Unit) {
        this.chickSanityFileChooserHandler = handler
    }
    init {
        val webSettings = settings
        webSettings.apply {
            setSupportMultipleWindows(true)
            allowFileAccess = true
            allowContentAccess = true
            domStorageEnabled = true
            javaScriptCanOpenWindowsAutomatically = true
            userAgentString = WebSettings.getDefaultUserAgent(chickSanityContext).replace("; wv)", "").replace("Version/4.0 ", "")
            @SuppressLint("SetJavaScriptEnabled")
            javaScriptEnabled = true
            cacheMode = WebSettings.LOAD_NO_CACHE
        }
        isNestedScrollingEnabled = true



        layoutParams = FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT,
            ViewGroup.LayoutParams.MATCH_PARENT
        )

        super.setWebViewClient(object : WebViewClient() {
            override fun shouldOverrideUrlLoading(
                view: WebView?,
                request: WebResourceRequest?,
            ): Boolean {
                val link = request?.url?.toString() ?: ""

                return if (request?.isRedirect == true) {
                    view?.loadUrl(request?.url.toString())
                    true
                }
                else if (URLUtil.isNetworkUrl(link)) {
                    false
                } else {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
                    try {
                        chickSanityContext.startActivity(intent)
                    } catch (e: Exception) {
                        Toast.makeText(chickSanityContext, "This application not found", Toast.LENGTH_SHORT).show()
                    }
                    true
                }
            }


            override fun onPageFinished(view: WebView?, url: String?) {
                CookieManager.getInstance().flush()
                if (url?.contains("ninecasino") == true) {
                    ChickSanityApplication.chickSanityInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
                    Log.d(ChickSanityApplication.CHICK_SANITY_MAIN_TAG, "onPageFinished : ${ChickSanityApplication.chickSanityInputMode}")
                    chickSanityWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
                } else {
                    ChickSanityApplication.chickSanityInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE
                    Log.d(ChickSanityApplication.CHICK_SANITY_MAIN_TAG, "onPageFinished : ${ChickSanityApplication.chickSanityInputMode}")
                    chickSanityWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
                }
            }


        })

        super.setWebChromeClient(object : WebChromeClient() {
            override fun onPermissionRequest(request: PermissionRequest?) {
                request?.grant(request.resources)
            }

            override fun onShowFileChooser(
                webView: WebView?,
                filePathCallback: ValueCallback<Array<Uri>>?,
                fileChooserParams: WebChromeClient.FileChooserParams?,
            ): Boolean {
                chickSanityFileChooserHandler?.invoke(filePathCallback)
                return true
            }
            override fun onCreateWindow(
                view: WebView?,
                isDialog: Boolean,
                isUserGesture: Boolean,
                resultMsg: Message?
            ): Boolean {
                chickSanityHandleCreateWebWindowRequest(resultMsg)
                return true
            }
        })
    }


    fun chickSanityFLoad(link: String) {
        super.loadUrl(link)
    }

    private fun chickSanityHandleCreateWebWindowRequest(resultMsg: Message?) {
        if (resultMsg == null) return
        if (resultMsg.obj != null && resultMsg.obj is WebView.WebViewTransport) {
            val transport = resultMsg.obj as WebView.WebViewTransport
            val windowWebView = ChickSanityVi(chickSanityContext, chickSanityCallback, chickSanityWindow)
            transport.webView = windowWebView
            resultMsg.sendToTarget()
            chickSanityCallback.chickSanityHandleCreateWebWindowRequest(windowWebView)
        }
    }

}