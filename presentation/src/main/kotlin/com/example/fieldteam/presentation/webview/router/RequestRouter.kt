package com.example.fieldteam.presentation.webview.router

import com.example.fieldteam.presentation.webview.router.model.RouteResult

internal interface RequestRouter {
    fun route(url: String): RouteResult
    fun extractPath(url: String): String
}
