package com.atarusov.justcounter.features.counters_screen.presentation.mvi

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import com.atarusov.justcounter.features.counters_screen.domain.Counter
import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.CounterItem
import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.InternalAction
import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.State
import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.toCounterItem
import com.atarusov.justcounter.features.counters_screen.presentation.ui.edit_counter_dialog.EditDialogState
import com.atarusov.justcounter.features.counters_screen.presentation.ui.edit_counter_dialog.StepConfiguratorState
import javax.inject.Inject

class Reducer @Inject constructor() {
    fun reduce(previousState: State, internalAction: InternalAction): State =
        when (internalAction) {
            is InternalAction.LoadCounterItems -> loadCounterItems(previousState, internalAction.counters)
            is InternalAction.AddCounterItem -> addCounterItem(previousState, internalAction.counter)
            is InternalAction.RemoveCounterItem -> removeCounterItem(previousState, internalAction.counterId)
            is InternalAction.UpdateCounterItemColor -> updateCounterItemColor(
                previousState,
                internalAction.counterId,
                internalAction.newColor
            )
            is InternalAction.UpdateCounterItemTitleField -> updateCounterItemTitleField(
                previousState,
                internalAction.counterId,
                internalAction.newTextField
            )
            is InternalAction.UpdateCounterItemValueField -> updateCounterItemValueField(
                previousState,
                internalAction.counterId,
                internalAction.newTextField
            )
            is InternalAction.ChangeCounterItemValueBy -> changeCounterItemValueBy(
                previousState,
                internalAction.counterId,
                internalAction.by
            )
            is InternalAction.UpdateCounterItemSteps -> updateCounterItemSteps(
                previousState,
                internalAction.counterId,
                internalAction.steps
            )
            is InternalAction.UpdateStepConfiguratorField -> updateStepConfiguratorField(
                previousState,
                internalAction.stepIndex,
                internalAction.newTextField
            )
            is InternalAction.RestoreCounterItem -> restoreCounterItem(
                previousState,
                internalAction.counterItem
            )
            InternalAction.RemoveLastStepField -> removeLastStepField(previousState)
            InternalAction.AddStepField -> addStepField(previousState)

            is InternalAction.OpenEditCounterDialog -> openEditCounterDialog(previousState, internalAction.counterId)
            InternalAction.CloseEditCounterDialog -> previousState.copy(editDialog = null)
            InternalAction.SwitchRemoveMode -> switchRemoveMode(previousState)

            InternalAction.ClearFocus,
            is InternalAction.ShowTitleError -> previousState

        }

    private fun loadCounterItems(previousState: State, counters: List<Counter>) =
        previousState.copy(counterItems = counters.map { it.toCounterItem() })

    private fun addCounterItem(previousState: State, newCounter: Counter) = previousState.copy(
        counterItems = previousState.counterItems + listOf(newCounter.toCounterItem())
    )

    private fun removeCounterItem(previousState: State, counterId: String) = previousState.copy(
        counterItems = previousState.counterItems.toMutableList().apply {
            removeIf { it.counterId == counterId }
        }
    )

    private fun updateCounterItemColor(
        previousState: State,
        counterId: String,
        newColor: Color
    ) = previousState.copy(
        counterItems = previousState.counterItems.map {
            if (it.counterId == counterId) it.copy(color = newColor)
            else it
        },
        editDialog = previousState.editDialog?.copy(
            itemState = previousState.editDialog.itemState.copy(color = newColor),
            stepConfiguratorState = previousState.editDialog.stepConfiguratorState.copy(btnColor = newColor)
        )
    )

    private fun updateCounterItemTitleField(
        previousState: State,
        counterId: String,
        newFieldValue: TextFieldValue
    ) = previousState.copy(
        counterItems = previousState.counterItems.map {
            if (it.counterId == counterId) it.copy(titleField = newFieldValue)
            else it
        },
        editDialog = previousState.editDialog?.copy(
            itemState = previousState.editDialog.itemState.copy(titleField = newFieldValue)
        )
    )

