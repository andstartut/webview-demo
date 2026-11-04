package com.example.fieldteam.presentation.webview.event.handler

import com.example.fieldteam.presentation.webview.event.model.WebViewEvent
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import org.koin.core.annotation.Single

@Single
internal class SpaWebViewEventHandler : WebViewEventHandler {

    private val _events = MutableSharedFlow<WebViewEvent>(
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )

    override val events: SharedFlow<WebViewEvent> = _events.asSharedFlow()

    override fun emit(event: WebViewEvent) {
        _events.tryEmit(event)
    }
}
