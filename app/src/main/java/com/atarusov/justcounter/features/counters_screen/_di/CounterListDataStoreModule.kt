package com.atarusov.justcounter.features.counters_screen._di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.core.DataStoreFactory
import androidx.datastore.dataStoreFile
import com.atarusov.justcounter.CounterListProto
import com.atarusov.justcounter.features.counters_screen.data.CounterListProtoSerializer
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import jakarta.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object CounterListDataStoreModule {

    @Provides
    @Singleton
    fun provideCounterListDataStore(
        @ApplicationContext context: Context
    ): DataStore<CounterListProto> = DataStoreFactory.create(
        serializer = CounterListProtoSerializer,
        produceFile = { context.dataStoreFile("counterList.pb") }
    )
}