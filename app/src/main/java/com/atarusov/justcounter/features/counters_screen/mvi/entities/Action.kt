package com.atarusov.justcounter.features.counters_screen.mvi.entities

sealed class Action {
    data object AddCounter : Action()
    data class RemoveCounter(val counterId: String) : Action()
    data class SwapCounters(val firstIndex: Int, val secondIndex: Int) : Action()
    data class MinusClick(val counterId: String, val oldValue: Int, val step: Int) : Action()
    data class PlusClick(val counterId: String, val oldValue: Int, val step: Int) : Action()
    object TitleTap : Action()

    data object SwitchRemoveMode : Action()
    data class OpenCounterEditDialog(val counterId: String) : Action()
}