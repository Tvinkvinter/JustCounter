package com.atarusov.justcounter.features.counter_list_screen._di

import com.atarusov.justcounter.features.counter_list_screen.data.CounterListRepository
import com.atarusov.justcounter.features.counter_list_screen.data.CounterListRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class CounterRepositoryModule {

    @Binds
    abstract fun bindCounterListRepository(repository: CounterListRepositoryImpl): CounterListRepository
}