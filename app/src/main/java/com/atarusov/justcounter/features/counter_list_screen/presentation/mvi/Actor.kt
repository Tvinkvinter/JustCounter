package com.atarusov.justcounter.features.counter_list_screen.presentation.mvi

import com.atarusov.justcounter.common.Counter
import com.atarusov.justcounter.features.counter_list_screen.data.CounterListRepository
import com.atarusov.justcounter.features.counter_list_screen.presentation.mvi.entities.Action
import com.atarusov.justcounter.features.counter_list_screen.presentation.mvi.entities.Action.*
import com.atarusov.justcounter.features.counter_list_screen.presentation.mvi.entities.InternalAction
import com.atarusov.justcounter.shared_features.analytics.AnalyticsCategoryType
import com.atarusov.justcounter.shared_features.analytics.AnalyticsDirection
import com.atarusov.justcounter.shared_features.analytics.AnalyticsSurface
import com.atarusov.justcounter.shared_features.analytics.AnalyticsTracker
import com.atarusov.justcounter.ui.theme.CounterColorProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class Actor @Inject constructor(
    private val repository: CounterListRepository,
    private val analyticsTracker: AnalyticsTracker,
) {
    @Inject lateinit var defaultCounterTitles: List<String>

    fun handleAction(action: Action): Flow<InternalAction> {
        return when (action) {
            is AddCounter -> createNewCounter(action.categoryId)
            is RemoveCounter -> removeCounter(action.counter)
            is SwapCounters -> swapCounters(action.categoryId, action.firstIndex, action.secondIndex)
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
            TitleTap -> flowOf(InternalAction.ShowDragTip)

            is SwitchRemoveMode -> flow {
                analyticsTracker.logRemoveModeToggled(
                    enabled = action.enabled,
                    surface = AnalyticsSurface.CounterList,
                )
                emit(InternalAction.SwitchRemoveMode)
            }
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
        analyticsTracker.logCounterCreated(
            surface = AnalyticsSurface.CounterList,
            categoryType = if (categoryId == null) {
                AnalyticsCategoryType.Uncategorized
            } else {
                AnalyticsCategoryType.Custom
            },
        )
    }

    private fun removeCounter(counter: Counter) = flow {
        emit(InternalAction.RemoveCounter(counter.id))
        repository.removeCounter(counter.id)
        analyticsTracker.logCounterDeleted(
            surface = AnalyticsSurface.CounterList,
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
            surface = AnalyticsSurface.CounterList,
            direction = direction,
            step = step,
            oldValue = oldValue,
            newValue = newValue,
            hasCustomSteps = hasCustomSteps,
        )
    }

    private fun swapCounters(categoryId: Int?, firstIndex: Int, secondIndex: Int) = flow {
        emit(InternalAction.SwapCounters(firstIndex, secondIndex))
        repository.swapCounters(categoryId, firstIndex, secondIndex)
    }
}
