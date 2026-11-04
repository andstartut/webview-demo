package com.example.fieldteam.presentation.webview.navigation

import android.webkit.JavascriptInterface

interface WebAppInterface {
    @JavascriptInterface
    fun onNavigateTo(
        stopsJson: String,
    )

    @JavascriptInterface
    fun onRequestDatePicker(
        requestId: String,
        currentValue: String?,
        minValue: String?,
        maxValue: String?,
    )

    @JavascriptInterface
    fun onRequestTimePicker(
        requestId: String,
        currentValue: String?,
        minValue: String?,
        maxValue: String?,
    )

    @JavascriptInterface
    fun onDownloadFile(
        base64Content: String,
        fileName: String,
        mimeType: String?,
    )

    @JavascriptInterface
    fun onDownloadStart()

    @JavascriptInterface
    fun onDownloadBegin(
        sessionId: String,
        fileName: String,
        mimeType: String?,
    ): Boolean

    @JavascriptInterface
    fun onDownloadChunk(
        sessionId: String,
        base64Chunk: String,
    ): Boolean

    @JavascriptInterface
    fun onDownloadFinish(sessionId: String)

    @JavascriptInterface
    fun onDownloadAbort(sessionId: String)
}
