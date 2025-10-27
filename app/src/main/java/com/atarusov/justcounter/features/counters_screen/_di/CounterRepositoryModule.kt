package com.atarusov.justcounter.features.counters_screen._di

import com.atarusov.justcounter.features.counters_screen.data.CounterListDataSource
import com.atarusov.justcounter.features.counters_screen.domain.CounterListRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CounterRepositoryModule {

    @Binds
    abstract fun bindCounterListRepository(dataSource: CounterListDataSource): CounterListRepository
}