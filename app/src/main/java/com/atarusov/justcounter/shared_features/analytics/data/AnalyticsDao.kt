package com.atarusov.justcounter.shared_features.analytics.data

import androidx.room.Dao
import androidx.room.Query

@Dao
interface AnalyticsDao {
    @Query("SELECT COUNT(*) FROM counters")
    suspend fun getCountersCount(): Long

    @Query("SELECT COUNT(*) FROM categories")
    suspend fun getCategoriesCount(): Long
}
