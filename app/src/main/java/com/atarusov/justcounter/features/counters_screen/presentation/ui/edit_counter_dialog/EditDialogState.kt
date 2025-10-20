package com.atarusov.justcounter.features.counters_screen.presentation.ui.edit_counter_dialog

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.atarusov.justcounter.features.counters_screen.domain.Counter
import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.CounterItem

data class EditDialogState(
    val itemState: CounterItem,
    val stepConfiguratorState: StepConfiguratorState,
    private val initialCounterState: Counter
) {
    fun getInitialCounterState() = initialCounterState
}

data class StepConfiguratorState(
    val steps: List<TextFieldValue>,
    val btnColor: Color,
    val removeBtnEnabled: Boolean = true,
    val addBtnEnabled: Boolean = true
) {
    constructor(itemState: CounterItem): this(
        steps = itemState.steps.map { TextFieldValue(it.toString(), TextRange(it.toString().length)) },
        btnColor = itemState.color
    )
}


