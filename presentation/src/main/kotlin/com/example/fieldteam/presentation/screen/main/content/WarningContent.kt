package com.example.fieldteam.presentation.screen.main.content

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.fieldteam.presentation.R
import com.example.fieldteam.presentation.screen.main.components.WarningDialog
import com.example.fieldteam.presentation.screen.main.model.PermissionState
import com.example.fieldteam.presentation.theme.FieldTeamTheme

@Composable
internal fun WarningContent(
    permissionState: PermissionState,
    onConfirm: () -> Unit,
) {
    val title = when (permissionState) {
        PermissionState.Location -> stringResource(R.string.location_permission_title)
        PermissionState.Notifications -> stringResource(R.string.notification_permission_title)
    }
    
    val message = when (permissionState) {
        PermissionState.Location -> stringResource(R.string.location_permission_message)
        PermissionState.Notifications -> stringResource(R.string.notification_permission_message)
    }
    
    WarningDialog(
        title = title,
        message = message,
        onConfirm = onConfirm,
    )
}

@Preview
@Composable
private fun LocationWarningDialogPreview() {
    FieldTeamTheme {
        WarningDialog(
            title = stringResource(R.string.location_permission_title),
            message = stringResource(R.string.location_permission_message),
            onConfirm = {},
        )
    }
}

@Preview
@Composable
private fun NotificationsWarningDialogPreview() {
    FieldTeamTheme {
        WarningDialog(
            title = stringResource(R.string.notification_permission_title),
            message = stringResource(R.string.notification_permission_message),
            onConfirm = {},
        )
    }
}
