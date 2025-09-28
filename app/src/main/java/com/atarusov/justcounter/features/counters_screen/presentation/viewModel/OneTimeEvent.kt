package com.atarusov.justcounter.features.counters_screen.presentation.viewModel

sealed class OneTimeEvent {
    object ClearFocus : OneTimeEvent()
}