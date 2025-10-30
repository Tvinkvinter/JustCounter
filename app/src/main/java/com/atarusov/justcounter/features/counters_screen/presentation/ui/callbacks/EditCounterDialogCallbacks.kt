package com.atarusov.justcounter.features.counters_screen.presentation.ui.callbacks

import androidx.compose.ui.text.input.TextFieldValue
import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.CounterItem
import com.atarusov.justcounter.ui.theme.CounterColor

data class EditCounterDialogCallbacks(
    val onTitleInput: (inputField: TextFieldValue) -> Unit,
    val onTitleInputDone: (input: String) -> Unit,
    val onValueInput: (inputField: TextFieldValue) -> Unit,
    val onValueInputDone: (input: String) -> Unit,
    val onStepInput: (index: Int, inputField: TextFieldValue) -> Unit,
    val onStepInputDone: () -> Unit,
    val onRemoveStep: () -> Unit,
    val onAddStep: () -> Unit,
    val onColorSelected: (selectedColor: CounterColor) -> Unit,
    val onDismiss: () -> Unit,
    val onConfirm: (newCounterState: CounterItem) -> Unit
) {
    companion object {
        fun getEmptyCallbacks() = EditCounterDialogCallbacks(
            {}, {}, {}, {}, {_, _ -> }, {}, {}, {}, {}, {}, {}
        )
    }
}
