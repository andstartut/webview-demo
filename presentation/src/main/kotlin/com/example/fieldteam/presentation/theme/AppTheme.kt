package com.example.fieldteam.presentation.theme

import androidx.compose.runtime.Composable
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.staticCompositionLocalOf
import com.example.fieldteam.presentation.theme.color.ColorPalette

object AppTheme {
    val colorPalette: ColorPalette
        @Composable
        @ReadOnlyComposable
        get() = LocalAppColorPalette.current
}

val LocalAppColorPalette = staticCompositionLocalOf { ColorPalette() }
