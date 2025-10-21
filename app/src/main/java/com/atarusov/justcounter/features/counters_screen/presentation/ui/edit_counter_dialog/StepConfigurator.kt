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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.atarusov.justcounter.R
import com.atarusov.justcounter.common.getContrastContentColor
import com.atarusov.justcounter.ui.theme.CounterCardColors
import com.atarusov.justcounter.ui.theme.JustCounterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepConfigurator(
    state: StepConfiguratorState,
    onStepInput: (index: Int, input: TextFieldValue) -> Unit,
    onStepInputDone: () -> Unit,
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
                contentColor = state.btnColor.getContrastContentColor(),
                disabledContainerColor = state.btnColor
            ),
            enabled = state.removeBtnEnabled
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
                    value = step,
                    onValueChange = { onStepInput(index, it) },
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
                    },
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { onStepInputDone() }
                    )
                )
            }
        }

        FilledIconButton(
            onClick = {},
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = state.btnColor,
                contentColor = state.btnColor.getContrastContentColor(),
                disabledContainerColor = state.btnColor
            ),
            enabled = state.addBtnEnabled
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_plus),
                contentDescription = stringResource(R.string.counter_screen_edit_plus_btn_description),
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun StepConfiguratorPreview() {
    val stepConfiguratorState = StepConfiguratorState(
        steps = listOf(
            TextFieldValue("1"),
            TextFieldValue("2"),
            TextFieldValue("3"),
        ),
        btnColor = CounterCardColors.green,
    )

    JustCounterTheme {
        StepConfigurator(
            stepConfiguratorState,
            { index, input -> }, {},
            Modifier.width(300.dp)
        )
    }
}