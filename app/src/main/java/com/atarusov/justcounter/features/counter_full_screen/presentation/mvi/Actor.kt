package com.atarusov.justcounter.features.counter_full_screen.presentation.mvi

import com.atarusov.justcounter.common.Counter
import com.atarusov.justcounter.features.counter_full_screen.data.CounterFullScreenRepository
import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities.Action
import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities.InternalAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class Actor @Inject constructor(
    val repository: CounterFullScreenRepository
) {

    fun handleAction(action: Action): Flow<InternalAction> {
        return when (action) {
            is Action.RemoveCounter -> removeCounter(action.counterId)
            is Action.MinusClick -> changeValue(action.counterId, action.oldValue, -action.step)
            is Action.PlusClick -> changeValue(action.counterId, action.oldValue, action.step)

            Action.BackPressed -> flowOf(InternalAction.NavigateBack)
            Action.SwitchRemoveMode -> flowOf(InternalAction.SwitchRemoveMode)
            is Action.OpenCounterEditDialog -> flowOf(InternalAction.OpenEditCounterDialog(action.counter))
        }
    }

    private fun removeCounter(counterId: String) = flow<InternalAction> {
        emit(InternalAction.NavigateBack)
        repository.removeCounter(counterId)
    }

    private fun changeValue(counterId: String, oldValue: Int, step: Int) = flow<InternalAction> {
        val newValue = (oldValue + step).coerceIn(Counter.MIN_VALUE, Counter.MAX_VALUE)

        emit(InternalAction.UpdateCounterValue(counterId, newValue))
        repository.updateCounterValue(counterId, newValue)
    }
}