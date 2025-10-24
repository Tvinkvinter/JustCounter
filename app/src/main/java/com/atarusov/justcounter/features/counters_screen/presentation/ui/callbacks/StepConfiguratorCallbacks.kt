package com.atarusov.justcounter.features.counters_screen.presentation.ui.callbacks

import androidx.compose.ui.text.input.TextFieldValue

data class StepConfiguratorCallbacks(
    val onStepInput: (index: Int, input: TextFieldValue) -> Unit,
    val onStepInputDone: () -> Unit,
    val onRemoveStepClick: () -> Unit,
    val onAddStepClick: () -> Unit,
) {
    companion object {
        fun getEmptyCallbacks() = StepConfiguratorCallbacks({_, _ -> }, {}, {}, {})
    }
}
