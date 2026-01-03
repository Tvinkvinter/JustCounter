package com.atarusov.justcounter.features.counter_full_screen.presentation.mvi

import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities.InternalAction
import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities.InternalAction.LoadData
import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities.InternalAction.NavigateBack
import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities.InternalAction.OpenEditCounterDialog
import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities.InternalAction.SwitchRemoveMode
import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities.InternalAction.UpdateCounterValue
import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities.State
import com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities.State.Loaded
import javax.inject.Inject

class Reducer @Inject constructor() {
    fun reduce(previousState: State, internalAction: InternalAction): State =
        when (internalAction) {
            is LoadData -> {
                if (internalAction.data == null) State.ItemRemoved
                else when (previousState) {
                    is State.Loading -> Loaded(
                        removeMode = false,
                        categoryName = internalAction.data.categoryName,
                        counter = internalAction.data.counter
                    )

                    is State.Loaded -> previousState.copy(
                        categoryName = internalAction.data.categoryName,
                        counter = internalAction.data.counter
                    )

                    is State.ItemRemoved -> previousState
                }
            }

            is UpdateCounterValue -> when (previousState) {
                is State.Loading -> previousState
                is State.Loaded -> previousState.copy(
                    counter = previousState.counter.copy(value = internalAction.newValue)
                )
                is State.ItemRemoved -> previousState
            }

            SwitchRemoveMode -> when (previousState) {
                is State.Loading -> previousState
                is State.Loaded -> previousState.copy(removeMode = !previousState.removeMode)
                is State.ItemRemoved -> previousState
            }

            NavigateBack,
            is OpenEditCounterDialog -> previousState
        }
}