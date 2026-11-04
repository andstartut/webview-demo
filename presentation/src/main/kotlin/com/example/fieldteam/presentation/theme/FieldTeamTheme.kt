package com.example.fieldteam.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import com.example.fieldteam.presentation.theme.color.ColorPalette
import com.example.fieldteam.presentation.theme.color.colorScheme

@Composable
fun FieldTeamTheme(
    colorPalette: ColorPalette = ColorPalette(),
    content: @Composable () -> Unit,
) {
    CompositionLocalProvider(
        LocalAppColorPalette provides colorPalette
    ) {
        MaterialTheme(
            colorScheme = colorScheme(colorPalette),
            content = content
        )
    }
}
