package com.atarusov.justcounter.features.counters_screen.ui.callbacks

data class CounterItemCallbacks(
    val onPLusClick: (step: Int) -> Unit,
    val onMinusClick: (step: Int) -> Unit,
    val onTitleTap: () -> Unit,
    val onEditClick: () -> Unit,
    val onRemoveClick: () -> Unit,
) {
    companion object {
        fun getEmptyCallbacks() = CounterItemCallbacks({}, {}, {}, {}, {})
    }
}
