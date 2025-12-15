package com.atarusov.justcounter.features.counter_list_screen.presentation.mvi

import com.atarusov.justcounter.common.Counter
import com.atarusov.justcounter.features.counter_list_screen.data.CounterListRepository
import com.atarusov.justcounter.features.counter_list_screen.presentation.mvi.entities.InternalAction
import com.atarusov.justcounter.ui.theme.CounterColorProvider
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

class Bootstrapper @Inject constructor(
    val repository: CounterListRepository,
    val defaultCounterTitles: List<String>
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun bootstrap(categoryIds: StateFlow<Int?>): Flow<InternalAction> =
        categoryIds.flatMapLatest { categoryId ->
            repository.getCountersOfCategory(categoryId).transform { countersOfCategory ->
                if (countersOfCategory.counters.isEmpty()) {
                    val newCounter = Counter(
                        title = defaultCounterTitles.random(),
                        value = 0,
                        color = CounterColorProvider.getRandomColor(),
                        steps = listOf(1),
                        categoryId = categoryId
                    )

                    repository.addCounter(newCounter)
                    emit(InternalAction.AddCounter(newCounter))
                }
                emit(InternalAction.LoadData(countersOfCategory))
            }
    }
}