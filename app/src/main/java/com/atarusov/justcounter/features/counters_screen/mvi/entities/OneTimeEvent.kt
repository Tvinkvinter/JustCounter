package com.atarusov.justcounter.features.counters_screen.mvi.entities

sealed class OneTimeEvent {
    data object ClearFocus : OneTimeEvent()
    data object ScrollDown: OneTimeEvent()
    data object ShowDragTip : OneTimeEvent()
    data class OpenEditCounterDialog(val counterId: String) : OneTimeEvent()
}