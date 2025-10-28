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

    @Inject lateinit var defaultCounterTitles: List<String>

    fun handleAction(action: Action): Flow<InternalAction> {
        return when (action) {
            Action.AddCounter -> createNewCounter()
            is Action.RemoveCounter -> removeCounter(action.counterId)

            is Action.ChangeColor -> changeCounterColor(action.counterId, action.newColor)
            is Action.MinusClick -> changeValueBy(action.counterId, -action.step, action.valueField)
            is Action.PlusClick -> changeValueBy(action.counterId, action.step, action.valueField)
            Action.RemoveStep -> flowOf(InternalAction.RemoveLastStepField)
            Action.AddStep -> flowOf(InternalAction.AddStepField)

            is Action.TitleInput -> updateCounterTitle(action.counterId, action.inputField)
            is Action.TitleInputDone -> onTitleInputDone(action.input)
            is Action.ValueInput -> updateCounterValue(action.counterId, action.inputField)
            is Action.ValueInputDone -> onValueInputDone(action.counterId, action.input)
            is Action.StepInput -> onStepInput(action.stepIndex, action.inputField)
            Action.StepInputDone -> flowOf(InternalAction.ClearFocus)

            Action.SwitchRemoveMode -> flowOf(InternalAction.SwitchRemoveMode)
            Action.TitleTap -> flowOf(InternalAction.ShowDragTip)
            is Action.SwapCounters -> swapCounters(action.firstIndex, action.secondIndex)
            is Action.OpenCounterEditDialog -> openEditDialog(action.counterId)
            is Action.CloseCounterEditDialog ->
                closeEditDialog(action.editDialogState, action.restoreInitialItemState)
        }
    }

    private fun createNewCounter() = flow {
        val newCounter = Counter(
            title = defaultCounterTitles.random(),
            value = 0,
            color = CounterCardColors.getRandom(),
            steps = listOf(1)
        )

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

    private fun updateCounterTitle(counterId: String, inputField: TextFieldValue) = flow {
        if (inputField.text.length <= 12) {
            emit(InternalAction.UpdateCounterItemTitleField(counterId, inputField))
            repository.updateCounterTitle(counterId, inputField.text)
        }
    }

    private fun onTitleInputDone(input: String) = flow {
        if (input.isBlank()) emit(InternalAction.ShowTitleError)
        else emit(InternalAction.ClearFocus)
    }

    private fun updateCounterValue(counterId: String, inputField: TextFieldValue) = flow {
        val clearedInputText = inputField.text.trim()
        if (clearedInputText.isEmpty() || clearedInputText == "-") {
            emit(InternalAction.UpdateCounterItemValueField(counterId, inputField))
            repository.updateCounterValue(counterId, 0)
            return@flow
        }

        val newValue = clearedInputText.toIntOrNull()
        if (newValue != null && newValue in MIN_VALUE..MAX_VALUE) {
            val newTextField = inputField.copy(text = newValue.toString())
            emit(InternalAction.UpdateCounterItemValueField(counterId, newTextField))
            repository.updateCounterValue(counterId, newValue)
        }
    }

    private fun onValueInputDone(counterId: String, input: String) = flow {
        val valueToSave = input.toIntOrNull() ?: 0

        emit(InternalAction.ClearFocus)
        emit(InternalAction.UpdateCounterItemValueField(counterId, TextFieldValue(valueToSave.toString())))
        repository.updateCounterValue(counterId, valueToSave)
    }

    private fun onStepInput(stepIndex: Int, inputField: TextFieldValue) = flow {
        if (inputField.text.length > MAX_VALUE.toString().length) return@flow
        if (inputField.text.contains("-")) return@flow

        emit(InternalAction.UpdateStepConfiguratorField(stepIndex, inputField))
    }

    private fun swapCounters(firstIndex: Int, secondIndex: Int) = flow {
        emit(InternalAction.SwapCounters(firstIndex, secondIndex))
        repository.swapCounters(firstIndex, secondIndex)
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
            onTitleInputDone(itemState.titleField.text).collect(::emit)
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