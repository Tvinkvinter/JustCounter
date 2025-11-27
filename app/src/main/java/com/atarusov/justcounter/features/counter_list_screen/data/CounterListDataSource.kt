package com.atarusov.justcounter.features.counter_list_screen.data

import androidx.datastore.core.DataStore
import com.atarusov.justcounter.CounterListProto
import com.atarusov.justcounter.CounterProto
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CounterListDataSource @Inject constructor(
    val dataStore: DataStore<CounterListProto>
) {

    val counters: Flow<CounterListProto>
        get() = dataStore.data

    suspend fun addCounter(counter: CounterProto) {
        dataStore.updateData { counterListProto ->
            counterListProto.toBuilder().addCounters(counter).build()
        }
    }

    suspend fun updateCounterValue(counterId: String, newValue: Int) {
        updateCounter(counterId) { it.setValue(newValue) }
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

    suspend fun removeCounter(counterId: String) {
        dataStore.updateData { counterListProto ->
            val itemIndex = counterListProto.countersList.indexOfFirst { it.id == counterId }
            counterListProto.toBuilder().removeCounters(itemIndex).build()
        }
    }

    suspend fun swapCounters(firstIndex: Int, secondIndex: Int) {
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