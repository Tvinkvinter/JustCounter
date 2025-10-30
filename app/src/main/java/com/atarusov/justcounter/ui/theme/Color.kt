package com.atarusov.justcounter.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val black = Color(0xFF2A2A2D)
val white = Color(0xFFFFFFFF)
val lightGrey = Color(0xFFECECEC)
val darkGrey = Color(0xFF3B3B3F)
val extraDarkGrey = Color(0xFF414144)

val lightOrange = Color(0xFFFFC75F)
val dangerRed = Color(0xDFFF3B30)

enum class CounterColor {
    Blue, Cyan, Green, Yellow, Orange, Red, Pink, Purple, Brown, Gray
}

object CounterColorProvider {

    private val light = mapOf(
        CounterColor.Blue to Color(0xFF60A5FA), // blue
        CounterColor.Cyan to Color(0xFF31E2FD), // cyan
        CounterColor.Green to Color(0xFF34D399), // green
        CounterColor.Yellow to Color(0xFFFEEA5D), // yellow
        CounterColor.Orange to Color(0xFFFB923C), // orange
        CounterColor.Red to Color(0xFFF65454), // red
        CounterColor.Pink to Color(0xFFF472B6), // pink
        CounterColor.Purple to Color(0xFF9D74FA), // purple
        CounterColor.Brown to Color(0xFF9C704F), // brown
        CounterColor.Gray to Color(0xFFB0B7C3)  // gray
    )

    private val dark = mapOf(
        CounterColor.Blue to Color(0xFF85B8FF), // blue
        CounterColor.Cyan to Color(0xFF6DE9FF), // cyan
        CounterColor.Green to Color(0xFF5EDFAC), // green
        CounterColor.Yellow to Color(0xFFFFF27A), // yellow
        CounterColor.Orange to Color(0xFFFFAB6B), // orange
        CounterColor.Red to Color(0xFFFF7A7A), // red
        CounterColor.Pink to Color(0xFFFF84C1), // pink
        CounterColor.Purple to Color(0xFFB397FF), // purple
        CounterColor.Brown to Color(0xFFB49071), // brown
        CounterColor.Gray to Color(0xFFC0C5CC)  // gray
    )

    fun getAllColors() = CounterColor.entries
    fun getRandomColor() = CounterColor.entries.random()

    @Composable
    fun getColor(counterColor: CounterColor) =
        if (isSystemInDarkTheme()) dark[counterColor]
        else {
            light[counterColor]
        } ?: throw IllegalStateException("Color with name ${counterColor.name} not found")

}

val TransparentTextSelectionColors = TextSelectionColors(
    handleColor = Color.Transparent,
    backgroundColor = Color.Transparent
)