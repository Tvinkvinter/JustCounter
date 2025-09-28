package com.atarusov.justcounter.features.counters_screen.presentation.edit_counter_dialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.text.selection.LocalTextSelectionColors
import androidx.compose.foundation.text.selection.TextSelectionColors
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.atarusov.justcounter.features.counters_screen.domain.Counter
import com.atarusov.justcounter.features.counters_screen.presentation.viewModel.CounterItem
import com.atarusov.justcounter.ui.ColorPalette
import com.atarusov.justcounter.ui.theme.CounterCardColors
import com.atarusov.justcounter.ui.theme.JustCounterTheme

@Composable
fun EditCounterDialog(
    state: EditDialogState,
    onInputValue: (newValue: String) -> Unit,
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
                    Text(
                        text = state.itemState.titleField,
                        modifier = Modifier.padding(vertical = 8.dp),
                        color =
                            if (state.itemState.color.luminance() < 0.5f) Color.White
                            else Color.Black,
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.titleLarge
                    )
                }


                val customTextSelectionColors = TextSelectionColors(
                    handleColor = Color.Transparent,
                    backgroundColor = Color.Transparent
                )

                CompositionLocalProvider(LocalTextSelectionColors provides customTextSelectionColors) {

                    OutlinedTextField(
                        value = state.itemState.valueField,
                        onValueChange = { onInputValue(it) },
                        modifier = Modifier
                            .defaultMinSize(24.dp)
                            .padding(top = 24.dp),
                        textStyle = LocalTextStyle.current.copy(textAlign = TextAlign.Center),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = state.itemState.color,
                        ),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number
                        )
                    )
                }

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
                        Text("Cancel")
                    }

                    Button(
                        onClick = { onConfirm(state.itemState) },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = state.itemState.color
                        ),
                        contentPadding = PaddingValues()
                    ) {
                        Text("Save")
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun EditCounterDialogPreview() {
    val counter = Counter(
        title = "Tvin",
        color = CounterCardColors.red,
        value = 120
    )
    val counterItem = CounterItem(counter)

    JustCounterTheme {
        EditCounterDialog(
            EditDialogState(
                counterItem,
                initialCounterState = Counter("Test", 128, CounterCardColors.red)
            ), {}, {}, {}, {}
        )
    }
}