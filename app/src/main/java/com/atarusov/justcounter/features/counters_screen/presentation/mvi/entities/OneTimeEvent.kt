package com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities

sealed class OneTimeEvent {
    object ClearFocus : OneTimeEvent()
    object ShowDragTip : OneTimeEvent()
    data class ShowTitleInputError(val counterId: String) : OneTimeEvent()
}