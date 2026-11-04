package com.example.fieldteam.presentation.webview.logger

import android.util.Log
import com.example.fieldteam.presentation.BuildConfig

private const val TAG = "SpaWebViewClient"

internal fun webViewDebugErrorLog(message: String) {
    if (BuildConfig.DEBUG) {
        Log.e(TAG, message)
    }
}

internal fun webViewDebugLog(message: String) {
    if (BuildConfig.DEBUG) {
        Log.d(TAG, message)
    }
}
