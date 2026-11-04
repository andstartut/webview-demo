package com.example.fieldteam.presentation.webview.client

import android.util.Log
import android.webkit.ConsoleMessage
import android.webkit.WebChromeClient
import com.example.fieldteam.presentation.BuildConfig

class SpaWebChromeClient : WebChromeClient() {

    companion object {
        private const val TAG = "WebView.Console"
    }

    override fun onConsoleMessage(message: ConsoleMessage): Boolean {
        if (BuildConfig.DEBUG) {
            val logMessage = buildString {
                append("[${message.sourceId()}:${message.lineNumber()}] ")
                append(message.message())
            }

            when (message.messageLevel()) {
                ConsoleMessage.MessageLevel.ERROR -> Log.e(TAG, logMessage)
                ConsoleMessage.MessageLevel.WARNING -> Log.w(TAG, logMessage)
                ConsoleMessage.MessageLevel.DEBUG -> Log.d(TAG, logMessage)
                else -> Log.i(TAG, logMessage)
            }
        }
        return true
    }
}
