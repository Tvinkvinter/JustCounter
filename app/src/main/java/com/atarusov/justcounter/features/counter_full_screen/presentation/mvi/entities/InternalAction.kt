package com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities

import com.atarusov.justcounter.common.Counter
import com.atarusov.justcounter.features.counter_full_screen.data.model.CounterWithCategoryName

sealed class InternalAction {
    data class LoadData(val data: CounterWithCategoryName) : InternalAction()
    data class UpdateCounterValue(val counterId: String, val newValue: Int) : InternalAction()

    data object SwitchRemoveMode : InternalAction()
    data object NavigateBack : InternalAction()
    data class OpenEditCounterDialog(val counter: Counter) : InternalAction()
}