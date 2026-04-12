package com.atarusov.justcounter.shared_features.hints._di

import android.content.Context
import com.atarusov.justcounter.shared_features.hints.data.HintsRepository
import com.atarusov.justcounter.shared_features.hints.data.HintsRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class HintsModule {

    @Singleton
    @Provides
    fun provideHintsRepository(@ApplicationContext context: Context): HintsRepository =
        HintsRepositoryImpl(context)

}
