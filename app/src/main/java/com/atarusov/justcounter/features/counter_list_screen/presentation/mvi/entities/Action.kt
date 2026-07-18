package com.atarusov.justcounter.features.counter_list_screen.presentation.mvi.entities

import com.atarusov.justcounter.common.Counter

sealed class Action {
    data class AddCounter(val categoryId: Int?) : Action()
    data class RemoveCounter(val counter: Counter) : Action()
    data class SwapCounters(
        val categoryId: Int?,
        val firstIndex: Int,
        val secondIndex: Int
    ) : Action()
    data class MinusClick(
        val counterId: String,
        val oldValue: Int,
        val step: Int,
        val hasCustomSteps: Boolean,
    ) : Action()
    data class PlusClick(
        val counterId: String,
        val oldValue: Int,
        val step: Int,
        val hasCustomSteps: Boolean,
    ) : Action()
    object TitleTap : Action()

    data class SwitchRemoveMode(val enabled: Boolean) : Action()
    data class ChangeCategory(val categoryId: Int?) : Action()
    data class ExpandCounter(val counter: Counter): Action()
    data class OpenCounterEditDialog(val counter: Counter) : Action()
}
