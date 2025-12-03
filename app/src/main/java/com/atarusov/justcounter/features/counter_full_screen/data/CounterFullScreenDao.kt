package com.atarusov.justcounter.features.counter_full_screen.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.atarusov.justcounter.common.Counter
import kotlinx.coroutines.flow.Flow

@Dao
interface CounterFullScreenDao {
    @Query("SELECT * FROM counters WHERE id = :id")
    fun getCounterById(id: String): Flow<Counter?>

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
}