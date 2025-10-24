package com.atarusov.justcounter.features.counters_screen.presentation.ui.edit_counter_dialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atarusov.justcounter.ui.theme.CounterCardColors
import com.atarusov.justcounter.ui.theme.Dimensions
import com.atarusov.justcounter.ui.theme.JustCounterTheme

@Composable
fun ColorPalette(
    selectedColor: Color,
    onColorSelected: (selectedColor: Color) -> Unit,
    modifier: Modifier = Modifier
) {
    FlowRow(
        modifier = modifier.fillMaxWidth().defaultMinSize(120.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalArrangement = Arrangement.spacedBy(Dimensions.Spacing.xxSmall),
        itemVerticalAlignment = Alignment.CenterVertically,
        maxItemsInEachRow = CounterCardColors.getList().size / 2
    ) {
        val allColors = CounterCardColors.getList()

        allColors.forEachIndexed { index, color ->
            val thisColorSelected = color == selectedColor
            Surface(
                modifier = Modifier.size(Dimensions.Size.large),
                shape = CircleShape,
                color = if (thisColorSelected) Color.White else Color.Transparent,
            ) {
                Surface(
                    modifier = Modifier.padding(Dimensions.Spacing.xxSmall),
                    shape = CircleShape,
                    color = color,
                    onClick = { onColorSelected(color) }
                ) {}
            }
        }
    }
}

@Preview
@Composable
private fun ColorPalettePreview() {
    JustCounterTheme {
        ColorPalette(
            selectedColor = CounterCardColors.green,
            onColorSelected = {},
            modifier = Modifier.width(300.dp)
        )
    }
}