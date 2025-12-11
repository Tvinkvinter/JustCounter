package com.atarusov.justcounter.features.category_drawer._di

import com.atarusov.justcounter.common.AppDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@Module
@InstallIn(SingletonComponent::class)
object CategoryDrawerModule {

    @Provides
    fun provideCategoriesDao(db: AppDatabase) = db.categoriesDao()

}