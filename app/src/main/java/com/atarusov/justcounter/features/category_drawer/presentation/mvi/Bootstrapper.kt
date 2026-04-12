package com.atarusov.justcounter.features.category_drawer.presentation.mvi

import com.atarusov.justcounter.shared_features.hints.data.HintsRepository
import com.atarusov.justcounter.features.category_drawer.data.CategoryRepository
import com.atarusov.justcounter.features.category_drawer.presentation.mvi.entities.InternalAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.merge
import javax.inject.Inject

class Bootstrapper @Inject constructor(
    val repository: CategoryRepository,
    val hintsRepository: HintsRepository,
) {
    fun bootstrap(): Flow<InternalAction> = merge(
        repository.getCategoriesFlow().map { InternalAction.LoadCategories(it) },
        hintsRepository.getHintsState().map { InternalAction.UpdateHintsState(it) },
    )
}