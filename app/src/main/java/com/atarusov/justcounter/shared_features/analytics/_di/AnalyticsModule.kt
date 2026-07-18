package com.atarusov.justcounter.shared_features.analytics._di

import android.content.Context
import com.atarusov.justcounter.common.AppDatabase
import com.atarusov.justcounter.shared_features.analytics.AnalyticsEventLogger
import com.atarusov.justcounter.shared_features.analytics.FirebaseAnalyticsEventLogger
import com.google.firebase.analytics.FirebaseAnalytics
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AnalyticsModule {
    @Provides
    fun provideAnalyticsDao(database: AppDatabase) = database.analyticsDao()

    @Provides
    @Singleton
    fun provideFirebaseAnalytics(@ApplicationContext context: Context): FirebaseAnalytics =
        FirebaseAnalytics.getInstance(context)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class AnalyticsBindingsModule {
    @Binds
    internal abstract fun bindAnalyticsEventLogger(
        logger: FirebaseAnalyticsEventLogger,
    ): AnalyticsEventLogger
}
