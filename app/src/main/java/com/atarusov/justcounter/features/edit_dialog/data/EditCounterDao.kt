package com.atarusov.justcounter.features.edit_dialog.data

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.atarusov.justcounter.common.Counter

@Dao
interface EditCounterDao {

    @Transaction
    suspend fun setCounter(counter: Counter) {
        if (counter.position != Counter.UNDEFINED_POSITION)
            throw IllegalStateException("Do not pass position parameter while editing Counter")
        val counterWithRightPosition = counter.copy(position = getPositionById(counter.id))
        setCounterRow(counterWithRightPosition)
    }

    @Query("SELECT position FROM counters WHERE id = :id")
    suspend fun getPositionById(id: String) : Int

    @Update
    suspend fun setCounterRow(counter: Counter)
}