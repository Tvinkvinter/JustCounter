package com.atarusov.justcounter.features.counters_screen.mvi

import androidx.compose.ui.text.input.TextFieldValue
import com.atarusov.justcounter.domain.Counter
import com.atarusov.justcounter.domain.CounterListRepository
import com.atarusov.justcounter.features.counters_screen.mvi.entities.Action
import com.atarusov.justcounter.features.counters_screen.mvi.entities.InternalAction
import com.atarusov.justcounter.ui.theme.CounterColorProvider
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
            is Action.SwapCounters -> swapCounters(action.firstIndex, action.secondIndex)
            is Action.MinusClick -> changeValueBy(action.counterId, -action.step, action.valueField)
            is Action.PlusClick -> changeValueBy(action.counterId, action.step, action.valueField)
            is Action.ValueInput -> updateCounterValue(action.counterId, action.inputField)
            is Action.ValueInputDone -> onValueInputDone(action.counterId, action.input)
            Action.TitleTap -> flowOf(InternalAction.ShowDragTip)

            Action.SwitchRemoveMode -> flowOf(InternalAction.SwitchRemoveMode)
            is Action.OpenCounterEditDialog -> openEditDialog(action.counterId)
        }
    }

    private fun createNewCounter() = flow {
        val newCounter = Counter(
            title = defaultCounterTitles.random(),
            value = 0,
            color = CounterColorProvider.getRandomColor(),
            steps = listOf(1)
        )

        emit(InternalAction.AddCounterItem(newCounter))
        emit(InternalAction.ScrollDown)
        repository.addCounter(newCounter)
    }

    private fun removeCounter(counterId: String) = flow {
        emit(InternalAction.RemoveCounterItem(counterId))
        repository.removeCounter(counterId)
    }

    //todo: убрать связь с UI
    private fun changeValueBy(counterId: String, by: Int, valueField: TextFieldValue) = flow<InternalAction> {
        val newValue = ((valueField.text.toIntOrNull() ?: 0) + by).coerceIn(MIN_VALUE, MAX_VALUE)
        val newField = valueField.copy(text = newValue.toString())

        emit(InternalAction.UpdateCounterItemValueField(counterId, newField))
        repository.updateCounterValue(counterId, newValue)
    }

    private fun updateCounterValue(counterId: String, inputField: TextFieldValue) = flow {
        val clearedInputText = inputField.text.trim()
        if (clearedInputText.isEmpty() || clearedInputText == "-") {
            val newTextField = inputField.copy(text = clearedInputText)
            emit(InternalAction.UpdateCounterItemValueField(counterId, newTextField))
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

    private fun swapCounters(firstIndex: Int, secondIndex: Int) = flow {
        emit(InternalAction.SwapCounters(firstIndex, secondIndex))
        repository.swapCounters(firstIndex, secondIndex)
    }

    private fun openEditDialog(counterId: String) = flow {
        emit(InternalAction.ClearFocus)
        emit(InternalAction.OpenEditCounterDialog(counterId))
    }
}