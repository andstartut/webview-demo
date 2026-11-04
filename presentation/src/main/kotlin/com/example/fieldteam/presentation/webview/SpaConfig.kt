package com.example.fieldteam.presentation.webview

import com.example.fieldteam.presentation.BuildConfig

internal object SpaConfig {

    private const val ASSETS_DOMAIN_DEV = "https://dev.example.com/"
    private const val ASSETS_DOMAIN_PROD = "https://example.com/"

    val ASSETS_DOMAIN = if (BuildConfig.DEBUG) ASSETS_DOMAIN_DEV else ASSETS_DOMAIN_PROD
    const val INDEX_HTML = "index.html"
}
