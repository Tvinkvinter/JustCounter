package com.atarusov.justcounter.ui.theme

import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.ui.graphics.Color

val Purple80 = Color(0xFFD0BCFF)
val PurpleGrey80 = Color(0xFFCCC2DC)
val Pink80 = Color(0xFFEFB8C8)

val Purple40 = Color(0xFF6650a4)
val PurpleGrey40 = Color(0xFF625b71)
val Pink40 = Color(0xFF7D5260)

val RemoveRed = Color(0xDFFF3B30)

val TransparentTextSelectionColors = TextSelectionColors(
    handleColor = Color.Transparent,
    backgroundColor = Color.Transparent
)

object CounterCardColors {
    val blue = Color(0xFF3B82F6) // синий
    val cyan = Color(0xFF06B6D4) // бирюзовый
    val green = Color(0xFF10B981) // зелёный
    val yellow = Color(0xFFFACC15) // жёлтый
    val orange = Color(0xFFF97316) // оранжевый
    val red = Color(0xFFEF4444) // красный
    val pink = Color(0xFFEC4899) // розовый
    val violet = Color(0xFF8B5CF6) // фиолетовый
    val brown = Color(0xFF8B5E3C) // коричневый
    val gray = Color(0xFF9CA3AF) // серый

    fun getList() = listOf(blue, cyan, green, yellow, orange, red, pink, violet, brown, gray)
    fun getRandom() = getList().random()
}