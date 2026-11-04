package com.example.fieldteam.presentation.screen.main.content

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.fieldteam.presentation.screen.main.components.DownloadProgressBar
import com.example.fieldteam.presentation.webview.holder.WebViewHolder

@Composable
internal fun ReadyContent(
    webViewHolder: WebViewHolder,
    isDownloading: Boolean,
) {
    Column(modifier = Modifier.fillMaxSize()) {
        DownloadProgressBar(isDownloading = isDownloading)
        WebViewContent(
            webViewHolder = webViewHolder,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
        )
    }
}
