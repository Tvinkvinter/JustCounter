package com.atarusov.justcounter.features.counter_full_screen.presentation.mvi

import com.atarusov.justcounter.features.counter_full_screen.data.CounterFullScreenRepository
import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities.InternalAction
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class Bootstrapper @Inject constructor(
    val repository: CounterFullScreenRepository,
) {
    fun bootstrap(counterId: String): Flow<InternalAction> = flow {
        repository.getCounterFlowById(counterId).collect {
            if (it == null) return@collect
            emit(InternalAction.LoadCounter(it))
        }
    }
}