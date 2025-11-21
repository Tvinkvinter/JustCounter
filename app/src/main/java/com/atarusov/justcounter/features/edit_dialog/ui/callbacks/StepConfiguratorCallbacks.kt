package com.atarusov.justcounter.features.edit_dialog.ui.callbacks

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
