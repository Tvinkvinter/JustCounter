package com.atarusov.justcounter.features.counter_full_screen.data

import com.atarusov.justcounter.features.counter_full_screen.data.model.CounterWithCategoryName
import kotlinx.coroutines.flow.Flow

interface CounterFullScreenRepository {
    suspend fun getCounterFlowById(counterId: String) :Flow<CounterWithCategoryName?>
    suspend fun updateCounterValue(counterId: String, newValue: Int)
    suspend fun removeCounter(counterId: String)
}