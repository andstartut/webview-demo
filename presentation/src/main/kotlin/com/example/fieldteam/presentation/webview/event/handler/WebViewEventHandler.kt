package com.example.fieldteam.presentation.webview.event.handler

import com.example.fieldteam.presentation.webview.event.model.WebViewEvent
import kotlinx.coroutines.flow.SharedFlow

internal interface WebViewEventHandler {

    val events: SharedFlow<WebViewEvent>

    fun emit(event: WebViewEvent)
}
