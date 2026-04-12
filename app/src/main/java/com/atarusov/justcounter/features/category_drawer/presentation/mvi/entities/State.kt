package com.atarusov.justcounter.features.category_drawer.presentation.mvi.entities

import com.atarusov.justcounter.common.Category

data class State(
    val selectedCategoryId: Int?,
    val categories: List<Category> = listOf(),
    val editDeleteHintDismissed: Boolean = false,
    val moveHintDismissed: Boolean = false,
) {
    companion object {
        fun getPreviewState() = State(
            selectedCategoryId = null,
            categories = listOf(Category("preview")) + List(4) { Category.getPreviewCategory() }
        )
    }
}