package com.atarusov.justcounter.features.counter_list_screen.data

import com.atarusov.justcounter.common.Counter
import com.atarusov.justcounter.features.counter_list_screen.data.model.CountersOfCategory
import kotlinx.coroutines.flow.Flow

interface CounterListRepository {
    suspend fun getCountersOfCategory(categoryId: Int?) : Flow<CountersOfCategory>
    suspend fun addCounter(counter: Counter)
    suspend fun updateCounterValue(counterId: String, newValue: Int)
    suspend fun removeCounter(counterId: String)
    suspend fun swapCounters(firstPosition: Int, secondPosition: Int)
}