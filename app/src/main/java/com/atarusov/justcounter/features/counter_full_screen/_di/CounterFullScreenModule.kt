package com.atarusov.justcounter.features.counter_full_screen._di

import com.atarusov.justcounter.common.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object CounterFullScreenModule {

    @Provides
    fun provideCounterFullScreenDao(db: AppDatabase) = db.counterFullScreenDao()
}