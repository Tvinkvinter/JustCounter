package com.atarusov.justcounter.features.edit_dialog._di

import com.atarusov.justcounter.features.edit_dialog.data.EditCounterRepository
import com.atarusov.justcounter.features.edit_dialog.data.EditCounterRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
abstract class EditCounterRepositoryModule {

    @Binds
    abstract fun bindEditCounterRepository(repository: EditCounterRepositoryImpl): EditCounterRepository
}