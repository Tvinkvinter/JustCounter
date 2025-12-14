package com.atarusov.justcounter.features.category_drawer.presentation.mvi.entities

sealed class Action {
    data class SelectCategory(val categoryId: Int?): Action()
    data class AddCategory(val name: String) : Action()
    data class RenameCategory(val categoryId: Int, val newName: String) : Action()
    data class RemoveCategory(val categoryId: Int, val isSelected: Boolean) : Action()
    data class SwapCategories(val firstIndex: Int, val secondIndex: Int) : Action()
}