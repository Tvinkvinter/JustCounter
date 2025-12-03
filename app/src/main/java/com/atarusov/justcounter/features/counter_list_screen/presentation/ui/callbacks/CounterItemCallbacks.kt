package com.atarusov.justcounter.features.counter_list_screen.presentation.ui.callbacks

data class CounterItemCallbacks(
    val onCounterTap: () -> Unit,
    val onPLusClick: (step: Int) -> Unit,
    val onMinusClick: (step: Int) -> Unit,
    val onExpandClick: () -> Unit,
    val onEditClick: () -> Unit,
    val onRemoveClick: () -> Unit,
) {
    companion object {
        fun getEmptyCallbacks() = CounterItemCallbacks({}, {}, {}, {}, {}, {})
    }
}
