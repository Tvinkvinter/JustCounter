package com.atarusov.justcounter.common

import androidx.room.AutoMigration
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.atarusov.justcounter.features.category_drawer.data.CategoryDao
import com.atarusov.justcounter.features.counter_full_screen.data.CounterFullScreenDao
import com.atarusov.justcounter.features.counter_list_screen.data.CounterListDao
import com.atarusov.justcounter.features.edit_dialog.data.EditCounterDao

@Database(
    entities = [Counter::class, Category::class],
    version = 2,
    autoMigrations = [AutoMigration(from = 1, to = 2)]
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun categoriesDao(): CategoryDao
    abstract fun counterFullScreenDao(): CounterFullScreenDao
    abstract fun counterListDao(): CounterListDao
    abstract fun editCounter(): EditCounterDao
}