package com.atarusov.justcounter.common

import androidx.room.TypeConverter
import com.atarusov.justcounter.ui.theme.CounterColor
import kotlinx.serialization.json.Json

class Converters {

    private val json = Json { encodeDefaults = true }

    @TypeConverter
    fun fromIntList(list: List<Int>): String {
        return json.encodeToString(list)
    }

    @TypeConverter
    fun toIntList(value: String): List<Int> {
        return json.decodeFromString(value)
    }

    @TypeConverter
    fun fromCounterColor(color: CounterColor): String {
        return color.name
    }

    @TypeConverter
    fun toCounterColor(value: String): CounterColor {
        return CounterColor.valueOf(value)
    }
}