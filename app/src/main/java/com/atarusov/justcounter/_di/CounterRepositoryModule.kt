package com.atarusov.justcounter._di

import com.atarusov.justcounter.data.CounterListDataSource
import com.atarusov.justcounter.domain.CounterListRepository
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