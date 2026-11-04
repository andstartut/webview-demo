package com.example.fieldteam.presentation.webview.assetloader

import android.webkit.WebResourceResponse

internal interface AssetLoader {
    fun loadAsset(path: String): WebResourceResponse?
}
