package com.atarusov.justcounter.features.category_drawer.presentation.mvi.entities

import com.atarusov.justcounter.common.Category
import com.atarusov.justcounter.shared_features.hints.data.HintsState

sealed class InternalAction {
    data class LoadCategories(val categories: List<Category>) : InternalAction()
    data class SelectCategory(val categoryId: Int?) : InternalAction()
    data class SwapCategories(val fromIndex: Int, val toIndex: Int) : InternalAction()
    data object ScrollCategoryListDown : InternalAction()
    data class UpdateHintsState(val hintsState: HintsState) : InternalAction()
}