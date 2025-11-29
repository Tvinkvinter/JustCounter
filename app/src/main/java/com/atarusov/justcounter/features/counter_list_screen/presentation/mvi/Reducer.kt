package com.atarusov.justcounter.features.counter_list_screen.presentation.mvi

import com.atarusov.justcounter.common.Counter
import com.atarusov.justcounter.features.counter_list_screen.presentation.mvi.entities.InternalAction
import com.atarusov.justcounter.features.counter_list_screen.presentation.mvi.entities.State
import javax.inject.Inject

class Reducer @Inject constructor() {
    fun reduce(previousState: State, internalAction: InternalAction): State =
        when (internalAction) {
            is InternalAction.LoadCounters -> previousState.copy(counters = internalAction.counters)
            is InternalAction.AddCounter -> addCounter(previousState, internalAction.counter)
            is InternalAction.RemoveCounter -> removeCounter(previousState, internalAction.counterId)
            is InternalAction.UpdateCounterValue -> updateCounterValueText(
                previousState,
                internalAction.counterId,
                internalAction.newValue
            )
            is InternalAction.SwapCounters -> swapCounters(previousState, internalAction.fromIndex, internalAction.toIndex)
            InternalAction.SwitchRemoveMode -> previousState.copy(removeMode = !previousState.removeMode)

            InternalAction.ShowDragTip,
            InternalAction.ScrollDown,
            is InternalAction.OpenEditCounterDialog -> previousState
        }

    private fun addCounter(previousState: State, newCounter: Counter) =
        previousState.copy(counters = previousState.counters + listOf(newCounter))

    private fun removeCounter(previousState: State, counterId: String) =
        previousState.copy(
            counters = previousState.counters.toMutableList().apply {
                removeIf { it.id == counterId }
            }
        )

    private fun updateCounterValueText(
        previousState: State,
        counterId: String,
        newValue: Int
    ) = previousState.copy(
            counters = previousState.counters.map {
                if (it.id == counterId) it.copy(value = newValue)
                else it
            }
        )

    private fun swapCounters(previousState: State, firstIndex: Int, secondIndex: Int): State {
        val newCounterList = previousState.counters.toMutableList()

        val firstCounter = newCounterList[firstIndex]
        val secondCounter = newCounterList[secondIndex]

        newCounterList[secondIndex] = firstCounter
        newCounterList[firstIndex] = secondCounter

        return previousState.copy(counters = newCounterList)
    }
}