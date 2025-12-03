package com.atarusov.justcounter.features.counter_full_screen.presentation.ui.callbacks

data class CounterFullScreenCallbacks(
    val onPLusClick: (step: Int) -> Unit,
    val onMinusClick: (step: Int) -> Unit,
    val onShrinkClick: () -> Unit,
    val onEditClick: () -> Unit,
    val onRemoveClick: () -> Unit,
) {
    companion object {
        fun getEmptyCallbacks() = CounterFullScreenCallbacks({}, {}, {}, {}, {})
    }
}
