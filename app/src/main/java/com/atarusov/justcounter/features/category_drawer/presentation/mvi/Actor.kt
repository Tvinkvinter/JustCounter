package com.atarusov.justcounter.features.category_drawer.presentation.mvi

import com.atarusov.justcounter.features.category_drawer.data.CategoryRepository
import com.atarusov.justcounter.features.category_drawer.presentation.mvi.entities.Action
import com.atarusov.justcounter.features.category_drawer.presentation.mvi.entities.Action.*
import com.atarusov.justcounter.features.category_drawer.presentation.mvi.entities.InternalAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class Actor @Inject constructor(
    val repository: CategoryRepository
) {
    fun handleAction(action: Action): Flow<InternalAction> {
        return when (action) {
            is SelectCategory -> flowOf(InternalAction.SelectCategory(action.categoryId))
            is AddCategory -> flow {
                repository.addCategory(action.name.trim())
                emit(InternalAction.ScrollCategoryListDown)
            }
            is RenameCategory -> flow {
                repository.renameCategory(action.categoryId, action.newName.trim())
            }
            is RemoveCategory -> flow {
                repository.removeCategory(action.categoryId)
                if (action.isSelected) emit(InternalAction.SelectCategory(null))
            }
            is SwapCategories -> flow {
                emit(InternalAction.SwapCategories(action.firstIndex, action.secondIndex))
                repository.swapCategories(action.firstIndex, action.secondIndex)
            }
        }
    }

}