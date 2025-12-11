package com.atarusov.justcounter.features.category_drawer.presentation.mvi

import com.atarusov.justcounter.features.category_drawer.presentation.mvi.entities.InternalAction
import com.atarusov.justcounter.features.category_drawer.presentation.mvi.entities.OneTimeEvent
import javax.inject.Inject

class OneTimeEventHandler @Inject constructor() {
    fun handleEvent(internalAction: InternalAction): OneTimeEvent? {
        return when (internalAction) {
            is InternalAction.SelectCategory -> OneTimeEvent.SelectCategory(internalAction.categoryId)
            is InternalAction.ScrollCategoryListDown -> OneTimeEvent.ScrollCategoryListDown

            is InternalAction.LoadCategories -> null
        }
    }
}