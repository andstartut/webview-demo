package com.example.fieldteam.presentation.webview.router.model

internal sealed class RouteResult {
    data object PassThrough : RouteResult()
    data class LoadFile(val path: String) : RouteResult()
    data object LoadIndex : RouteResult()
}
