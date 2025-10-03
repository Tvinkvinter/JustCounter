package com.atarusov.justcounter.features.counters_screen.presentation.ui.edit_counter_dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import com.atarusov.justcounter.common.getContrastContentColor
import com.atarusov.justcounter.features.counters_screen.domain.Counter
import com.atarusov.justcounter.features.counters_screen.presentation.ui.ColorPalette
import com.atarusov.justcounter.features.counters_screen.presentation.viewModel.CounterItem
import com.atarusov.justcounter.features.counters_screen.presentation.viewModel.OneTimeEvent
import com.atarusov.justcounter.ui.theme.CounterCardColors
import com.atarusov.justcounter.ui.theme.JustCounterTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.emptyFlow

@Composable
fun EditCounterDialog(
    state: EditDialogState,
    events: Flow<OneTimeEvent>,
    onTitleInput: (newTitle: String) -> Unit,
    onTitleInputDone: () -> Unit,
    onValueInput: (newValue: String) -> Unit,
    onColorSelected: (selectedColor: Color) -> Unit,
    onDismiss: () -> Unit,
    onConfirm: (newCounterState: CounterItem) -> Unit
) {
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(
            usePlatformDefaultWidth = false
        )
    ) {
        val focusManager = LocalFocusManager.current

        LaunchedEffect(Unit) {
            events.collect { event ->
                when (event) {
                    OneTimeEvent.ClearFocus -> focusManager.clearFocus(force = true)
                    is OneTimeEvent.ShowTitleInputError -> null
                }
            }
        }

        Card(
            modifier = Modifier.fillMaxWidth(0.9f),
            elevation = CardDefaults.cardElevation(8.dp),
            border = BorderStroke(3.dp, state.itemState.color)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = state.itemState.color
                ) {
                    TitleTextFieldWithIcon(
                        value = state.itemState.titleField,
                        onValueChange = onTitleInput,
                        onInputDone = onTitleInputDone,
                        itemColor = state.itemState.color,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }

                OutlinedTextField(
                    value = state.itemState.valueField,
                    onValueChange = onValueInput,
                    modifier = Modifier
                        .defaultMinSize(24.dp)
                        .padding(top = 24.dp),
                    textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = state.itemState.color
                    ),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    )
                )

                ColorPalette(
                    selectedColor = state.itemState.color,
                    onColorSelected = onColorSelected,
                    modifier = Modifier.padding(top = 24.dp)
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    TextButton(
                        onClick = onDismiss,
                        colors = ButtonDefaults.textButtonColors(
                            contentColor = state.itemState.color
                        )
                    ) {
                        Text(stringResource(R.string.counter_edit_dialog_btn_cancel))
                    }

                    Button(
                        onClick = { onConfirm(state.itemState) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = state.itemState.color
                        ),
                        contentPadding = PaddingValues()
                    ) {
                        Text(stringResource(R.string.counter_edit_dialog_btn_save))
                    }
                }
            }
        }
    }
}

@Composable
fun TitleTextFieldWithIcon(
    value: String,
    onValueChange: (newValue: String) -> Unit,
    onInputDone: () -> Unit,
    itemColor: Color,
    modifier: Modifier = Modifier
) {
    val focusRequester = remember { FocusRequester() }
    var textFieldValue by remember(value) { mutableStateOf(TextFieldValue(value)) }

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center
    ) {
        Spacer(Modifier.size(24.dp))

        BasicTextField(
            value = textFieldValue,
            onValueChange = { newValue ->
                textFieldValue = newValue
                onValueChange(newValue.text)
            },
            modifier = Modifier.focusRequester(focusRequester),
            textStyle = MaterialTheme.typography.titleLarge.copy(
                color = itemColor.getContrastContentColor(),
                textAlign = TextAlign.Center
            ),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done
            ),
            keyboardActions = KeyboardActions(
                onDone = { onInputDone() }
            ),
            singleLine = true,
            cursorBrush = SolidColor(itemColor.getContrastContentColor())
        )

        Icon(
            painter = painterResource(R.drawable.ic_pencil),
            contentDescription = stringResource(R.string.counter_screen_edit_btn_description),
            modifier = Modifier
                .size(24.dp)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = false, radius = 16.dp),
                ) {
                    textFieldValue = textFieldValue.copy(
                        text = value,
                        selection = TextRange(value.length)
                    )
                    focusRequester.requestFocus()
                },
            tint = itemColor.getContrastContentColor()
        )
    }
}

@Preview
@Composable
fun EditCounterDialogPreview() {
    val counter = Counter(
        title = "Tvinkvinter",
        color = CounterCardColors.red,
        value = 120
    )
    val counterItem = CounterItem(counter)

    JustCounterTheme {
        EditCounterDialog(
            EditDialogState(
                counterItem,
                initialCounterState = Counter("Test", 128, CounterCardColors.red)
            ), emptyFlow(), {}, {}, {}, {}, {}, {}
        )
    }
}