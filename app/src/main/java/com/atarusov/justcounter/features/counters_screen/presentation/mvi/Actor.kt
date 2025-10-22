package com.atarusov.justcounter.features.counters_screen.presentation.mvi

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import com.atarusov.justcounter.features.counters_screen.domain.Counter
import com.atarusov.justcounter.features.counters_screen.domain.CounterListRepository
import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.Action
import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.InternalAction
import com.atarusov.justcounter.features.counters_screen.presentation.ui.edit_counter_dialog.EditDialogState
import com.atarusov.justcounter.ui.theme.CounterCardColors
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class Actor @Inject constructor(
    val repository: CounterListRepository
) {

    fun handleAction(action: Action): Flow<InternalAction> {
        return when (action) {
            Action.AddCounter -> createNewCounter()
            is Action.RemoveCounter -> removeCounter(action.counterId)

            is Action.ChangeColor -> changeCounterColor(action.counterId, action.newColor)
            is Action.MinusClick -> changeValueBy(action.counterId, -action.step)
            is Action.PlusClick -> changeValueBy(action.counterId, action.step)
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
        val newCounter = repository.getCounterById(counterId).copy(color = newColor)
        emit(InternalAction.UpdateCounterItem(newCounter))
        repository.updateCounterColor(counterId, newColor)
    }

    private fun changeValueBy(counterId: String, by: Int, ) = flow {
        val oldCounter = repository.getCounterById(counterId)
        val newCounter = oldCounter.copy(value = oldCounter.value + by)

        emit(InternalAction.UpdateCounterItem(newCounter))
        repository.updateCounterValue(counterId, oldCounter.value + by)
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
        when {
            inputTextField.text.isBlank() || inputTextField.text == "-" -> {
                emit(InternalAction.UpdateCounterItemValueField(counterId, inputTextField))
                repository.updateCounterValue(counterId, 0)
            }

            inputTextField.text.replace("-", "").length <= 9 -> {
                inputTextField.text.toIntOrNull()?.let { value ->
                    val newTextFieldValue = inputTextField.copy(text = value.toString())
                    emit(InternalAction.UpdateCounterItemValueField(counterId, newTextFieldValue))
                    repository.updateCounterValue(counterId, value)
                }
            }
        }
    }

    private fun onValueInputDone(counterId: String, input: String) = flow {
        var valueToSave = input.toIntOrNull() ?: 0
        if (input.isBlank() || input == "-") valueToSave = 0
        val newCounter = repository.getCounterById(counterId).copy(value = valueToSave)

        emit(InternalAction.ClearFocus)
        emit(InternalAction.UpdateCounterItem(newCounter))
        repository.updateCounterValue(counterId, valueToSave)
    }

    private fun onStepInput(stepIndex: Int, input: TextFieldValue) = flow {
        if (input.text.length > 9 || input.text.contains("-")) return@flow
        emit(InternalAction.UpdateStepConfiguratorField(stepIndex, input))
    }

    private fun openEditDialog(counterId: String) = flow {
        val openDialogForCounter = repository.getCounterById(counterId)

        emit(InternalAction.ClearFocus)
        emit(InternalAction.OpenEditCounterDialog(openDialogForCounter))
    }

    private fun closeEditDialog(editDialogState: EditDialogState, restoreInitialState: Boolean) = flow {
        emit(InternalAction.CloseEditCounterDialog)
        if (restoreInitialState) {
            emit(InternalAction.UpdateCounterItem(editDialogState.getInitialCounterState()))
            repository.setCounter(editDialogState.getInitialCounterState())
            return@flow
        }

        with(editDialogState) {
            onTitleInputDone(itemState.counterId, itemState.titleField.text).collect(::emit)
            onValueInputDone(itemState.counterId, itemState.valueField.text).collect(::emit)
            saveStepsToRepository(itemState.counterId, stepConfiguratorState.steps).collect(::emit)
        }
    }

    private fun saveStepsToRepository(counterId: String, stepFields: List<TextFieldValue>) = flow {
        var stepsToSave = stepFields.filter {
            it.text.isNotBlank() && it.text != "0"
        }.map { it.text.toInt() }

        if (stepsToSave.isEmpty()) stepsToSave = listOf(1)
        val newCounter = repository.getCounterById(counterId).copy(steps = stepsToSave)

        emit(InternalAction.UpdateCounterItem(newCounter))
        repository.updateCounterSteps(counterId, stepsToSave)
    }
}