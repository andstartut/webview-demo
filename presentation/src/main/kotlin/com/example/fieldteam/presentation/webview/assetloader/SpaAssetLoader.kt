package com.example.fieldteam.presentation.webview.assetloader

import android.content.Context
import android.webkit.WebResourceResponse
import org.koin.core.annotation.Single
import java.io.InputStream

@Single
internal class SpaAssetLoader(private val context: Context) : AssetLoader {

    companion object {
        private const val ASSETS_BASE_DIR = "spa/"
    }
    private val assetManager = context.assets

    override fun loadAsset(path: String): WebResourceResponse? {
        val assetPath = "$ASSETS_BASE_DIR$path"

        return runCatching {
            val inputStream: InputStream = assetManager.open(assetPath)
            val mimeType = getMimeType(path)
            WebResourceResponse(mimeType, "UTF-8", inputStream)
        }.getOrNull()
    }

    private fun getMimeType(path: String): String {
        return when {
            path.endsWith(".js") -> "application/javascript"
            path.endsWith(".css") -> "text/css"
            path.endsWith(".html") -> "text/html"
            path.endsWith(".json") -> "application/json"
            path.endsWith(".png") -> "image/png"
            path.endsWith(".jpg") || path.endsWith(".jpeg") -> "image/jpeg"
            path.endsWith(".svg") -> "image/svg+xml"
            path.endsWith(".ico") -> "image/x-icon"
            path.endsWith(".woff2") -> "font/woff2"
            path.endsWith(".woff") -> "font/woff"
            path.endsWith(".ttf") -> "font/ttf"
            path.endsWith(".otf") -> "font/otf"
            else -> "application/octet-stream"
        }
    }
}
