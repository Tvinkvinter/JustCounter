package com.atarusov.justcounter.features.category_drawer.presentation.mvi

import com.atarusov.justcounter.features.category_drawer.presentation.mvi.entities.InternalAction
import com.atarusov.justcounter.features.category_drawer.presentation.mvi.entities.State
import javax.inject.Inject

class Reducer @Inject constructor() {
    fun reduce(previousState: State, internalAction: InternalAction): State =
        when (internalAction) {
            is InternalAction.LoadCategories -> previousState.copy(categories = internalAction.categories)
            is InternalAction.SelectCategory ->
                previousState.copy(selectedCategoryId = internalAction.categoryId)
            is InternalAction.SwapCategories -> swapCategories(
                previousState,
                internalAction.fromIndex,
                internalAction.toIndex
            )


            InternalAction.ScrollCategoryListDown -> previousState
        }

    private fun swapCategories(previousState: State, firstIndex: Int, secondIndex: Int): State {
        val newCategoryList = previousState.categories.toMutableList()

        val firstCategory = newCategoryList[firstIndex]
        val secondCategory = newCategoryList[secondIndex]

        newCategoryList[secondIndex] = firstCategory
        newCategoryList[firstIndex] = secondCategory

        return previousState.copy(categories = newCategoryList)
    }
}