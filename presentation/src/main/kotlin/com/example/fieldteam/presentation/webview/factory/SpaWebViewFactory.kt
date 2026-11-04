package com.example.fieldteam.presentation.webview.factory

import android.content.Context
import android.webkit.ServiceWorkerClient
import android.webkit.ServiceWorkerController
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.URLUtil
import android.webkit.WebView
import com.example.fieldteam.presentation.webview.SpaConfig.ASSETS_DOMAIN
import com.example.fieldteam.presentation.webview.assetloader.AssetLoader
import com.example.fieldteam.presentation.webview.client.SpaWebChromeClient
import com.example.fieldteam.presentation.webview.client.SpaWebViewClient
import com.example.fieldteam.presentation.webview.event.handler.WebViewEventHandler
import com.example.fieldteam.presentation.webview.holder.WebViewJsCommands
import com.example.fieldteam.presentation.webview.logger.webViewDebugLog
import com.example.fieldteam.presentation.webview.navigation.ExternalApplicationInterface
import com.example.fieldteam.presentation.webview.holder.WebViewHolder
import com.example.fieldteam.presentation.webview.router.RequestRouter
import com.example.fieldteam.presentation.webview.router.model.RouteResult
import org.koin.core.annotation.Factory

@Factory
internal class SpaWebViewFactory(
    private val context: Context,
    private val assetLoader: AssetLoader,
    private val requestRouter: RequestRouter,
    private val externalApplicationInterface: ExternalApplicationInterface,
    private val webViewEventHandler: WebViewEventHandler,
) : WebViewFactory {

    init {
        setupServiceWorkerInterception()
    }

    private fun setupServiceWorkerInterception() {
        ServiceWorkerController.getInstance().setServiceWorkerClient(
            object : ServiceWorkerClient() {
                override fun shouldInterceptRequest(request: WebResourceRequest): WebResourceResponse? {
                    val url = request.url.toString()
                    return when (val result = requestRouter.route(url)) {
                        is RouteResult.LoadFile -> assetLoader.loadAsset(result.path)
                        RouteResult.LoadIndex -> assetLoader.loadAsset("index.html")
                        RouteResult.PassThrough -> null
                    }
                }
            }
        )
    }

    override fun create(): WebView {
        return WebView(context).apply {
            webViewClient = SpaWebViewClient(
                assetLoader = assetLoader,
                requestRouter = requestRouter,
                webViewEventHandler = webViewEventHandler,
            )

            webChromeClient = SpaWebChromeClient()

            settings.apply {
                javaScriptEnabled = true
                domStorageEnabled = true
                allowFileAccess = true
                allowContentAccess = true
                useWideViewPort = true
                loadWithOverviewMode = true
            }

            addJavascriptInterface(externalApplicationInterface, WebViewHolder.JS_INTERFACE_NAME)

            setDownloadListener { url, _, contentDisposition, mimetype, _ ->
                handleDownload(
                    webView = this,
                    url = url,
                    contentDisposition = contentDisposition,
                    mimeType = mimetype,
                )
            }

            loadUrl(ASSETS_DOMAIN)
        }
    }

    private fun handleDownload(
        webView: WebView,
        url: String,
        contentDisposition: String?,
        mimeType: String?,
    ) {
        webViewDebugLog(
            "DownloadListener triggered: url=$url, mimeType=$mimeType, disposition=$contentDisposition"
        )

        val fileName = URLUtil.guessFileName(url, contentDisposition, mimeType).orEmpty()
        val script = WebViewJsCommands.downloadUrl(url = url, fileName = fileName)
        webView.evaluateJavascript(script, null)
    }
}
