package com.atarusov.justcounter.features.edit_dialog.ui.edit_counter_dialog

import android.widget.Toast
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
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.atarusov.justcounter.R
import com.atarusov.justcounter.features.edit_dialog.mvi.entities.Action
import com.atarusov.justcounter.features.edit_dialog.mvi.entities.OneTimeEvent
import com.atarusov.justcounter.features.edit_dialog.mvi.entities.State
import com.atarusov.justcounter.features.edit_dialog.ui.callbacks.StepConfiguratorCallbacks
import com.atarusov.justcounter.features.edit_dialog.viewModel.EditCounterDialogViewModel
import com.atarusov.justcounter.ui.theme.CounterColorProvider
import com.atarusov.justcounter.ui.theme.Dimensions
import com.atarusov.justcounter.ui.theme.JustCounterTheme
import com.atarusov.justcounter.ui.theme.getReadability
import com.atarusov.justcounter.ui.theme.getReadableContentColor

@Composable
fun EditCounterDialog(
    onEditDialogClose: () -> Unit,
    viewModel: EditCounterDialogViewModel = hiltViewModel(),
) {
    val state by viewModel.screenState.collectAsStateWithLifecycle()

    Dialog(
        onDismissRequest = { viewModel.onAction(Action.CloseCounterEditDialog(state, false)) },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val context = LocalContext.current
        val focusManager = LocalFocusManager.current

        LaunchedEffect(Unit) {
            viewModel.screenEvents.collect { event ->
                when (event) {
                    OneTimeEvent.ClearFocus -> focusManager.clearFocus(force = true)

                    is OneTimeEvent.ShowEmptyTitleTip -> {
                        val errorMessage =
                            context.getString(R.string.counter_screen_empty_title_tip)
                        Toast.makeText(context, errorMessage, Toast.LENGTH_LONG).show()
                    }

                    OneTimeEvent.CloseEditDialog -> onEditDialogClose()
                }
            }
        }

        EditCounterDialogContent(
            state = state,
            onAction = viewModel::onAction
        )
    }
}

@Composable
private fun EditCounterDialogContent(
    state: State,
    onAction: (Action) -> Unit,
) {
    val counterColor = CounterColorProvider.getColor(state.color)

    Card(
        modifier = Modifier.width(IntrinsicSize.Max),
        elevation = CardDefaults.cardElevation(Dimensions.Elevation.dialog),
        border = BorderStroke(Dimensions.Border.bold, counterColor)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = counterColor
            ) {
                TitleTextFieldWithIcon(
                    titleField = state.titleField,
                    onTitleChange = { onAction(Action.TitleInput(it)) },
                    onInputDone = { onAction(Action.TitleInputDone(it)) },
                    itemColor = counterColor,
                    modifier = Modifier.padding(vertical = Dimensions.Spacing.extraSmall)
                )
            }

            OutlinedTextField(
                value = state.valueField,
                onValueChange = { onAction(Action.ValueInput(it)) },
                modifier = Modifier
                    .defaultMinSize(24.dp)
                    .padding(top = Dimensions.Spacing.large),
                textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = counterColor
                ),
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                keyboardActions = KeyboardActions(
                    onDone = { onAction(Action.ValueInputDone(state.valueField.text)) }
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
                    onStepInput = { index, textField ->
                        onAction(Action.StepInput(index, textField))
                    },
                    onStepInputDone = { onAction(Action.StepInputDone) },
                    onRemoveStepClick = { onAction(Action.RemoveStep) },
                    onAddStepClick = { onAction(Action.AddStep) }
                ),
                modifier = Modifier
                    .padding(horizontal = Dimensions.Spacing.huge - 4.dp)
                    .padding(top = Dimensions.Spacing.extraSmall)
            )

            ColorPalette(
                selectedColor = state.color,
                onColorSelected = { onAction(Action.ChangeColor(it)) },
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
                val textButtonColor =
                    if (counterColor.getReadability(backgroundColor) > 1.5) counterColor
                    else MaterialTheme.colorScheme.onSurface

                TextButton(
                    onClick = { onAction(Action.CloseCounterEditDialog(state, false)) },
                    colors = ButtonDefaults.textButtonColors(contentColor = textButtonColor)
                ) {
                    Text(
                        text = stringResource(R.string.edit_dialog_cancel_btn),
                    )
                }

                Button(
                    onClick = { onAction(Action.CloseCounterEditDialog(state, true)) },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = counterColor
                    ),
                    contentPadding = PaddingValues(horizontal = Dimensions.Spacing.medium)
                ) {
                    Text(
                        text = stringResource(R.string.edit_dialog_save_btn),
                        color = counterColor.getReadableContentColor()
                    )
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
    val density = LocalDensity.current
    val focusRequester = remember { FocusRequester() }
    var localTitleFieldState by remember(titleField) { mutableStateOf(titleField) }

    val titleFieldHint = stringResource(R.string.edit_dialog_title_hint)
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = textMeasurer.measure(
        text = localTitleFieldState.text.ifEmpty { titleFieldHint },
        style = MaterialTheme.typography.titleLarge,
        maxLines = 1
    )

    Row(
        modifier = modifier.height(Dimensions.Size.medium),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.size(Dimensions.Size.small))

        BasicTextField(
            value = localTitleFieldState,
            onValueChange = onTitleChange,
            modifier = Modifier
                .focusRequester(focusRequester)
                .fillMaxHeight()
                .padding(horizontal = Dimensions.Spacing.small)
                .width(
                    with(density) {
                        min(
                            textLayoutResult.size.width.toDp(),
                            Dimensions.Size.maxEditCounterDialogTitleWidth
                        )
                    }),
            textStyle = MaterialTheme.typography.titleLarge.copy(
                color = itemColor.getReadableContentColor(),
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
                Box(contentAlignment = Alignment.Center) {
                    if (localTitleFieldState.text.isEmpty()) {
                        Text(
                            text = titleFieldHint,
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

@Composable
@Preview(showBackground = true)
private fun EditCounterDialogPreview() {
    JustCounterTheme {
        Dialog(
            onDismissRequest = {},
            properties = DialogProperties(usePlatformDefaultWidth = false)
        ) {
            EditCounterDialogContent(
                state = State.getPreviewState(),
                onAction = {}
            )
        }
    }
}