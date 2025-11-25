package com.atarusov.justcounter.features.counter_list_screen.mvi.entities

import com.atarusov.justcounter.domain.Counter

sealed class OneTimeEvent {
    data object ScrollDown: OneTimeEvent()
    data object ShowDragTip : OneTimeEvent()
    data class OpenEditCounterDialog(val counter: Counter) : OneTimeEvent()
}