package com.atarusov.justcounter

import android.app.Application
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import com.atarusov.justcounter.shared_features.analytics.AnalyticsTracker
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class App : Application() {

    @Inject
    lateinit var analyticsTracker: AnalyticsTracker

    private val analyticsScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()

        ProcessLifecycleOwner.get().lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onStart(owner: LifecycleOwner) {
                    analyticsScope.launch { analyticsTracker.onAppForegrounded() }
                }

                override fun onStop(owner: LifecycleOwner) {
                    analyticsScope.launch { analyticsTracker.onAppBackgrounded() }
                }
            }
        )
    }
}
