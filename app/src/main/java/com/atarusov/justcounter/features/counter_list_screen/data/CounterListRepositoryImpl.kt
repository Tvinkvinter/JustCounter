package com.atarusov.justcounter.features.counter_list_screen.data

import com.atarusov.justcounter.common.Counter
import com.atarusov.justcounter.common.toCounter
import com.atarusov.justcounter.common.toProto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CounterListRepositoryImpl @Inject constructor(
    val dataSource: CounterListDataSource
) : CounterListRepository {

    override suspend fun getCountersFlow(): Flow<List<Counter>> =
        dataSource.counters.map { counterListProto ->
            counterListProto.countersList.map { it.toCounter() }
        }

    override suspend fun addCounter(counter: Counter) = dataSource.addCounter(counter.toProto())

    override suspend fun updateCounterValue(counterId: String, newValue: Int) =
        dataSource.updateCounterValue(counterId, newValue)

    override suspend fun removeCounter(counterId: String) = dataSource.removeCounter(counterId)

    override suspend fun swapCounters(firstIndex: Int, secondIndex: Int) =
        dataSource.swapCounters(firstIndex, secondIndex)

}