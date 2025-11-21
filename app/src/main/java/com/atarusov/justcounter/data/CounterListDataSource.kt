package com.atarusov.justcounter.data

import androidx.datastore.core.DataStore
import com.atarusov.justcounter.CounterListProto
import com.atarusov.justcounter.CounterProto
import com.atarusov.justcounter.domain.Counter
import com.atarusov.justcounter.domain.CounterListRepository
import com.atarusov.justcounter.domain.toDomain
import com.atarusov.justcounter.domain.toProto
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject


class CounterListDataSource @Inject constructor(
    val dataStore: DataStore<CounterListProto>
) : CounterListRepository {

    override val counters: Flow<List<Counter>>
        get() = dataStore.data.map { counterListProto ->
            counterListProto.countersList.map { it.toDomain() }
        }

    override suspend fun getCounterById(counterId: String): Counter =
        dataStore.data.first().countersList.find { it.id == counterId }?.toDomain()
            ?: throw NoSuchElementException("Counter with id $counterId not found")

    override suspend fun addCounter(counter: Counter) {
        dataStore.updateData { counterListProto ->
            counterListProto.toBuilder().addCounters(counter.toProto()).build()
        }
    }

    override suspend fun setCounter(counter: Counter) {
        dataStore.updateData { counters ->
            val index = counters.countersList.indexOfFirst { it.id == counter.id }
            if (index == -1) return@updateData counters

            counters.toBuilder()
                .setCounters(index, counter.toProto())
                .build()
        }
    }

    override suspend fun updateCounterTitle(counterId: String, newTitle: String) {
        updateCounter(counterId) { it.setTitle(newTitle) }
    }

    override suspend fun updateCounterValue(counterId: String, newValue: Int) {
        updateCounter(counterId) { it.setValue(newValue) }
    }

    override suspend fun updateCounterColor(counterId: String, newColor: String) {
        updateCounter(counterId) { it.setColor(newColor) }
    }

    override suspend fun updateCounterSteps(counterId: String, newSteps: List<Int>) {
        updateCounter(counterId) { it.clearSteps().addAllSteps(newSteps) }
    }

    private suspend fun updateCounter(
        counterId: String,
        transform: (CounterProto.Builder) -> CounterProto.Builder
    ) {
        dataStore.updateData { counters ->
            val index = counters.countersList.indexOfFirst { it.id == counterId }
            if (index == -1) return@updateData counters

            val updatedCounter = transform(counters.countersList[index].toBuilder()).build()

            counters.toBuilder()
                .setCounters(index, updatedCounter)
                .build()
        }
    }

    override suspend fun removeCounter(counterId: String) {
        dataStore.updateData { counterListProto ->
            val itemIndex = counterListProto.countersList.indexOfFirst { it.id == counterId }
            counterListProto.toBuilder().removeCounters(itemIndex).build()
        }
    }

    override suspend fun swapCounters(firstIndex: Int, secondIndex: Int) {
        dataStore.updateData { counterListProto ->
            val firstCounter = counterListProto.countersList[firstIndex]
            val secondCounter = counterListProto.countersList[secondIndex]

            counterListProto.toBuilder()
                .setCounters(firstIndex, secondCounter)
                .setCounters(secondIndex, firstCounter)
                .build()
        }
    }
}