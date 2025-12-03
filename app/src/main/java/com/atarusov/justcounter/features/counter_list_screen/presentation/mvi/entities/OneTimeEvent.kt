package com.atarusov.justcounter.features.counter_list_screen.presentation.mvi.entities

import com.atarusov.justcounter.common.Counter

sealed class OneTimeEvent {
    data object ScrollDown: OneTimeEvent()
    data object ShowDragTip : OneTimeEvent()
    data class NavigateToCounterFullScreen(val counter: Counter) : OneTimeEvent()
    data class OpenEditCounterDialog(val counter: Counter) : OneTimeEvent()
}