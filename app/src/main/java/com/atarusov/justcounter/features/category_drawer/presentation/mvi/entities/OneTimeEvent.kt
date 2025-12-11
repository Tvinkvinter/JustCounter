package com.atarusov.justcounter.features.category_drawer.presentation.mvi.entities

sealed class OneTimeEvent {
    data class SelectCategory(val categoryId: Int?) : OneTimeEvent()
    data object ScrollCategoryListDown : OneTimeEvent()
}