    private fun updateCounterItemValueField(
        previousState: State,
        counterId: String,
        newFieldValue: TextFieldValue
    ) = previousState.copy(
        counterItems = previousState.counterItems.map {
            if (it.counterId == counterId) it.copy(valueField = newFieldValue)
            else it
        },
        editDialog = previousState.editDialog?.copy(
            itemState = previousState.editDialog.itemState.copy(valueField = newFieldValue)
        )
    )

    private fun changeCounterItemValueBy(
        previousState: State,
        counterId: String,
        by: Int
    ): State {
        val counterItem = previousState.counterItems.getCounterItemById(counterId)
        val newValueField = counterItem.valueField.copy(
            text = ((counterItem.valueField.text.toIntOrNull() ?: 0) + by).toString()
        )
        return updateCounterItemValueField(previousState, counterId, newValueField)
    }

    private fun updateCounterItemSteps(
        previousState: State,
        counterId: String,
        newSteps: List<Int>
    ) = previousState.copy(
        counterItems = previousState.counterItems.map {
            if (it.counterId == counterId) it.copy(steps = newSteps)
            else it
        }
    )

    private fun updateStepConfiguratorField(
        previousState: State,
        stepIndex: Int,
        newFieldValue: TextFieldValue
    ): State {
        val newSteps = previousState.editDialog?.stepConfiguratorState?.steps?.mapIndexed { index, fieldValue ->
            if (index == stepIndex) newFieldValue
            else fieldValue
        }

        val newStepConfiguratorState = previousState.editDialog?.stepConfiguratorState?.copy(
            steps = newSteps!!
        )

        return previousState.copy(
            editDialog = previousState.editDialog?.copy(
                stepConfiguratorState = newStepConfiguratorState!!
            )
        )
    }

    private fun restoreCounterItem(previousState: State, counterItemToRestore: CounterItem) =
        previousState.copy(
            counterItems = previousState.counterItems.map {
                if (it.counterId == counterItemToRestore.counterId) counterItemToRestore
                else it
            }
        )

    private fun removeLastStepField(previousState: State): State {
        checkNotNull(previousState.editDialog) { "Cannot remove step field when editDialog is not open" }

        if (previousState.editDialog.stepConfiguratorState.steps.size <= 1) return previousState

        val newSteps = previousState.editDialog.stepConfiguratorState.steps.toMutableList().apply {
            removeAt(lastIndex)
        }
        val newStepConfiguratorState = previousState.editDialog.stepConfiguratorState.copy(
            steps = newSteps
        )

        return previousState.copy(
            editDialog = previousState.editDialog.copy(stepConfiguratorState = newStepConfiguratorState)
        )
    }

    private fun addStepField(previousState: State): State {
        checkNotNull(previousState.editDialog) { "Cannot add step field when editDialog is not open" }

        val newSteps = previousState.editDialog.stepConfiguratorState.steps.toMutableList().apply {
            add(TextFieldValue())
        }
        val newStepConfiguratorState = previousState.editDialog.stepConfiguratorState.copy(
            steps = newSteps
        )

        return previousState.copy(
            editDialog = previousState.editDialog.copy(stepConfiguratorState = newStepConfiguratorState)
        )
    }

    private fun openEditCounterDialog(previousState: State, counterId: String): State {
        val openDialogForCounterItem = previousState.counterItems.getCounterItemById(counterId)
        val editDialogState = EditDialogState(
            itemState = openDialogForCounterItem,
            stepConfiguratorState = StepConfiguratorState(openDialogForCounterItem),
        )

        return previousState.copy(editDialog = editDialogState)
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

    private fun List<CounterItem>.getCounterItemById(counterId: String) = find {
        it.counterId == counterId
    } ?: throw NoSuchElementException("Counter item with id = $counterId wasn't found")
}