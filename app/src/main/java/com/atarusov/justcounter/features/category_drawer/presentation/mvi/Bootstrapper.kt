package com.atarusov.justcounter.features.category_drawer.presentation.mvi

import com.atarusov.justcounter.features.category_drawer.data.CategoryRepository
import com.atarusov.justcounter.features.category_drawer.presentation.mvi.entities.InternalAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class Bootstrapper @Inject constructor(
    val repository: CategoryRepository
) {
    fun bootstrap(): Flow<InternalAction> = flow {
        repository.getCategoriesFlow().collect {
            emit(InternalAction.LoadCategories(it))
        }
    }
}