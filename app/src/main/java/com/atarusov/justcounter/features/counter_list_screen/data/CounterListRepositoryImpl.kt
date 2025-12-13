package com.atarusov.justcounter.features.counter_list_screen.data

import com.atarusov.justcounter.common.Counter
import com.atarusov.justcounter.features.counter_list_screen.data.model.CountersOfCategory
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CounterListRepositoryImpl @Inject constructor(
    val counterListDao: CounterListDao
) : CounterListRepository {

    override suspend fun getCountersOfCategory(categoryId: Int?): Flow<CountersOfCategory> =
        counterListDao.getCountersWithCategory(categoryId).map { countersWithCategory ->
            CountersOfCategory(
                category = countersWithCategory.first().category,
                counters = countersWithCategory.map { it.counter }
            )
        }

    override suspend fun addCounter(counter: Counter) = counterListDao.addCounter(counter)

    override suspend fun updateCounterValue(counterId: String, newValue: Int) =
        counterListDao.updateCounterValue(counterId, newValue)

    override suspend fun removeCounter(counterId: String) = counterListDao.deleteCounterById(counterId)

    override suspend fun swapCounters(firstPosition: Int, secondPosition: Int) =
        counterListDao.swapCountersOnPositions(firstPosition, secondPosition)
}