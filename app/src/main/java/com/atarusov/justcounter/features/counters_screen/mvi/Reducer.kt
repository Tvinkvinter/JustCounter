package com.atarusov.justcounter.features.counters_screen.mvi

import androidx.compose.ui.text.input.TextFieldValue
import com.atarusov.justcounter.domain.Counter
import com.atarusov.justcounter.features.counters_screen.mvi.entities.InternalAction
import com.atarusov.justcounter.features.counters_screen.mvi.entities.State
import com.atarusov.justcounter.features.counters_screen.mvi.entities.toCounterItem
import javax.inject.Inject

class Reducer @Inject constructor() {
    fun reduce(previousState: State, internalAction: InternalAction): State =
        when (internalAction) {
            is InternalAction.LoadCounterItems -> loadCounterItems(previousState, internalAction.counters)
            is InternalAction.AddCounterItem -> addCounterItem(previousState, internalAction.counter)
            is InternalAction.RemoveCounterItem -> removeCounterItem(previousState, internalAction.counterId)
            is InternalAction.UpdateCounterItemValueField -> updateCounterItemValueField(
                previousState,
                internalAction.counterId,
                internalAction.newTextField
            )
            is InternalAction.SwapCounters -> swapCounters(previousState, internalAction.fromIndex, internalAction.toIndex)
            InternalAction.SwitchRemoveMode -> switchRemoveMode(previousState)

            InternalAction.ShowDragTip,
            InternalAction.ClearFocus,
            InternalAction.ScrollDown,
            is InternalAction.OpenEditCounterDialog -> previousState
        }

    private fun loadCounterItems(previousState: State, counters: List<Counter>) =
        previousState.copy(counterItems = counters.map { it.toCounterItem() })

    private fun addCounterItem(previousState: State, newCounter: Counter) =
        previousState.copy(
            counterItems = previousState.counterItems + listOf(newCounter.toCounterItem())
        )

    private fun removeCounterItem(previousState: State, counterId: String) =
        previousState.copy(
            counterItems = previousState.counterItems.toMutableList().apply {
                removeIf { it.counterId == counterId }
            }
        )

    private fun updateCounterItemValueField(
        previousState: State,
        counterId: String,
        newTextField: TextFieldValue
    ) = previousState.copy(
            counterItems = previousState.counterItems.map {
                if (it.counterId == counterId) it.copy(valueField = newTextField)
                else it
            }
        )

    private fun swapCounters(previousState: State, fromIndex: Int, toIndex: Int): State {
        val newCounterItems = previousState.counterItems.toMutableList()
        val temp = newCounterItems[fromIndex]
        newCounterItems[fromIndex] = newCounterItems[toIndex]
        newCounterItems[toIndex] = temp

        return previousState.copy(counterItems = newCounterItems)
    }

    private fun switchRemoveMode(previousState: State): State {
        val correctedCounterItems = previousState.counterItems.map {
            if (it.valueField.text.isBlank() || it.valueField.text == "-") {
                it.copy(valueField = it.valueField.copy(text = "0"))
            } else it
        }

        return previousState.copy(
            counterItems = correctedCounterItems,
            removeMode = !previousState.removeMode
        )
    }
}