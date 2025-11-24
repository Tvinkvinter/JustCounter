package com.atarusov.justcounter.features.edit_dialog.ui.edit_counter_dialog

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.times
import com.atarusov.justcounter.R
import com.atarusov.justcounter.ui.theme.getReadableContentColor
import com.atarusov.justcounter.features.edit_dialog.mvi.entities.StepConfiguratorState
import com.atarusov.justcounter.features.edit_dialog.ui.callbacks.StepConfiguratorCallbacks
import com.atarusov.justcounter.ui.theme.CounterColor
import com.atarusov.justcounter.ui.theme.CounterColorProvider
import com.atarusov.justcounter.ui.theme.Dimensions
import com.atarusov.justcounter.ui.theme.JustCounterTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StepConfigurator(
    state: StepConfiguratorState,
    callbacks: StepConfiguratorCallbacks,
    modifier: Modifier = Modifier
) {
    val itemColor = CounterColorProvider.getColor(state.btnColor)

    Row(
        horizontalArrangement = Arrangement.spacedBy(Dimensions.Spacing.extraSmall),
        modifier = modifier.widthIn(min= 2 * Dimensions.Size.large +  3 * Dimensions.Size.large + 5 * Dimensions.Spacing.extraSmall)
    ) {
        FilledIconButton(
            onClick = callbacks.onRemoveStepClick,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = itemColor,
                contentColor = itemColor.getReadableContentColor(),
                disabledContainerColor = itemColor
            ),
            enabled = state.removeBtnEnabled
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_minus),
                contentDescription = stringResource(R.string.edit_dialog_minus_btn_description)
            )
        }

        Row(
            modifier = Modifier.align(Alignment.CenterVertically).weight(1f),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            state.steps.forEachIndexed { index, step ->
                BasicTextField(
                    value = step,
                    onValueChange = { callbacks.onStepInput(index, it) },
                    modifier = Modifier.size(Dimensions.Size.large),
                    textStyle = MaterialTheme.typography.bodyMedium.copy(
                        textAlign = TextAlign.Center,
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { callbacks.onStepInputDone() }
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.onSurface),
                    decorationBox = { innerTextField ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .border(
                                    width = if (index == 0) Dimensions.Border.bold else Dimensions.Border.thin,
                                    color = if (index == 0) itemColor else MaterialTheme.colorScheme.outline,
                                    shape = RoundedCornerShape(Dimensions.Radius.small)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            innerTextField()
                        }
                    }
                )

                if (index != state.steps.lastIndex) Spacer(Modifier.width(Dimensions.Spacing.extraSmall))
            }
        }

        FilledIconButton(
            onClick = callbacks.onAddStepClick,
            colors = IconButtonDefaults.iconButtonColors(
                containerColor = itemColor,
                contentColor = itemColor.getReadableContentColor(),
                disabledContainerColor = itemColor
            ),
            enabled = state.addBtnEnabled
        ) {
            Icon(
                painter = painterResource(R.drawable.ic_plus),
                contentDescription = stringResource(R.string.edit_dialog_plus_btn_description),
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun StepConfiguratorPreview() {
    JustCounterTheme {
        StepConfigurator(
            state = StepConfiguratorState(
                steps = listOf(TextFieldValue("1"), TextFieldValue("2")),
                btnColor = CounterColor.Blue
            ),
            callbacks = StepConfiguratorCallbacks.getEmptyCallbacks(),
            modifier = Modifier.width(300.dp)
        )
    }
}