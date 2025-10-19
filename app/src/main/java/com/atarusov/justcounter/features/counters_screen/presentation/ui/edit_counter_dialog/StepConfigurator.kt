package com.atarusov.justcounter.features.counters_screen.presentation.ui.edit_counter_dialog

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atarusov.justcounter.R
import com.atarusov.justcounter.common.getContrastContentColor
import com.atarusov.justcounter.ui.theme.CounterCardColors
import com.atarusov.justcounter.ui.theme.JustCounterTheme

data class StepConfiguratorState(
    val steps: List<Int>,
    val btnColor: Color,
    val removeBtnEnabled: Boolean = true,
    val addBtnEnabled: Boolean = true
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepConfigurator(
    state: StepConfiguratorState,
    modifier: Modifier = Modifier
) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = modifier.fillMaxWidth()
    ) {
        FilledIconButton(
            onClick = {},
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = state.btnColor,
                contentColor = state.btnColor.getContrastContentColor()
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_minus),
                contentDescription = stringResource(R.string.counter_screen_edit_minus_btn_description)
            )
        }

        Row(
            modifier = Modifier.align(Alignment.CenterVertically).weight(1f),
            horizontalArrangement = Arrangement.SpaceAround
        ) {
            state.steps.forEachIndexed { index, step ->
                BasicTextField(
                    value = TextFieldValue(step.toString()),
                    onValueChange = {},
                    modifier = Modifier.size(48.dp),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        textAlign = TextAlign.Center,
                    ),
                    singleLine = true,
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(
                                    width = if (index == 0) 2.dp else 1.dp,
                                    color = if (index == 0) state.btnColor else MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(4.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            innerTextField()
                        }
                    }
                )
            }
        }

        FilledIconButton(
            onClick = {},
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = state.btnColor,
                contentColor = state.btnColor.getContrastContentColor()
            )
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_plus),
                contentDescription = stringResource(R.string.counter_screen_edit_plus_btn_description) //todo,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun StepConfiguratorPreview() {
    val stepConfiguratorState = StepConfiguratorState(
        steps = listOf(1, 2, 3),
        btnColor = CounterCardColors.green,
    )

    JustCounterTheme {
        StepConfigurator(stepConfiguratorState, Modifier.width(300.dp))

    }
}