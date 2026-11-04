package com.example.fieldteam.presentation.webview.holder

import android.webkit.WebView
import com.example.fieldteam.domain.model.GeoPoint
import com.example.fieldteam.presentation.webview.factory.WebViewFactory
import com.example.fieldteam.presentation.webview.logger.webViewDebugLog
import com.example.fieldteam.presentation.webview.navigation.ExternalApplicationInterface
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import org.koin.core.annotation.Single

@Single
internal class WebViewHolder(
    private val webViewFactory: WebViewFactory,
    private val externalApplicationInterface: ExternalApplicationInterface,
) {

    companion object {
        internal const val JS_INTERFACE_NAME = "ExternalApplication"
    }

    private var webView: WebView? = null
    private val coroutineScope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    fun createWebView(): WebView {
        val view = webViewFactory.create()
        webView = view
        return view
    }

    fun clear() {
        coroutineScope.cancel()
        webView = null
    }

    fun installSpaCommandBridge() {
        evaluateJavascript(WebViewJsCommands.ensureCommandBridge())
    }

    fun evaluateJavascript(script: String, callback: ((String?) -> Unit)? = null) {
        coroutineScope.launch {
            webView?.evaluateJavascript(script, callback)
        }
    }

    fun updateDeviceToken(token: String) {
        webViewDebugLog("updateDeviceToken token: $token")
        evaluateJavascript(WebViewJsCommands.updateDeviceToken(token))
    }

    fun updateUserLocation(geoPoint: GeoPoint) {
        webViewDebugLog("updateUserLocation lat: ${geoPoint.lat}, lng: ${geoPoint.lng}")
        evaluateJavascript(WebViewJsCommands.updateUserLocation(geoPoint.toJson()))
    }

    fun injectDateTimeInputBridge() {
        evaluateJavascript(DateTimeInputBridgeScript.SCRIPT)
    }

    fun injectDownloadBridge() {
        evaluateJavascript(DownloadBridgeScript.SCRIPT)
    }

    fun applyDatePickerValue(requestId: String, value: String) {
        evaluateJavascript(WebViewJsCommands.applyDatePickerValue(requestId, value))
    }

    fun cancelDatePicker(requestId: String) {
        evaluateJavascript(WebViewJsCommands.cancelDatePicker(requestId))
    }

    fun applyTimePickerValue(requestId: String, value: String) {
        evaluateJavascript(WebViewJsCommands.applyTimePickerValue(requestId, value))
    }

    fun cancelTimePicker(requestId: String) {
        evaluateJavascript(WebViewJsCommands.cancelTimePicker(requestId))
    }

    fun onResume() {
        coroutineScope.launch {
            val webView = webView ?: return@launch
            webView.addJavascriptInterface(externalApplicationInterface, JS_INTERFACE_NAME)
            webView.evaluateJavascript(
                "(typeof window['$JS_INTERFACE_NAME'] !== 'undefined').toString()"
            ) { result ->
                val isAvailable = result?.trim('"') == "true"
                webViewDebugLog("onResume: $JS_INTERFACE_NAME available in JS: $isAvailable")
                if (!isAvailable) {
                    webViewDebugLog("onResume: bridge not visible, reloading page")
                    webView.reload()
                }
            }
        }
    }

    fun goBack(): Boolean {
        val view = webView ?: return false

        return if (view.canGoBack()) {
            view.goBack()
            true
        } else {
            false
        }
    }
}
