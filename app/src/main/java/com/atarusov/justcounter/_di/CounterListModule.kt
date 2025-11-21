package com.atarusov.justcounter._di

import android.content.Context
import com.atarusov.justcounter.R
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CounterListModule {

    @Provides
    fun provideDefaultCounterTitles(@ApplicationContext context: Context): List<String> {
        return context.resources.getStringArray(R.array.default_counter_names).toList()
    }
}