package com.chickisa.sofskil.utils

import androidx.compose.ui.graphics.Color

// Color extensions
fun Color.lighten(factor: Float = 0.2f): Color {
    return copy(
        red = (red + (1 - red) * factor).coerceIn(0f, 1f),
        green = (green + (1 - green) * factor).coerceIn(0f, 1f),
        blue = (blue + (1 - blue) * factor).coerceIn(0f, 1f)
    )
}

fun Color.darken(factor: Float = 0.2f): Color {
    return copy(
        red = (red * (1 - factor)).coerceIn(0f, 1f),
        green = (green * (1 - factor)).coerceIn(0f, 1f),
        blue = (blue * (1 - factor)).coerceIn(0f, 1f)
    )
}

// String extensions
fun String.capitalizeWords(): String {
    return split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { it.uppercase() }
    }
}

// Number extensions
fun Int.toOrdinal(): String {
    return when {
        this % 100 in 11..13 -> "${this}th"
        this % 10 == 1 -> "${this}st"
        this % 10 == 2 -> "${this}nd"
        this % 10 == 3 -> "${this}rd"
        else -> "${this}th"
    }
}

fun Float.format(decimals: Int = 1): String {
    return "%.${decimals}f".format(this)
}

// List extensions
fun <T> List<T>.takeIfNotEmpty(): List<T>? {
    return if (isEmpty()) null else this
}

