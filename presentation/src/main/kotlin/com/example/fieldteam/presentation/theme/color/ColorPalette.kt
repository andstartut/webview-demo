package com.example.fieldteam.presentation.theme.color

import androidx.compose.runtime.Immutable
import androidx.compose.ui.graphics.Color

@Suppress("MagicNumber")
@Immutable
data class ColorPalette(
    val blue: Blue = Blue(),
    val grey: Grey = Grey(),
    val red: Red = Red(),
    val yellow: Yellow = Yellow(),
    val green: Green = Green(),
    val text: Text = Text(),
    val base: Base = Base()
) {
    val accent = blue.color500
    val active = blue.color700
    val disable = grey.color300

    val primaryBackground = Color(0xFFFDFCFE)
    val surfaceBackground = base.white

    val dark = text.color900
    val additional = grey.color600

    val unitBackground = Color(0xFFF7F7F7)
    val comments = grey.color500
    val inputFieldBackground = base.white
    val inputFieldBorder = grey.color300

    data class Base(
        val white: Color = Color(0xFFFFFFFF)
    )

    val buttonDisable = grey.color400

    data class Text(
        val color900: Color = Color(0xFF0C192D),
        val color800: Color = Color(0xFF344054),
        val color600: Color = Color(0xFF6C757D)
    )

    data class Blue(
        val color700: Color = Color(0xFF0C192D),
        val color600: Color = Color(0xFF2D4A87),
        val color500: Color = Color(0xFF3F51A0),
        val color400: Color = Color(0xFF5A6BB8),
        val color300: Color = Color(0xFF7B8ACF),
        val color200: Color = Color(0xFFE7EBFF),
        val color100: Color = Color(0xFFDAD5FC),
    )

    data class Grey(
        val color900: Color = Color(0xFF0C192D),
        val color800: Color = Color(0xFF344054),
        val color700: Color = Color(0xFF475467),
        val color600: Color = Color(0xFF667085),
        val color500: Color = Color(0xFF98A2B3),
        val color400: Color = Color(0xFFD0D5DD),
        val color300: Color = Color(0xFFD5D6DA),
        val color200: Color = Color(0xFFE3E5EA),
        val color100: Color = Color(0xFFF7F7F7),
    )

    data class Red(
        val color800: Color = Color(0xFFD92D20),
        val color600: Color = Color(0xFFF04438),
    )

    data class Yellow(
        val color600: Color = Color(0xFFF79009),
        val color400: Color = Color(0xFFFDB022)
    )

    data class Green(
        val color600: Color = Color(0xFF12B76A)
    )
}

fun Color.transparency(alpha: Float): Color {
    require(alpha in 0f..1f) { "Alpha must be in range 0..1" }
    return this.copy(alpha = alpha)
}
