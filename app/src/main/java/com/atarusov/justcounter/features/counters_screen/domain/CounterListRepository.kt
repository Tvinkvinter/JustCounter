package com.atarusov.justcounter.features.counters_screen.domain

import androidx.compose.ui.graphics.Color

interface CounterListRepository {
    suspend fun getAllCounters(): List<Counter>
    suspend fun addCounter(counter: Counter)
    suspend fun setCounter(counter: Counter)
    suspend fun updateCounterTitle(counterId: String, newTitle: String)
    suspend fun updateCounterValue(counterId: String, newValue: Int)
    suspend fun updateCounterColor(counterId: String, newColor: Color)
    suspend fun updateCounterSteps(counterId: String, newSteps: List<Int>)
    suspend fun removeCounter(counterId: String)
}