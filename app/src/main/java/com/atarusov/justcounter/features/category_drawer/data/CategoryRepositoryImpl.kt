package com.atarusov.justcounter.features.category_drawer.data

import com.atarusov.justcounter.common.Category
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class CategoryRepositoryImpl @Inject constructor(
    val categoryDao: CategoryDao
) : CategoryRepository {

    override fun getCategoriesFlow(): Flow<List<Category>> = categoryDao.getCategories()
    override suspend fun addCategory(name: String) = categoryDao.addCategory(Category(name))
    override suspend fun renameCategory(categoryId: Int, newName: String) =
        categoryDao.renameCategoryById(categoryId, newName)

    override suspend fun removeCategory(categoryId: Int) = categoryDao.deleteCategoryById(categoryId)
    override suspend fun swapCategories(firstPosition: Int, secondPosition: Int) =
        categoryDao.swapCategoriesOnPositions(firstPosition, secondPosition)
}