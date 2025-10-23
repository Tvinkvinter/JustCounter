package com.atarusov.justcounter.features.counters_screen.presentation.mvi

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import com.atarusov.justcounter.features.counters_screen.domain.Counter
import com.atarusov.justcounter.features.counters_screen.domain.CounterListRepository
import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.Action
import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.InternalAction
import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.toCounter
import com.atarusov.justcounter.features.counters_screen.presentation.ui.edit_counter_dialog.EditDialogState
import com.atarusov.justcounter.ui.theme.CounterCardColors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class Actor @Inject constructor(
    val repository: CounterListRepository
) {
    companion object {
        private const val MIN_VALUE = -999_999_999
        private const val MAX_VALUE = 999_999_999
    }

    fun handleAction(action: Action): Flow<InternalAction> {
        return when (action) {
            Action.AddCounter -> createNewCounter()
            is Action.RemoveCounter -> removeCounter(action.counterId)

            is Action.ChangeColor -> changeCounterColor(action.counterId, action.newColor)
            is Action.MinusClick -> changeValueBy(action.counterId, -action.step, action.valueField)
            is Action.PlusClick -> changeValueBy(action.counterId, action.step, action.valueField)
            Action.RemoveStep -> flowOf(InternalAction.RemoveLastStepField)
            Action.AddStep -> flowOf(InternalAction.AddStepField)

            is Action.TitleInput -> updateCounterTitle(action.counterId, action.inputTextField)
            is Action.TitleInputDone -> onTitleInputDone(action.counterId, action.input)
            is Action.ValueInput -> updateCounterValue(action.counterId, action.inputTextField)
            is Action.ValueInputDone -> onValueInputDone(action.counterId, action.input)
            is Action.StepInput -> onStepInput(action.stepIndex, action.input)
            Action.StepInputDone -> flowOf(InternalAction.ClearFocus)

            Action.SwitchRemoveMode -> flowOf(InternalAction.SwitchRemoveMode)
            is Action.OpenCounterEditDialog -> openEditDialog(action.counterId)
            is Action.CloseCounterEditDialog ->
                closeEditDialog(action.editDialogState, action.restoreInitialItemState)
        }
    }

    private fun createNewCounter() = flow {
        val newCounter = Counter("test", 0, CounterCardColors.getRandom(), listOf(1))
        emit(InternalAction.AddCounterItem(newCounter))
        repository.addCounter(newCounter)
    }

    private fun removeCounter(counterId: String) = flow {
        emit(InternalAction.RemoveCounterItem(counterId))
        repository.removeCounter(counterId)
    }

    private fun changeCounterColor(counterId: String, newColor: Color) = flow<InternalAction> {
        emit(InternalAction.UpdateCounterItemColor(counterId, newColor))
        repository.updateCounterColor(counterId, newColor)
    }

    private fun changeValueBy(counterId: String, by: Int, valueField: TextFieldValue) = flow {
        val newValue = ((valueField.text.toIntOrNull() ?: 0) + by).coerceIn(MIN_VALUE, MAX_VALUE)
        val newField = valueField.copy(text = newValue.toString())

        emit(InternalAction.UpdateCounterItemValueField(counterId, newField))
        repository.updateCounterValue(counterId, newValue)
    }

    private fun updateCounterTitle(counterId: String, inputTextField: TextFieldValue) = flow {
        if (inputTextField.text.length <= 12) {
            emit(InternalAction.UpdateCounterItemTitleField(counterId, inputTextField))
            repository.updateCounterTitle(counterId, inputTextField.text)
        }
    }

    private fun onTitleInputDone(counterId: String, input: String) = flow {
        if (input.isBlank()) emit(InternalAction.ShowTitleError(counterId))
        else emit(InternalAction.ClearFocus)
    }

    private fun updateCounterValue(counterId: String, inputTextField: TextFieldValue) = flow {
        val clearedInputText = inputTextField.text.trim()
        if (clearedInputText.isEmpty() || clearedInputText == "-") {
            emit(InternalAction.UpdateCounterItemValueField(counterId, inputTextField))
            repository.updateCounterValue(counterId, 0)
            return@flow
        }

        val newValue = clearedInputText.toIntOrNull()
        if (newValue != null && newValue in MIN_VALUE..MAX_VALUE) {
            val newTextFieldValue = inputTextField.copy(text = newValue.toString())
            emit(InternalAction.UpdateCounterItemValueField(counterId, newTextFieldValue))
            repository.updateCounterValue(counterId, newValue)
        }
    }

    private fun onValueInputDone(counterId: String, input: String) = flow {
        val valueToSave = input.toIntOrNull() ?: 0

        emit(InternalAction.ClearFocus)
        emit(InternalAction.UpdateCounterItemValueField(counterId, TextFieldValue(valueToSave.toString())))
        repository.updateCounterValue(counterId, valueToSave)
    }

    private fun onStepInput(stepIndex: Int, input: TextFieldValue) = flow {
        if (input.text.length > MAX_VALUE.toString().length || input.text.contains("-")) return@flow
        emit(InternalAction.UpdateStepConfiguratorField(stepIndex, input))
    }

    private fun openEditDialog(counterId: String) = flow {
        emit(InternalAction.ClearFocus)
        emit(InternalAction.OpenEditCounterDialog(counterId))
    }

    private fun closeEditDialog(editDialogState: EditDialogState, restoreInitialState: Boolean) = flow {
        emit(InternalAction.CloseEditCounterDialog)
        if (restoreInitialState) {
            val counterItemToRestore = editDialogState.getInitialCounterItemState()

            emit(InternalAction.RestoreCounterItem(counterItemToRestore))
            repository.setCounter(editDialogState.getInitialCounterItemState().toCounter())
            return@flow
        }

        with(editDialogState) {
            onTitleInputDone(itemState.counterId, itemState.titleField.text).collect(::emit)
            onValueInputDone(itemState.counterId, itemState.valueField.text).collect(::emit)
            saveCounterSteps(itemState.counterId, stepConfiguratorState.steps).collect(::emit)
        }
    }

    private fun saveCounterSteps(counterId: String, stepFields: List<TextFieldValue>) = flow {
        var stepsToSave = stepFields.filter {
            it.text.isNotBlank() && it.text.trim() != "0"
        }.map { it.text.trim().toInt() }
        if (stepsToSave.isEmpty()) stepsToSave = listOf(1)

        emit(InternalAction.UpdateCounterItemSteps(counterId, stepsToSave))
        repository.updateCounterSteps(counterId, stepsToSave)
    }
}