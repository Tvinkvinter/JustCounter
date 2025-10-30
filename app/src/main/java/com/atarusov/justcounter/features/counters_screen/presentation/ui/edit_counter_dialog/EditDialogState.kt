package com.atarusov.justcounter.features.counters_screen.presentation.ui.edit_counter_dialog

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.CounterItem
import com.atarusov.justcounter.ui.theme.CounterColor

data class EditDialogState(
    val itemState: CounterItem,
    val stepConfiguratorState: StepConfiguratorState,
) {
    private val initialCounterItemState = itemState
    fun getInitialCounterItemState() = initialCounterItemState
}

data class StepConfiguratorState(
    val steps: List<TextFieldValue>,
    val btnColor: CounterColor
) {
    val removeBtnEnabled: Boolean get() = steps.size > 1
    val addBtnEnabled: Boolean get() = steps.size < 3

    constructor(itemState: CounterItem): this(
        steps = itemState.steps.map { TextFieldValue(it.toString(), TextRange(it.toString().length)) },
        btnColor = itemState.color
    )
}


