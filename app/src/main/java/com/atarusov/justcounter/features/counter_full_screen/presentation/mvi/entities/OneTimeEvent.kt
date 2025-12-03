package com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities

import com.atarusov.justcounter.common.Counter

sealed class OneTimeEvent {
    data object NavigateBack : OneTimeEvent()
    data class OpenEditCounterDialog(val counter: Counter) : OneTimeEvent()
}