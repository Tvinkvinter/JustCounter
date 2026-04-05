package com.atarusov.justcounter.features.counter_full_screen.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.atarusov.justcounter.features.counter_full_screen.data.model.CounterWithCategoryName
import kotlinx.coroutines.flow.Flow

@Dao
interface CounterFullScreenDao {

    @Query("""
    SELECT counters.*, categories.name AS categoryName
    FROM counters
    LEFT JOIN categories ON counters.categoryId = categories.id
    WHERE counters.id = :id
    """)
    fun getCounterWithCategoryNameById(id: String): Flow<CounterWithCategoryName?>

    @Query("UPDATE counters SET value = :newValue WHERE id = :id")
    suspend fun updateCounterValue(id: String, newValue: Int)

    @Transaction
    suspend fun deleteCounterById(id: String) {
        val counterCategoryId = getCounterCategoryIdById(id)
        val counterPosition = getCounterPositionById(id)
        deleteRowById(id)
        shiftPositions(counterCategoryId, counterPosition)
    }

    @Query("SELECT categoryId FROM counters WHERE id = :id")
    suspend fun getCounterCategoryIdById(id: String): Int?

    @Query("SELECT position FROM counters WHERE id = :id")
    suspend fun getCounterPositionById(id: String): Int

    @Query("DELETE FROM counters WHERE id = :id")
    suspend fun deleteRowById(id: String)

    @Query("""
        UPDATE counters
        SET position = position - 1
        WHERE position > :deletedPosition
            AND (
                (:categoryId IS NULL AND categoryId IS NULL)
                OR (:categoryId IS NOT NULL AND categoryId = :categoryId)
            )
    """)
    suspend fun shiftPositions(categoryId: Int?, deletedPosition: Int)
}
