package com.atarusov.justcounter.features.counter_list_screen._di

import android.content.Context
import com.atarusov.justcounter.R
import com.atarusov.justcounter.common.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CounterListModule {

    @Provides
    fun provideCounterListDao(db: AppDatabase) = db.counterListDao()

    @Provides
    fun provideDefaultCounterTitles(@ApplicationContext context: Context): List<String> {
        return context.resources.getStringArray(R.array.default_counter_names).toList()
    }
}