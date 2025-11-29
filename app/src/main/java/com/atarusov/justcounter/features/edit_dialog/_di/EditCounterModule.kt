package com.atarusov.justcounter.features.edit_dialog._di

import com.atarusov.justcounter.common.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent


@Module
@InstallIn(SingletonComponent::class)
object EditCounterModule {

    @Provides
    fun provideEditCounterDao(db: AppDatabase) = db.editCounter()
}