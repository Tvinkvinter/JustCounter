package com.atarusov.justcounter.features.category_drawer.data

import com.atarusov.justcounter.common.Category
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    suspend fun getCategoriesFlow(): Flow<List<Category>>
    suspend fun addCategory(name: String)
    suspend fun renameCategory(categoryId: Int, newName: String)
    suspend fun removeCategory(categoryId: Int)
    suspend fun swapCategories(firstPosition: Int, secondPosition: Int)
}