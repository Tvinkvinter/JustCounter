package com.atarusov.justcounter.features.category_drawer.presentation.mvi.entities

import com.atarusov.justcounter.common.Category

sealed class InternalAction {
    data class LoadCategories(val categories: List<Category>) : InternalAction()
    data class SelectCategory(val categoryId: Int?) : InternalAction()
    data object ScrollCategoryListDown : InternalAction()
}