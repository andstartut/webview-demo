package com.example.fieldteam.presentation.theme.color

import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color

@Suppress("MagicNumber")
fun colorScheme(palette: ColorPalette) = lightColorScheme(
    primary = palette.blue.color500,
    onPrimary = palette.base.white,
    primaryContainer = palette.blue.color100,
    onPrimaryContainer = palette.blue.color700,

    secondary = palette.blue.color400,
    onSecondary = palette.base.white,
    secondaryContainer = palette.blue.color200,
    onSecondaryContainer = palette.blue.color700,

    tertiary = palette.grey.color600,
    onTertiary = palette.base.white,
    tertiaryContainer = palette.grey.color200,
    onTertiaryContainer = palette.text.color900,

    background = palette.primaryBackground,
    onBackground = palette.text.color900,
    surface = palette.surfaceBackground,
    onSurface = palette.text.color900,

    surfaceVariant = palette.grey.color200,
    onSurfaceVariant = palette.grey.color700,
    surfaceTint = palette.blue.color500,

    inverseSurface = palette.grey.color900,
    inverseOnSurface = palette.base.white,
    inversePrimary = palette.blue.color300,

    outline = palette.inputFieldBorder,
    outlineVariant = palette.grey.color200,

    scrim = palette.grey.color900,

    error = palette.red.color800,
    onError = palette.base.white,
    errorContainer = Color(0xFFFFDAD6),
    onErrorContainer = Color(0xFF410002)
)
