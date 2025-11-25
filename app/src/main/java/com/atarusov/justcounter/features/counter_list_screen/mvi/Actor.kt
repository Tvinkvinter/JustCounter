package com.atarusov.justcounter.features.counter_list_screen.mvi

import com.atarusov.justcounter.domain.Counter
import com.atarusov.justcounter.domain.CounterListRepository
import com.atarusov.justcounter.features.counter_list_screen.mvi.entities.Action
import com.atarusov.justcounter.features.counter_list_screen.mvi.entities.InternalAction
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
            is Action.MinusClick -> changeValueBy(action.counterId, action.oldValue, -action.step)
            is Action.PlusClick -> changeValueBy(action.counterId, action.oldValue, action.step)
            Action.TitleTap -> flowOf(InternalAction.ShowDragTip)

            Action.SwitchRemoveMode -> flowOf(InternalAction.SwitchRemoveMode)
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

    //todo: убрать связь с UI
    private fun changeValueBy(counterId: String, oldValue: Int, by: Int) = flow<InternalAction> {
        val newValue = (oldValue + by).coerceIn(MIN_VALUE, MAX_VALUE)

        emit(InternalAction.UpdateCounterValue(counterId, newValue))
        repository.updateCounterValue(counterId, newValue)
    }

    private fun swapCounters(firstIndex: Int, secondIndex: Int) = flow {
        emit(InternalAction.SwapCounters(firstIndex, secondIndex))
        repository.swapCounters(firstIndex, secondIndex)
    }
}