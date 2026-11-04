package com.example.fieldteam.presentation.screen.main

import com.example.fieldteam.presentation.screen.main.model.PermissionState
import com.example.fieldteam.presentation.webview.holder.WebViewHolder

internal sealed class State {
    data class CheckingPermissions(
        val permission: PermissionState = PermissionState.Location,
        val isPermissionRequestActive: Boolean = false,
    ) : State()
    data class Warning(
        val permission: PermissionState,
    ) : State()
    data class Ready(
        val webViewHolder: WebViewHolder,
        val isDownloading: Boolean = false,
    ) : State()
}
