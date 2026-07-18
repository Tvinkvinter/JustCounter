package com.atarusov.justcounter.shared_features.analytics

import android.os.Bundle
import com.google.firebase.analytics.FirebaseAnalytics
import javax.inject.Inject

internal interface AnalyticsEventLogger {
    fun log(event: AnalyticsEvent)
}

internal class FirebaseAnalyticsEventLogger @Inject constructor(
    private val firebaseAnalytics: FirebaseAnalytics,
) : AnalyticsEventLogger {

    override fun log(event: AnalyticsEvent) {
        val parameters = Bundle().apply {
            event.parameters.forEach { (name, value) ->
                when (value) {
                    is String -> putString(name, value)
                    is Long -> putLong(name, value)
                    is Double -> putDouble(name, value)
                    else -> error("Unsupported Analytics parameter type: ${value::class}")
                }
            }
        }

        firebaseAnalytics.logEvent(event.name, parameters)
    }
}
