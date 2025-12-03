package com.atarusov.justcounter.common

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.atarusov.justcounter.ui.theme.CounterColor
import com.atarusov.justcounter.ui.theme.CounterColorProvider
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
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
        const val MIN_VALUE = -999_999_999
        const val MAX_VALUE = 999_999_999
        const val UNDEFINED_POSITION = -1

        fun getPreviewCounter(withCustomSteps: Boolean = false) = Counter (
            title = "Tvinkvinter",
            value = 128000,
            color = CounterColorProvider.getRandomColor(),
            steps = if (withCustomSteps) listOf(1, 2, 300) else listOf(1),
        )
    }
}
