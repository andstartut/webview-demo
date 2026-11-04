package com.example.fieldteam.presentation.webview.event.model

internal sealed interface WebViewEvent {
    data class PageFinished(val url: String?) : WebViewEvent
    data object ExternalNavigationError : WebViewEvent
    data class RequestDatePicker(
        val requestId: String,
        val currentValue: String?,
        val minValue: String?,
        val maxValue: String?,
    ) : WebViewEvent

    data class RequestTimePicker(
        val requestId: String,
        val currentValue: String?,
        val minValue: String?,
        val maxValue: String?,
    ) : WebViewEvent
}
