package com.atarusov.justcounter.features.counters_screen.data

import androidx.datastore.core.DataStore
import com.atarusov.justcounter.CounterListProto
import com.atarusov.justcounter.features.counters_screen.domain.Counter
import com.atarusov.justcounter.features.counters_screen.domain.CounterListRepository
import com.atarusov.justcounter.features.counters_screen.domain.toDomain
import com.atarusov.justcounter.features.counters_screen.domain.toProto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class CounterListDataSource @Inject constructor(
    val dataStore: DataStore<CounterListProto>
) : CounterListRepository {

    override val counterListFlow: Flow<List<Counter>> = dataStore.data.map { counterListProto ->
        counterListProto.countersList.map { it.toDomain() }
    }.flowOn(Dispatchers.IO)

    override suspend fun getAllCounters(): List<Counter> {
        return dataStore.data.map { counterListProto ->
            counterListProto.countersList.map { it.toDomain() }
        }.first()
    }

    override suspend fun getCounterById(counterId: String): Counter {
        return counterListFlow.first().find { it.id == counterId }
            ?: throw NoSuchElementException("Counter with id = $counterId wasn't found")
    }

    override suspend fun addCounter(counter: Counter) {
        dataStore.updateData { counterListProto ->
            counterListProto.toBuilder().addCounters(counter.toProto()).build()
        }
    }

    override suspend fun updateCounter(newCounterState: Counter) {
        dataStore.updateData { counterListProto ->
            val itemIndex =
                counterListProto.countersList.indexOfFirst { it.id == newCounterState.id }
            if (itemIndex != -1) {
                counterListProto.toBuilder()
                    .setCounters(itemIndex, newCounterState.toProto())
                    .build()
            } else counterListProto
        }
    }

    override suspend fun removeCounter(counterId: String) {
        dataStore.updateData { counterListProto ->
            val itemIndex = counterListProto.countersList.indexOfFirst { it.id == counterId }
            counterListProto.toBuilder().removeCounters(itemIndex).build()
        }
    }
}