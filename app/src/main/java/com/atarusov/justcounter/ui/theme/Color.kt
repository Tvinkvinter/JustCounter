package com.atarusov.justcounter.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance

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
        CounterColor.Blue to Color(0xFF60A5FA),
        CounterColor.Cyan to Color(0xFF33CDD3),
        CounterColor.Green to Color(0xFF34D399),
        CounterColor.Yellow to Color(0xFFFEEA5D),
        CounterColor.Orange to Color(0xFFFB923C),
        CounterColor.Red to Color(0xFFF65454),
        CounterColor.Pink to Color(0xFFF472B6),
        CounterColor.Purple to Color(0xFF9D74FA),
        CounterColor.Brown to Color(0xFF9C704F),
        CounterColor.Gray to Color(0xFFB0B7C3)
    )

    private val dark = mapOf(
        CounterColor.Blue to Color(0xFF85B8FF),
        CounterColor.Cyan to Color(0xFF6DE9FF),
        CounterColor.Green to Color(0xFF5EDFAC),
        CounterColor.Yellow to Color(0xFFFFF27A),
        CounterColor.Orange to Color(0xFFFFAB6B),
        CounterColor.Red to Color(0xFFFF7A7A),
        CounterColor.Pink to Color(0xFFFF84C1),
        CounterColor.Purple to Color(0xFFB397FF),
        CounterColor.Brown to Color(0xFFB99678),
        CounterColor.Gray to Color(0xFFC0C5CC)
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

@Composable
fun Color.getReadableContentColor(): Color {
    val baseColor = MaterialTheme.colorScheme.inverseOnSurface
    if (baseColor.getReadability(this) >= 1.5) return baseColor

    return if (isSystemInDarkTheme()) white else extraDarkGrey
}

fun Color.getReadability(backgroundColor: Color): Double {
    val l1 = this.luminance()
    val l2 = backgroundColor.luminance()
    val (bright, dark) = if (l1 > l2) l1 to l2 else l2 to l1
    return (bright + 0.05) / (dark + 0.05)
}
