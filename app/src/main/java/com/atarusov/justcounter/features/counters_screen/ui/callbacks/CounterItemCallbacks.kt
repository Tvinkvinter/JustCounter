package com.atarusov.justcounter.features.counters_screen.ui.callbacks

import androidx.compose.ui.text.input.TextFieldValue

data class CounterItemCallbacks(
    val onPLusClick: (step: Int) -> Unit,
    val onMinusClick: (step: Int) -> Unit,
    val onTitleTap: () -> Unit,
    val onEditClick: () -> Unit,
    val onInputValue: (inputField: TextFieldValue) -> Unit,
    val onInputValueDone: (input: String) -> Unit,
    val onRemoveClick: () -> Unit,
) {
    companion object {
        fun getEmptyCallbacks() = CounterItemCallbacks({}, {}, {}, {}, {}, {}, {})
    }
}
