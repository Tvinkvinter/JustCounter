package com.atarusov.justcounter.features.counters_screen.presentation.mvi

import com.atarusov.justcounter.features.counters_screen.domain.Counter
import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.CounterItem
import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.InternalAction
import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.State
import com.atarusov.justcounter.features.counters_screen.presentation.ui.edit_counter_dialog.EditDialogState
import javax.inject.Inject

class Reducer @Inject constructor() {
    fun reduce(previousState: State, internalAction: InternalAction): State =
        when (internalAction) {
            is InternalAction.LoadCounterItems -> loadCounterItems(previousState, internalAction.counters)
            is InternalAction.AddCounterItem -> addCounterItem(previousState, internalAction.counter)
            is InternalAction.RemoveCounterItem -> removeCounterItem(previousState, internalAction.counterId)
            is InternalAction.UpdateCounterItem -> updateCounterItem(previousState, internalAction.counter)
            is InternalAction.UpdateCounterItemValueField -> updateCounterItemValueField(
                previousState,
                internalAction.counterId,
                internalAction.newFieldValue
            )

            is InternalAction.OpenEditCounterDialog -> openEditCounterDialog(previousState, internalAction.counter)
            InternalAction.CloseEditCounterDialog -> previousState.copy(editDialog = null)
            InternalAction.SwitchRemoveMode -> switchRemoveMode(previousState)

            InternalAction.ClearFocus,
            is InternalAction.ShowTitleError -> previousState

        }

    private fun loadCounterItems(previousState: State, counters: List<Counter>) =
        previousState.copy(counterItems = counters.map { CounterItem(it) })

    private fun addCounterItem(previousState: State, newCounter: Counter) = previousState.copy(
        counterItems = previousState.counterItems + listOf(CounterItem(newCounter))
    )

    private fun removeCounterItem(previousState: State, counterId: String) = previousState.copy(
        counterItems = previousState.counterItems.toMutableList().apply {
            removeIf { it.counterId == counterId }
        }
    )

    private fun updateCounterItem(previousState: State, newCounter: Counter) = previousState.copy(
        counterItems = previousState.counterItems.map {
            if (it.counterId == newCounter.id) CounterItem(newCounter)
            else it
        },
        editDialog = previousState.editDialog?.copy(itemState = CounterItem(newCounter))
    )

    private fun updateCounterItemValueField(
        previousState: State,
        counterId: String,
        newFieldValue: String
    ) = previousState.copy(
        counterItems = previousState.counterItems.map {
            if (it.counterId == counterId) it.copy(valueField = newFieldValue)
            else it
        },
        editDialog = previousState.editDialog?.copy(
            itemState = previousState.editDialog.itemState.copy(valueField = newFieldValue)
        )
    )

    private fun openEditCounterDialog(previousState: State, counter: Counter): State {
        val openDialogForCounterItem = previousState.counterItems.find {
            it.counterId == counter.id
        } ?: throw NoSuchElementException("Counter item with id = ${counter.id} wasn't found")

        return previousState.copy(editDialog = EditDialogState(openDialogForCounterItem, counter))
    }

    private fun switchRemoveMode(previousState: State) = previousState.copy(
        removeMode = !previousState.removeMode
    )
}