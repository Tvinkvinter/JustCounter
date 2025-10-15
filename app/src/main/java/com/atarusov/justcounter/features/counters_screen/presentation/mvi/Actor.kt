package com.atarusov.justcounter.features.counters_screen.presentation.mvi

import androidx.compose.ui.graphics.Color
import com.atarusov.justcounter.features.counters_screen.domain.Counter
import com.atarusov.justcounter.features.counters_screen.domain.CounterListRepository
import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.Action
import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.CounterItem
import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.InternalAction
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
            Action.GetAllCounters -> getAllCounters()
            Action.AddCounter -> createNewCounter()
            is Action.RemoveCounter -> removeCounter(action.counterId)

            is Action.ChangeColor -> changeCounterColor(action.counterId, action.newColor)
            is Action.MinusClick -> changeValueBy(action.counterId, -1)
            is Action.PlusClick -> changeValueBy(action.counterId, 1)
            is Action.TitleInput -> updateCounterTitle(action.counterId, action.input)
            is Action.TitleInputDone -> onTitleInputDone(action.counterId, action.input)
            is Action.ValueInput -> updateCounterValue(action.counterId, action.input)
            is Action.ValueInputDone -> onValueInputDone(action.counterId, action.input)

            Action.SwitchRemoveMode -> flowOf(InternalAction.SwitchRemoveMode)
            is Action.OpenCounterEditDialog -> openEditDialog(action.counterId)
            is Action.CloseCounterEditDialog -> closeEditDialog(action.currentItem, action.counterToRestore)
        }
    }

    private fun getAllCounters() = flow {
        val list = repository.getAllCounters()
        emit(InternalAction.LoadCounterItems(list))
    }

    private fun createNewCounter() = flow {
        val newCounter = Counter("test", 0, CounterCardColors.getRandom())
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
        repository.updateCounter(newCounter)
    }

    private fun changeValueBy(counterId: String, by: Int, ) = flow {
        val oldCounter = repository.getCounterById(counterId)
        val newCounter = oldCounter.copy(value = oldCounter.value + by)

        emit(InternalAction.UpdateCounterItem(newCounter))
        repository.updateCounter(newCounter)
    }

    private fun updateCounterTitle(counterId: String, newTitle: String) = flow {
        if (newTitle.length <= 12) {
            val newCounter = repository.getCounterById(counterId).copy(title = newTitle)

            emit(InternalAction.UpdateCounterItem(newCounter))
            repository.updateCounter(newCounter)
        }
    }

    private fun onTitleInputDone(counterId: String, input: String) = flow {
        if (input.isBlank()) emit(InternalAction.ShowTitleError(counterId))
        else emit(InternalAction.ClearFocus)
    }

    private fun updateCounterValue(counterId: String, input: String) = flow {
        val counter = repository.getCounterById(counterId)

        when {
            input.isBlank() || input == "-" -> {
                emit(InternalAction.UpdateCounterItemValueField(counterId, input))
                repository.updateCounter(counter.copy(value = 0))
            }

            input.replace("-", "").length <= 9 -> {
                input.toIntOrNull()?.let { value ->
                    val newCounter = counter.copy(value = value)
                    emit(InternalAction.UpdateCounterItem(newCounter))
                    repository.updateCounter(newCounter)
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
        repository.updateCounter(newCounter)
    }

    private fun openEditDialog(counterId: String) = flow {
        val openDialogForCounter = repository.getCounterById(counterId)

        emit(InternalAction.ClearFocus)
        emit(InternalAction.OpenEditCounterDialog(openDialogForCounter))
    }

    private fun closeEditDialog(currentItem: CounterItem, counterToRestore: Counter? = null) = flow {
        emit(InternalAction.CloseEditCounterDialog)
        counterToRestore?.let {
            emit(InternalAction.UpdateCounterItem(it))
            repository.updateCounter(it)
            return@flow
        }

        onTitleInputDone(currentItem.counterId, currentItem.titleField).collect(::emit)
        onValueInputDone(currentItem.counterId, currentItem.valueField).collect(::emit)
        emit(InternalAction.ClearFocus)
    }
}