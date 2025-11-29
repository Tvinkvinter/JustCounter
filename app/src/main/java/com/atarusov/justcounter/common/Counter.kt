package com.atarusov.justcounter.common

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.atarusov.justcounter.ui.theme.CounterColor
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity(tableName = "counters")
@OptIn(ExperimentalUuidApi::class)
data class Counter(
    val title: String,
    val value: Int,
    val color: CounterColor,
    val steps: List<Int>,
    val position: Int = UNDEFINED_POSITION,
    @PrimaryKey val id: String = Uuid.random().toString()
) {
    companion object {
        const val UNDEFINED_POSITION = -1
    }
}
