package com.atarusov.justcounter.features.counters_screen.data

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.datastore.core.DataStore
import com.atarusov.justcounter.CounterListProto
import com.atarusov.justcounter.CounterProto
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

    override suspend fun changeCounterValueBy(counterId: String, by: Int) {
        updateCounter(counterId) {it.setValue(it.value + by)}
    }

    override suspend fun updateCounterColor(counterId: String, newColor: Color) {
        updateCounter(counterId) { it.setColor(newColor.toArgb()) }
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
}