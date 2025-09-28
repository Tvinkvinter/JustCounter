package com.atarusov.justcounter.features.counters_screen.domain

import kotlinx.coroutines.flow.Flow

interface CounterListRepository {
    val counterListFlow: Flow<List<Counter>>
    suspend fun addCounter(counter: Counter)
    suspend fun updateCounter(newCounterState: Counter)
    suspend fun removeCounter(counterId: String)
}