package com.atarusov.justcounter.features.category_drawer.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.atarusov.justcounter.common.Category
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Query("SELECT * FROM categories ORDER BY position")
    fun getCategories(): Flow<List<Category>>

    @Transaction
    suspend fun addCategory(category: Category) {
        if (category.position != Category.UNDEFINED_POSITION)
            throw IllegalStateException("Do not pass position parameter when adding Category")
        val categoryWithPosition = category.copy(
            position = getMaxCategoryPosition()?.let { it + 1 } ?: 0
        )
        addCategoryRow(categoryWithPosition)
    }

    @Query("SELECT MAX(position) FROM categories")
    suspend fun getMaxCategoryPosition(): Int?

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun addCategoryRow(category: Category)

    @Query("UPDATE categories SET name = :newName WHERE id = :id")
    suspend fun renameCategoryById(id: Int, newName: String)

    @Transaction
    suspend fun deleteCategoryById(id: Int) {
        val categoryPosition = getCategoryPositionById(id)
        deleteRowById(id)
        shiftPositions(categoryPosition)
    }

    @Query("SELECT position FROM categories WHERE id = :id")
    suspend fun getCategoryPositionById(id: Int): Int

    @Query("DELETE FROM categories WHERE id = :id")
    suspend fun deleteRowById(id: Int)

    @Query("UPDATE categories SET position = position - 1 WHERE position > :deletedPosition")
    suspend fun shiftPositions(deletedPosition: Int)
}