package com.atarusov.justcounter.features.counters_screen.presentation.ui.edit_counter_dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.atarusov.justcounter.R
import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.CounterItem
import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.OneTimeEvent
import com.atarusov.justcounter.features.counters_screen.presentation.ui.callbacks.EditCounterDialogCallbacks
import com.atarusov.justcounter.features.counters_screen.presentation.ui.callbacks.StepConfiguratorCallbacks
import com.atarusov.justcounter.ui.theme.CounterColorProvider
import com.atarusov.justcounter.ui.theme.Dimensions
import com.atarusov.justcounter.ui.theme.JustCounterTheme
import com.atarusov.justcounter.ui.theme.getReadability
import com.atarusov.justcounter.ui.theme.getReadableContentColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun EditCounterDialog(
    state: EditDialogState,
    events: Flow<OneTimeEvent>,
    callbacks: EditCounterDialogCallbacks
) {
    val itemColor = CounterColorProvider.getColor(state.itemState.color)

    Dialog(
        onDismissRequest = callbacks.onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        val focusManager = LocalFocusManager.current

        LaunchedEffect(Unit) {
            events.collect { event ->
                when (event) {
                    OneTimeEvent.ClearFocus -> focusManager.clearFocus(force = true)

                    OneTimeEvent.ScrollDown,
                    OneTimeEvent.ShowDragTip,
                    OneTimeEvent.ShowEmptyTitleTip -> null
                }
            }
        }

        Card(
            modifier = Modifier.width(IntrinsicSize.Max),
            elevation = CardDefaults.cardElevation(Dimensions.Elevation.dialog),
            border = BorderStroke(Dimensions.Border.bold, itemColor)
        ) {
            Column(
                modifier = Modifier,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = itemColor
                ) {
                    TitleTextFieldWithIcon(
                        titleField = state.itemState.titleField,
                        onTitleChange = callbacks.onTitleInput,
                        onInputDone = callbacks.onTitleInputDone,
                        itemColor = itemColor,
                        modifier = Modifier.padding(vertical = Dimensions.Spacing.extraSmall)
                    )
                }

                OutlinedTextField(
                    value = state.itemState.valueField,
                    onValueChange = callbacks.onValueInput,
                    modifier = Modifier
                        .defaultMinSize(24.dp)
                        .padding(top = Dimensions.Spacing.large),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = itemColor
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    keyboardActions = KeyboardActions(
                        onDone = { callbacks.onValueInputDone(state.itemState.valueField.text) }
                    ),
                )

                Text(
                    text = stringResource(R.string.edit_dialog_steps_text),
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(start = Dimensions.Spacing.huge, top = Dimensions.Spacing.small),
                    style = MaterialTheme.typography.bodySmall
                )

                StepConfigurator(
                    state = state.stepConfiguratorState,
                    callbacks = StepConfiguratorCallbacks(
                        callbacks.onStepInput,
                        callbacks.onStepInputDone,
                        callbacks.onRemoveStep,
                        callbacks.onAddStep
                    ),
                    modifier = Modifier
                        .padding(horizontal = Dimensions.Spacing.huge - 4.dp)
                        .padding(top = Dimensions.Spacing.extraSmall)
                )

                ColorPalette(
                    selectedColor = state.itemState.color,
                    onColorSelected = callbacks.onColorSelected,
                    modifier = Modifier
                        .padding(
                            horizontal = Dimensions.Spacing.huge - 2.dp,
                            vertical = Dimensions.Spacing.large
                        )
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Dimensions.Spacing.small),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    val backgroundColor = MaterialTheme.colorScheme.surfaceContainerHighest
                    val textButtonColor = if (itemColor.getReadability(backgroundColor) > 1.5) itemColor
                                          else MaterialTheme.colorScheme.onSurface

                    TextButton(
                        onClick = callbacks.onDismiss,
                        colors = ButtonDefaults.textButtonColors(contentColor = textButtonColor)
                    ) {
                        Text(
                            text = stringResource(R.string.edit_dialog_cancel_btn),
                        )
                    }

                    Button(
                        onClick = { callbacks.onConfirm(state.itemState) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = itemColor
                        ),
                        contentPadding = PaddingValues(horizontal = Dimensions.Spacing.medium)
                    ) {
                        Text(
                            text = stringResource(R.string.edit_dialog_save_btn),
                            color = itemColor.getReadableContentColor()
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TitleTextFieldWithIcon(
    titleField: TextFieldValue,
    onTitleChange: (inputField: TextFieldValue) -> Unit,
    onInputDone: (input: String) -> Unit,
    itemColor: Color,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    var localTitleFieldState by remember(titleField) { mutableStateOf(titleField) }

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.size(Dimensions.Size.small))

        BasicTextField(
            value = localTitleFieldState,
            onValueChange = onTitleChange,
            modifier = Modifier.focusRequester(focusRequester),
            textStyle = MaterialTheme.typography.titleLarge.copy(
                color = itemColor.getReadableContentColor(),
                textAlign = TextAlign.Center
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onInputDone(titleField.text) }
            ),
            singleLine = true,
            cursorBrush = SolidColor(itemColor.getReadableContentColor()),
            decorationBox = { innerTextField ->
                Box(
                    contentAlignment = Alignment.Center,
                ) {
                    if (localTitleFieldState.text.isBlank()) {
                        Text(
                            text = stringResource(R.string.edit_dialog_title_hint),
                            color = itemColor.getReadableContentColor().copy(alpha = 0.5f),
                            style = MaterialTheme.typography.titleLarge,
                        )
                    }

                    innerTextField()
                }
            }
        )

        Icon(
            painter = painterResource(R.drawable.ic_pencil),
            contentDescription = stringResource(R.string.edit_dialog_edit_title_btn_description),
            modifier = Modifier
                .size(Dimensions.Size.small)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false, radius = Dimensions.Radius.large),
                ) {
                    localTitleFieldState =
                        localTitleFieldState.copy(selection = TextRange(titleField.text.length))
                    focusRequester.requestFocus()
                },
            tint = itemColor.getReadableContentColor()
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EditCounterDialogPreview() {
    val counterItem = CounterItem.getPreviewCounterItem(withCustomSteps = true)
    val stepConfiguratorState = StepConfiguratorState(counterItem)
    val state =
        EditDialogState(itemState = counterItem, stepConfiguratorState = stepConfiguratorState)

    JustCounterTheme {
        EditCounterDialog(
            state = state,
            events = emptyFlow(),
            callbacks = EditCounterDialogCallbacks.getEmptyCallbacks()
        )
    }
}