package com.atarusov.justcounter.features.edit_dialog.mvi

import com.atarusov.justcounter.domain.CounterListRepository
import com.atarusov.justcounter.features.edit_dialog.mvi.entities.InternalAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class Bootstrapper @Inject constructor(val repository: CounterListRepository) {
    fun bootstrap(counterId: String): Flow<InternalAction> = flow {
        val counter = repository.getCounterById(counterId)
        emit(InternalAction.LoadCounterItem(counter))
    }
}