package com.atarusov.justcounter.features.counter_full_screen.data

import com.atarusov.justcounter.features.counter_full_screen.data.model.CounterWithCategoryName
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CounterFullScreenRepositoryImpl @Inject constructor(
    val counterFullScreenDao: CounterFullScreenDao
) : CounterFullScreenRepository {
    override suspend fun getCounterFlowById(counterId: String): Flow<CounterWithCategoryName?> =
        counterFullScreenDao.getCounterWithCategoryNameById(counterId)

    override suspend fun updateCounterValue(counterId: String, newValue: Int) =
        counterFullScreenDao.updateCounterValue(counterId, newValue)

    override suspend fun removeCounter(counterId: String) =
        counterFullScreenDao.deleteCounterById(counterId)
}