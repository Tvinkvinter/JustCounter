package com.atarusov.justcounter.shared_features.analytics.data

import android.content.Context
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import javax.inject.Inject
import javax.inject.Singleton

private val Context.analyticsDataStore by preferencesDataStore(name = "analytics")

data class AppOpenMetadata(
    val isFirstOpen: Boolean,
    val firstOpenTimestampMs: Long,
)

@Singleton
class AnalyticsPreferences @Inject constructor(
    @param:ApplicationContext private val context: Context,
) {
    private object Keys {
        val FIRST_OPEN_TIMESTAMP_MS = longPreferencesKey("first_open_timestamp_ms")
        val COUNTER_CREATED_LOGGED = booleanPreferencesKey("counter_created_logged")
        val COUNTER_VALUE_CHANGED_LOGGED = booleanPreferencesKey("counter_value_changed_logged")
    }

    suspend fun recordAppOpen(timestampMs: Long): AppOpenMetadata {
        var metadata = AppOpenMetadata(
            isFirstOpen = false,
            firstOpenTimestampMs = timestampMs,
        )

        context.analyticsDataStore.edit { preferences ->
            val firstOpenTimestampMs = preferences[Keys.FIRST_OPEN_TIMESTAMP_MS]
            metadata = AppOpenMetadata(
                isFirstOpen = firstOpenTimestampMs == null,
                firstOpenTimestampMs = firstOpenTimestampMs ?: timestampMs,
            )
            if (firstOpenTimestampMs == null) {
                preferences[Keys.FIRST_OPEN_TIMESTAMP_MS] = timestampMs
            }
        }

        return metadata
    }

    suspend fun recordCounterCreated(): Boolean = consumeFirstEvent(Keys.COUNTER_CREATED_LOGGED)

    suspend fun recordCounterValueChanged(): Boolean =
        consumeFirstEvent(Keys.COUNTER_VALUE_CHANGED_LOGGED)

    private suspend fun consumeFirstEvent(key: Preferences.Key<Boolean>): Boolean {
        var isFirst = false
        context.analyticsDataStore.edit { preferences ->
            isFirst = preferences[key] != true
            preferences[key] = true
        }
        return isFirst
    }
}
