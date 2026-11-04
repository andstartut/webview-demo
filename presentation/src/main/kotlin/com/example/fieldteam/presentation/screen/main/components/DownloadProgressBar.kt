package com.example.fieldteam.presentation.screen.main.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
internal fun DownloadProgressBar(isDownloading: Boolean) {
    AnimatedVisibility(
        visible = isDownloading,
        enter = expandVertically(),
        exit = shrinkVertically(),
    ) {
        LinearProgressIndicator(
            color = MaterialTheme.colorScheme.inversePrimary,
            modifier = Modifier.fillMaxWidth(),
        )
    }
}
