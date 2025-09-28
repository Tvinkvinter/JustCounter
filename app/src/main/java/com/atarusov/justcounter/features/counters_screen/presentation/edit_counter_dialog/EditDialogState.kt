package com.atarusov.justcounter.features.counters_screen.presentation.edit_counter_dialog

import com.atarusov.justcounter.features.counters_screen.domain.Counter
import com.atarusov.justcounter.features.counters_screen.presentation.viewModel.CounterItem

data class EditDialogState(
    val itemState: CounterItem,
    private val initialCounterState: Counter
) {
    fun getInitialCounterState() = initialCounterState
}
