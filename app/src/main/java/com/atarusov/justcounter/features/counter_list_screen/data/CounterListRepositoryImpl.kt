package com.atarusov.justcounter.features.counter_list_screen.data

import com.atarusov.justcounter.common.Counter
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CounterListRepositoryImpl @Inject constructor(
    val counterListDao: CounterListDao
) : CounterListRepository {

    override suspend fun getCountersFlow(): Flow<List<Counter>> = counterListDao.getCounterList()

    override suspend fun addCounter(counter: Counter) = counterListDao.addCounter(counter)

    override suspend fun updateCounterValue(counterId: String, newValue: Int) =
        counterListDao.updateCounterValue(counterId, newValue)

    override suspend fun removeCounter(counterId: String) = counterListDao.deleteCounterById(counterId)

    override suspend fun swapCounters(firstPosition: Int, secondPosition: Int) =
        counterListDao.swapCountersOnPositions(firstPosition, secondPosition)
}