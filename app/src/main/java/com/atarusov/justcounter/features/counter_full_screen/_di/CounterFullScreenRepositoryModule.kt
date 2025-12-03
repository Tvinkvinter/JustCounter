package com.atarusov.justcounter.features.counter_full_screen._di

import com.atarusov.justcounter.features.counter_full_screen.data.CounterFullScreenRepository
import com.atarusov.justcounter.features.counter_full_screen.data.CounterFullScreenRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CounterFullScreenRepositoryModule {

    @Binds
    abstract fun bindCounterFullRepository(repository: CounterFullScreenRepositoryImpl): CounterFullScreenRepository
}