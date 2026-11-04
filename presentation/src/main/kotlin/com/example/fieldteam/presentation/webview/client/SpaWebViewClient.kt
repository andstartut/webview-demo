package com.example.fieldteam.presentation.webview.client

import android.content.Intent
import android.net.Uri
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebResourceResponse
import android.webkit.WebView
import android.webkit.WebViewClient
import com.example.fieldteam.presentation.webview.SpaConfig
import com.example.fieldteam.presentation.webview.assetloader.AssetLoader
import com.example.fieldteam.presentation.webview.event.handler.WebViewEventHandler
import com.example.fieldteam.presentation.webview.event.model.WebViewEvent
import com.example.fieldteam.presentation.webview.logger.webViewDebugErrorLog
import com.example.fieldteam.presentation.webview.logger.webViewDebugLog
import com.example.fieldteam.presentation.webview.router.RequestRouter
import com.example.fieldteam.presentation.webview.router.model.RouteResult

internal class SpaWebViewClient(
    private val assetLoader: AssetLoader,
    private val requestRouter: RequestRouter,
    private val webViewEventHandler: WebViewEventHandler,
) : WebViewClient() {

    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest,
    ): Boolean {
        val url = request.url.toString()
        webViewDebugLog("shouldOverrideUrlLoading: $url")

        if (handleExternalScheme(view, request.url, url)) {
            return true
        }

        val result = requestRouter.route(url)

        if (result == RouteResult.PassThrough && isExternalUrl(url)) {
            webViewDebugErrorLog("External URL blocked: $url")
            webViewEventHandler.emit(WebViewEvent.ExternalNavigationError)
            return true
        }

        return result != RouteResult.PassThrough
    }

    private fun handleExternalScheme(
        view: WebView?,
        uri: Uri,
        url: String,
    ): Boolean {
        val action = when (uri.scheme?.lowercase()) {
            "tel" -> Intent.ACTION_DIAL
            "mailto" -> Intent.ACTION_SENDTO
            "sms", "smsto" -> Intent.ACTION_SENDTO
            else -> return false
        }
        val context = view?.context ?: return false
        return runCatching {
            val intent = Intent(action, uri)
            val chooser = Intent.createChooser(intent, null).apply {
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(chooser)
            webViewDebugLog("Launched chooser for external scheme: $url")
            true
        }.onFailure { error ->
            webViewDebugErrorLog("Failed to launch chooser for $url: ${error.message}")
        }.getOrDefault(true)
    }

    private fun isExternalUrl(url: String): Boolean {
        return !url.contains("example.com") &&
               !url.contains("/api/") &&
               !url.contains("/hubs/")
    }

    override fun shouldInterceptRequest(
        view: WebView?,
        request: WebResourceRequest,
    ): WebResourceResponse? {
        val url = request.url.toString()
        webViewDebugLog("Request: $url")

        val result = requestRouter.route(url)

        if (result is RouteResult.LoadFile) {
            return loadAssetOrFallback(
                path = result.path,
                view = view,
                request = request
            )
        }

        if (result == RouteResult.LoadIndex) {
            return loadAssetOrFallback(
                path = SpaConfig.INDEX_HTML,
                view = view,
                request = request,
                logMessage = "SPA route, serving index.html"
            )
        }

        return super.shouldInterceptRequest(view, request)
    }

    private fun loadAssetOrFallback(
        path: String,
        view: WebView?,
        request: WebResourceRequest,
        logMessage: String? = null,
    ): WebResourceResponse? {
        logMessage?.let { webViewDebugLog(it) }

        return assetLoader.loadAsset(path) ?: run {
            webViewDebugErrorLog("Asset not found: $path")
            super.shouldInterceptRequest(view, request)
        }
    }

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)
        webViewDebugLog("Page finished: $url")
        webViewEventHandler.emit(WebViewEvent.PageFinished(url))
    }

    override fun onReceivedError(
        view: WebView?,
        request: WebResourceRequest?,
        error: WebResourceError?,
    ) {
        super.onReceivedError(view, request, error)
        webViewDebugErrorLog("Error: ${error?.description}")
    }
}
