package com.atarusov.justcounter.features.counters_screen.domain

import androidx.compose.ui.graphics.Color
import kotlinx.coroutines.flow.Flow

interface CounterListRepository {
    val counterListFlow: Flow<List<Counter>>
    suspend fun getAllCounters(): List<Counter>
    suspend fun getCounterById(counterId: String): Counter
    suspend fun addCounter(counter: Counter)
    suspend fun setCounter(counter: Counter)
    suspend fun updateCounterTitle(counterId: String, newTitle: String)
    suspend fun updateCounterValue(counterId: String, newValue: Int)
    suspend fun updateCounterColor(counterId: String, newColor: Color)
    suspend fun updateCounterSteps(counterId: String, newSteps: List<Int>)
    suspend fun removeCounter(counterId: String)
}