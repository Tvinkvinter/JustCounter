package com.atarusov.justcounter.features.counter_full_screen.presentation.mvi

import com.atarusov.justcounter.common.Counter
import com.atarusov.justcounter.features.counter_full_screen.data.CounterFullScreenRepository
import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities.Action
import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities.Action.*
import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities.InternalAction
import com.atarusov.justcounter.shared_features.analytics.AnalyticsDirection
import com.atarusov.justcounter.shared_features.analytics.AnalyticsSurface
import com.atarusov.justcounter.shared_features.analytics.AnalyticsTracker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class Actor @Inject constructor(
    private val repository: CounterFullScreenRepository,
    private val analyticsTracker: AnalyticsTracker,
) {

    fun handleAction(action: Action): Flow<InternalAction> {
        return when (action) {
            is RemoveCounter -> removeCounter(action.counter)
            is MinusClick -> changeValue(
                counterId = action.counterId,
                oldValue = action.oldValue,
                step = action.step,
                hasCustomSteps = action.hasCustomSteps,
                direction = AnalyticsDirection.Minus,
            )
            is PlusClick -> changeValue(
                counterId = action.counterId,
                oldValue = action.oldValue,
                step = action.step,
                hasCustomSteps = action.hasCustomSteps,
                direction = AnalyticsDirection.Plus,
            )

            BackPressed -> flowOf(InternalAction.NavigateBack)
            is SwitchRemoveMode -> flow {
                analyticsTracker.logRemoveModeToggled(
                    enabled = action.enabled,
                    surface = AnalyticsSurface.CounterFullscreen,
                )
                emit(InternalAction.SwitchRemoveMode)
            }
            is OpenCounterEditDialog -> flowOf(InternalAction.OpenEditCounterDialog(action.counter))
        }
    }

    private fun removeCounter(counter: Counter) = flow<InternalAction> {
        emit(InternalAction.NavigateBack)
        repository.removeCounter(counter.id)
        analyticsTracker.logCounterDeleted(
            surface = AnalyticsSurface.CounterFullscreen,
            valueNonzero = counter.value != 0,
            hasCustomSteps = counter.steps != listOf(1),
        )
    }

    private fun changeValue(
        counterId: String,
        oldValue: Int,
        step: Int,
        hasCustomSteps: Boolean,
        direction: AnalyticsDirection,
    ) = flow<InternalAction> {
        val signedStep = if (direction == AnalyticsDirection.Plus) step else -step
        val newValue = (oldValue + signedStep).coerceIn(Counter.MIN_VALUE, Counter.MAX_VALUE)

        emit(InternalAction.UpdateCounterValue(counterId, newValue))
        repository.updateCounterValue(counterId, newValue)
        analyticsTracker.logCounterValueChanged(
            surface = AnalyticsSurface.CounterFullscreen,
            direction = direction,
            step = step,
            oldValue = oldValue,
            newValue = newValue,
            hasCustomSteps = hasCustomSteps,
        )
    }
}
