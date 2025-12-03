package com.atarusov.justcounter.common

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.atarusov.justcounter.features.counter_full_screen.data.CounterFullScreenDao
import com.atarusov.justcounter.features.counter_list_screen.data.CounterListDao
import com.atarusov.justcounter.features.edit_dialog.data.EditCounterDao

@Database(entities = [Counter::class], version = 1)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun counterFullScreenDao(): CounterFullScreenDao
    abstract fun counterListDao(): CounterListDao
    abstract fun editCounter(): EditCounterDao
}