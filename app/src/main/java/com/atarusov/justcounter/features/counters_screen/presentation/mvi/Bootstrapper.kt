package com.atarusov.justcounter.features.counters_screen.presentation.mvi

import com.atarusov.justcounter.features.counters_screen.domain.Counter
import com.atarusov.justcounter.features.counters_screen.domain.CounterListRepository
import com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities.InternalAction
import com.atarusov.justcounter.ui.theme.CounterColorProvider
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class Bootstrapper @Inject constructor(
    val repository: CounterListRepository,
    val defaultCounterTitles: List<String>
) {
    fun bootstrap(): Flow<InternalAction> = flow {
        val counters = repository.getAllCounters()
        emit(InternalAction.LoadCounterItems(counters))

        if (counters.isEmpty()) {
            val newCounter = Counter(
                defaultCounterTitles.random(),
                0,
                CounterColorProvider.getRandomColor(),
                listOf(1)
            )

            repository.addCounter(newCounter)
            emit(InternalAction.AddCounterItem(newCounter))
        }
    }
}