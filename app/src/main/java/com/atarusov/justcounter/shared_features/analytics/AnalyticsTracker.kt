package com.atarusov.justcounter.shared_features.analytics

import android.os.SystemClock
import com.atarusov.justcounter.shared_features.analytics.data.AnalyticsDao
import com.atarusov.justcounter.shared_features.analytics.data.AnalyticsPreferences
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.absoluteValue

@Singleton
class AnalyticsTracker @Inject internal constructor(
    private val eventLogger: AnalyticsEventLogger,
    private val preferences: AnalyticsPreferences,
    private val analyticsDao: AnalyticsDao,
) {
    private val screenMutex = Mutex()

    private var appInForeground = false
    private var currentDestination: AnalyticsDestination? = null
    private var screenStartedAtMs: Long? = null

    suspend fun onAppForegrounded() = screenMutex.withLock {
        if (appInForeground) return@withLock
        appInForeground = true

        val timestampMs = System.currentTimeMillis()
        val appOpenMetadata = preferences.recordAppOpen(timestampMs)
        eventLogger.log(
            AnalyticsEvents.appOpen(
                isFirstOpen = appOpenMetadata.isFirstOpen,
                daysSinceFirstOpen = TimeUnit.MILLISECONDS.toDays(
                    (timestampMs - appOpenMetadata.firstOpenTimestampMs).coerceAtLeast(0L)
                ),
                countersCount = analyticsDao.getCountersCount(),
                categoriesCount = analyticsDao.getCategoriesCount(),
            )
        )

        currentDestination?.let { destination ->
            logScreenView(destination, APP_START_SOURCE)
        }
    }

    suspend fun onAppBackgrounded() = screenMutex.withLock {
        if (!appInForeground) return@withLock
        closeCurrentScreen()
        appInForeground = false
    }

    suspend fun trackScreen(destination: AnalyticsDestination) = screenMutex.withLock {
        if (currentDestination == destination && screenStartedAtMs != null) return@withLock

        val source = currentDestination?.value ?: APP_START_SOURCE
        closeCurrentScreen()
        currentDestination = destination

        if (appInForeground) {
            logScreenView(destination, source)
        }
    }

    suspend fun logCounterCreated(
        surface: AnalyticsSurface,
        categoryType: AnalyticsCategoryType,
    ) {
        eventLogger.log(
            AnalyticsEvents.counterCreated(
                isFirstForUser = preferences.recordCounterCreated(),
                surface = surface,
                categoryType = categoryType,
                countersCountAfter = analyticsDao.getCountersCount(),
            )
        )
    }

    suspend fun logCounterValueChanged(
        surface: AnalyticsSurface,
        direction: AnalyticsDirection,
        step: Int,
        oldValue: Int,
        newValue: Int,
        hasCustomSteps: Boolean,
    ) {
        if (oldValue == newValue) return

        eventLogger.log(
            AnalyticsEvents.counterValueChanged(
                isFirstForUser = preferences.recordCounterValueChanged(),
                surface = surface,
                direction = direction,
                step = step.toLong().absoluteValue,
                oldValue = oldValue.toLong(),
                newValue = newValue.toLong(),
                hasCustomSteps = hasCustomSteps,
            )
        )
    }

    suspend fun logCounterDeleted(
        surface: AnalyticsSurface,
        valueNonzero: Boolean,
        hasCustomSteps: Boolean,
    ) {
        eventLogger.log(
            AnalyticsEvents.counterDeleted(
                surface = surface,
                valueNonzero = valueNonzero,
                hasCustomSteps = hasCustomSteps,
                countersCountAfter = analyticsDao.getCountersCount(),
            )
        )
    }

    fun logRemoveModeToggled(
        enabled: Boolean,
        surface: AnalyticsSurface,
    ) {
        eventLogger.log(AnalyticsEvents.removeModeToggled(enabled, surface))
    }

    fun logCounterEditClosed(
        saved: Boolean,
        changedTitle: Boolean,
        changedValue: Boolean,
        changedColor: Boolean,
        changedSteps: Boolean,
        durationSec: Long,
    ) {
        eventLogger.log(
            AnalyticsEvents.counterEditClosed(
                saved = saved,
                changedTitle = changedTitle,
                changedValue = changedValue,
                changedColor = changedColor,
                changedSteps = changedSteps,
                durationSec = durationSec,
            )
        )
    }

    private suspend fun logScreenView(
        destination: AnalyticsDestination,
        source: String,
    ) {
        eventLogger.log(
            AnalyticsEvents.screenView(
                destination = destination,
                source = source,
                countersCount = analyticsDao.getCountersCount(),
                categoriesCount = analyticsDao.getCategoriesCount(),
            )
        )
        screenStartedAtMs = SystemClock.elapsedRealtime()
    }

    private fun closeCurrentScreen() {
        val destination = currentDestination ?: return
        val startedAtMs = screenStartedAtMs ?: return
        eventLogger.log(
            AnalyticsEvents.screenClosed(
                destination = destination,
                durationSec = TimeUnit.MILLISECONDS.toSeconds(
                    (SystemClock.elapsedRealtime() - startedAtMs).coerceAtLeast(0L)
                ),
            )
        )
        screenStartedAtMs = null
    }

    private companion object {
        const val APP_START_SOURCE = "app_start"
    }
}
