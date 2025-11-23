package com.atarusov.justcounter.features.counters_screen.ui.callbacks

data class CounterItemCallbacks(
    val onCounterTap: () -> Unit,
    val onPLusClick: (step: Int) -> Unit,
    val onMinusClick: (step: Int) -> Unit,
    val onEditClick: () -> Unit,
    val onRemoveClick: () -> Unit,
) {
    companion object {
        fun getEmptyCallbacks() = CounterItemCallbacks({}, {}, {}, {}, {})
    }
}
