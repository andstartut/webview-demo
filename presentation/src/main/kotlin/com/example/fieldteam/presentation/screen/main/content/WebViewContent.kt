package com.example.fieldteam.presentation.screen.main.content

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView
import com.example.fieldteam.presentation.webview.holder.WebViewHolder

@Composable
internal fun WebViewContent(
    webViewHolder: WebViewHolder,
    modifier: Modifier = Modifier,
) {
    AndroidView(
        factory = { webViewHolder.createWebView() },
        modifier = modifier.fillMaxSize(),
        onRelease = {
            webViewHolder.clear()
        }
    )
}
