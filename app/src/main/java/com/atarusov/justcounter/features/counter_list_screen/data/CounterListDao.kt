package com.atarusov.justcounter.features.counter_list_screen.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.atarusov.justcounter.common.Counter
import kotlinx.coroutines.flow.Flow

@Dao
interface CounterListDao {

    @Query("SELECT * FROM counters ORDER BY position")
    fun getCounterList(): Flow<List<Counter>>

    @Transaction
    suspend fun addCounter(counter: Counter) {
        if (counter.position != Counter.UNDEFINED_POSITION)
            throw IllegalStateException("Do not pass position parameter when editing Counter")
        val counterWithPosition = counter.copy(
            position = getMaxCounterPosition()?.let { it + 1 } ?: 0
        )
        addCounterRow(counterWithPosition)
    }

    @Query("SELECT MAX(position) FROM counters")
    suspend fun getMaxCounterPosition(): Int?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addCounterRow(counter: Counter)

    @Query("UPDATE counters SET value = :newValue WHERE id = :id")
    suspend fun updateCounterValue(id: String, newValue: Int)

    @Transaction
    suspend fun deleteCounterById(id: String) {
        val counterPosition = getCounterPositionById(id)
        deleteRowById(id)
        shiftPositions(counterPosition)
    }

    @Query("SELECT position FROM counters WHERE id = :id")
    suspend fun getCounterPositionById(id: String): Int

    @Query("DELETE FROM counters WHERE id = :id")
    suspend fun deleteRowById(id: String)

    @Query("UPDATE counters SET position = position - 1 WHERE position > :deletedPosition")
    suspend fun shiftPositions(deletedPosition: Int)

    @Transaction
    suspend fun swapCountersOnPositions(firstPosition: Int, secondPosition: Int) {
        val temp = -1
        setPosition(firstPosition, temp)
        setPosition(secondPosition, firstPosition)
        setPosition(temp, secondPosition)
    }

    @Query("UPDATE counters SET position = :newPosition WHERE position = :oldPosition")
    suspend fun setPosition(oldPosition: Int, newPosition: Int)
}