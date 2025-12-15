package com.atarusov.justcounter.features.counter_list_screen.presentation.mvi

import com.atarusov.justcounter.common.Counter
import com.atarusov.justcounter.features.counter_list_screen.data.CounterListRepository
import com.atarusov.justcounter.features.counter_list_screen.presentation.mvi.entities.Action
import com.atarusov.justcounter.features.counter_list_screen.presentation.mvi.entities.Action.*
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
            is AddCounter -> createNewCounter(action.categoryId)
            is RemoveCounter -> removeCounter(action.counterId)
            is SwapCounters -> swapCounters(action.firstIndex, action.secondIndex)
            is MinusClick -> changeValue(action.counterId, action.oldValue, -action.step)
            is PlusClick -> changeValue(action.counterId, action.oldValue, action.step)
            TitleTap -> flowOf(InternalAction.ShowDragTip)

            SwitchRemoveMode -> flowOf(InternalAction.SwitchRemoveMode)
            is ChangeCategory -> flowOf(InternalAction.ChangeCategory(action.categoryId))
            is ExpandCounter -> flowOf(InternalAction.NavigateToCounterFullScreen(action.counter))
            is OpenCounterEditDialog -> flowOf(InternalAction.OpenEditCounterDialog(action.counter))
        }
    }

    private fun createNewCounter(categoryId: Int?) = flow {
        val newCounter = Counter(
            title = defaultCounterTitles.random(),
            value = 0,
            color = CounterColorProvider.getRandomColor(),
            steps = listOf(1),
            categoryId = categoryId
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