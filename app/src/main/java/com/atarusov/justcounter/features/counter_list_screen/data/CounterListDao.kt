package com.atarusov.justcounter.features.counter_list_screen.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.atarusov.justcounter.common.Counter
import com.atarusov.justcounter.features.counter_list_screen.data.model.CounterWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface CounterListDao {

    @Query("""
    SELECT
        counters.id AS counter_id,
        counters.title AS counter_title,
        counters.value AS counter_value,
        counters.color AS counter_color,
        counters.steps AS counter_steps,
        counters.position AS counter_position,
        counters.categoryId AS counter_categoryId,

        categories.id AS category_id,
        categories.name AS category_name
    FROM counters
    LEFT JOIN categories ON counters.categoryId = categories.id
    WHERE (:categoryId IS NULL AND categoryId IS NULL)
            OR (:categoryId IS NOT NULL AND categoryId = :categoryId)
    ORDER BY counter_position
""")
    fun getCountersWithCategory(categoryId: Int?): Flow<List<CounterWithCategory>>

    @Transaction
    suspend fun addCounter(counter: Counter) {
        if (counter.position != Counter.UNDEFINED_POSITION)
            throw IllegalStateException("Do not pass position parameter when adding Counter")
        val counterWithPosition = counter.copy(
            position = getMaxCounterPosition(counter.categoryId)?.let { it + 1 } ?: 0
        )
        addCounterRow(counterWithPosition)
    }

    @Query("""
        SELECT MAX(position) FROM counters 
        WHERE (:categoryId IS NULL AND categoryId IS NULL) 
            OR (:categoryId IS NOT NULL AND categoryId = :categoryId)
""")
    suspend fun getMaxCounterPosition(categoryId: Int?): Int?

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
    suspend fun swapCountersOnPositions(categoryId: Int?, firstPosition: Int, secondPosition: Int) {
        val temp = -1
        setPosition(categoryId, firstPosition, temp)
        setPosition(categoryId, secondPosition, firstPosition)
        setPosition(categoryId, temp, secondPosition)
    }

    @Query("""
        UPDATE counters
        SET position = :newPosition
        WHERE position = :oldPosition
            AND (
                (:categoryId IS NULL AND categoryId IS NULL)
                OR (:categoryId IS NOT NULL AND categoryId = :categoryId)
            )
    """)
    suspend fun setPosition(categoryId: Int?, oldPosition: Int, newPosition: Int)
}
