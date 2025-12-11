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

            InternalAction.ScrollCategoryListDown -> previousState
        }
}