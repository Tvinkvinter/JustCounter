package com.atarusov.justcounter.features.counter_list_screen.presentation.mvi

import com.atarusov.justcounter.common.Counter
import com.atarusov.justcounter.features.counter_list_screen.data.CounterListRepository
import com.atarusov.justcounter.features.counter_list_screen.presentation.mvi.entities.Action
import com.atarusov.justcounter.features.counter_list_screen.presentation.mvi.entities.InternalAction
import com.atarusov.justcounter.ui.theme.CounterColorProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class Actor @Inject constructor(
    val repository: CounterListRepository
) {
    @Inject lateinit var defaultCounterTitles: List<String>

    fun handleAction(action: Action): Flow<InternalAction> {
        return when (action) {
            Action.AddCounter -> createNewCounter()
            is Action.RemoveCounter -> removeCounter(action.counterId)
            is Action.SwapCounters -> swapCounters(action.firstIndex, action.secondIndex)
            is Action.MinusClick -> changeValue(action.counterId, action.oldValue, -action.step)
            is Action.PlusClick -> changeValue(action.counterId, action.oldValue, action.step)
            Action.TitleTap -> flowOf(InternalAction.ShowDragTip)

            Action.SwitchRemoveMode -> flowOf(InternalAction.SwitchRemoveMode)
            is Action.ExpandCounter -> flowOf(InternalAction.NavigateToCounterFullScreen(action.counter))
            is Action.OpenCounterEditDialog -> flowOf(InternalAction.OpenEditCounterDialog(action.counter))
        }
    }

    private fun createNewCounter() = flow {
        val newCounter = Counter(
            title = defaultCounterTitles.random(),
            value = 0,
            color = CounterColorProvider.getRandomColor(),
            steps = listOf(1)
        )

        emit(InternalAction.AddCounter(newCounter))
        emit(InternalAction.ScrollDown)
        repository.addCounter(newCounter)
    }

    private fun removeCounter(counterId: String) = flow {
        emit(InternalAction.RemoveCounter(counterId))
        repository.removeCounter(counterId)
    }

    private fun changeValue(counterId: String, oldValue: Int, step: Int) = flow<InternalAction> {
        val newValue = (oldValue + step).coerceIn(Counter.MIN_VALUE, Counter.MAX_VALUE)

        emit(InternalAction.UpdateCounterValue(counterId, newValue))
        repository.updateCounterValue(counterId, newValue)
    }

    private fun swapCounters(firstIndex: Int, secondIndex: Int) = flow {
        emit(InternalAction.SwapCounters(firstIndex, secondIndex))
        repository.swapCounters(firstIndex, secondIndex)
    }
}