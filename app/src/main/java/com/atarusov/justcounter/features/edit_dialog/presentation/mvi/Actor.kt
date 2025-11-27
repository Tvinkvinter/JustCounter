package com.atarusov.justcounter.features.edit_dialog.presentation.mvi

import androidx.compose.ui.text.input.TextFieldValue
import com.atarusov.justcounter.common.Counter
import com.atarusov.justcounter.features.edit_dialog.data.EditCounterRepository
import com.atarusov.justcounter.features.edit_dialog.presentation.mvi.entities.Action
import com.atarusov.justcounter.features.edit_dialog.presentation.mvi.entities.InternalAction
import com.atarusov.justcounter.features.edit_dialog.presentation.mvi.entities.State
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class Actor @Inject constructor(
    val repository: EditCounterRepository
) {
    companion object {
        private const val MAX_TITLE_LENGTH = 24
        private const val MIN_VALUE = -999_999_999
        private const val MAX_VALUE = 999_999_999
    }

    fun handleAction(action: Action): Flow<InternalAction> {
        return when (action) {
            is Action.TitleInput -> updateCounterTitle(action.inputField)
            is Action.TitleInputDone -> flowOf(InternalAction.ClearFocus)
            is Action.ValueInput -> updateCounterValue(action.inputField)
            is Action.ValueInputDone -> onValueInputDone(action.input)
            is Action.StepInput -> updateStep(action.stepIndex, action.inputField)
            Action.StepInputDone -> flowOf(InternalAction.ClearFocus)
            Action.RemoveStep -> flowOf(InternalAction.RemoveLastStepField)
            Action.AddStep -> flowOf(InternalAction.AddStepField)
            is Action.ChangeColor -> flowOf(InternalAction.UpdateCounterItemColor(action.newColor))
            is Action.CloseCounterEditDialog -> closeEditDialog(action.state, action.saveChanges)
        }
    }

    private fun updateCounterTitle(inputField: TextFieldValue) = flow {
        if (inputField.text.length <= MAX_TITLE_LENGTH)
            emit(InternalAction.UpdateCounterItemTitleField(inputField))
    }

    private fun updateCounterValue(inputField: TextFieldValue) = flow {
        val clearedInputText = inputField.text.trim()
        if (clearedInputText.isEmpty() || clearedInputText == "-") {
            val newTextField = inputField.copy(text = clearedInputText)
            emit(InternalAction.UpdateCounterItemValueField(newTextField))
            return@flow
        }

        val newValue = clearedInputText.toIntOrNull()
        if (newValue != null && newValue in MIN_VALUE..MAX_VALUE) {
            val newTextField = inputField.copy(text = newValue.toString())
            emit(InternalAction.UpdateCounterItemValueField(newTextField))
        }
    }

    private fun onValueInputDone(input: String) = flowOf(
        InternalAction.ClearFocus,
        InternalAction.UpdateCounterItemValueField(TextFieldValue(input.validateValue().toString()))
    )

    private fun updateStep(stepIndex: Int, inputField: TextFieldValue) = flow {
        val clearedInputText = inputField.text.trim()

        if (clearedInputText.length > MAX_VALUE.toString().length) return@flow
        if (clearedInputText.isNotEmpty() && inputField.text.toUIntOrNull() == null) return@flow

        val newTextFieldValue = if (clearedInputText.isEmpty()) clearedInputText
        else clearedInputText.toUInt().toString()
        val newTextField = inputField.copy(text = newTextFieldValue)

        emit(InternalAction.UpdateStepConfiguratorField(stepIndex, newTextField))
    }

    private fun closeEditDialog(state: State, saveChanges: Boolean) = flow {
        if (saveChanges) {
            if (state.titleField.text.isBlank()) emit(InternalAction.ShowEmptyTitleTip)
            repository.setCounter(state.toCounter())
        }
        emit(InternalAction.CloseEditCounterDialog)
    }

    private fun State.toCounter() = Counter(
        title = titleField.text.validateTitle(),
        value = valueField.text.validateValue(),
        color = color,
        steps = stepConfiguratorState.steps.validateSteps(),
        id = counterId
    )

    fun String.validateTitle() = this.trim()
    fun String.validateValue() = this.toIntOrNull() ?: 0
    fun List<TextFieldValue>.validateSteps(): List<Int> {
        var validatedSteps = this.filter {
            it.text.isNotBlank() && it.text.trim() != "0"
        }.map { it.text.trim().toInt() }
        if (validatedSteps.isEmpty()) validatedSteps = listOf(1)
        return validatedSteps
    }
}