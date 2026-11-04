package com.example.fieldteam.presentation.webview.router

import com.example.fieldteam.presentation.webview.SpaConfig.ASSETS_DOMAIN
import com.example.fieldteam.presentation.webview.logger.webViewDebugErrorLog
import com.example.fieldteam.presentation.webview.logger.webViewDebugLog
import com.example.fieldteam.presentation.webview.router.model.RouteResult
import org.koin.core.annotation.Single

@Single
internal class SpaRequestRouter(
    private val domain: String = ASSETS_DOMAIN,
) : RequestRouter {

    override fun route(url: String): RouteResult {
        webViewDebugLog("Routing URL: $url, domain: $domain")

        if (!url.startsWith(domain)) {
            webViewDebugErrorLog("URL doesn't start with domain: $domain")
            return RouteResult.PassThrough
        }

        val path = extractPath(url)
        webViewDebugLog("Extracted path: $path")

        val firstSegment = path.split("/").firstOrNull { it.isNotEmpty() }?.lowercase()
        val isApiRequest = firstSegment == "api" || firstSegment == "hubs"
        webViewDebugLog("First segment: $firstSegment, isApiRequest: $isApiRequest")

        if (isApiRequest) {
            webViewDebugLog("API request, passing through")
            return RouteResult.PassThrough
        }

        if (isFileRequest(path)) {
            webViewDebugLog("File request detected: $path")
            return RouteResult.LoadFile(path)
        }

        webViewDebugLog("SPA route detected, loading index.html")
        return RouteResult.LoadIndex
    }

    override fun extractPath(url: String): String {
        return url.removePrefix(domain)
    }

    private fun isFileRequest(path: String): Boolean {
        val lastSegment = path.substringAfterLast('/')
        return lastSegment.contains('.')
    }
}
