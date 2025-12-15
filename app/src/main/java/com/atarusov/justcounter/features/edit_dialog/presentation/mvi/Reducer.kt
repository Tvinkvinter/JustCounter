package com.atarusov.justcounter.features.edit_dialog.presentation.mvi

import androidx.compose.ui.text.input.TextFieldValue
import com.atarusov.justcounter.common.CounterColor
import com.atarusov.justcounter.features.edit_dialog.presentation.mvi.entities.InternalAction
import com.atarusov.justcounter.features.edit_dialog.presentation.mvi.entities.InternalAction.*
import com.atarusov.justcounter.features.edit_dialog.presentation.mvi.entities.State
import javax.inject.Inject

class Reducer @Inject constructor() {
    fun reduce(previousState: State, internalAction: InternalAction): State =
        when (internalAction) {
            is LoadCounterItem -> State(internalAction.counter)
            is UpdateCounterItemTitleField ->
                previousState.copy(titleField = internalAction.newTextField)

            is UpdateCounterItemValueField ->
                previousState.copy(valueField = internalAction.newTextField)

            is UpdateStepConfiguratorField -> updateStepConfiguratorField(
                previousState,
                internalAction.stepIndex,
                internalAction.newTextField
            )

            RemoveLastStepField -> removeLastStepField(previousState)
            AddStepField -> addStepField(previousState)
            is UpdateCounterItemColor -> updateCounterItemColor(previousState, internalAction.newColor)

            ClearFocus,
            ShowEmptyTitleTip,
            CloseEditCounterDialog -> previousState
    }

    private fun updateStepConfiguratorField(
        previousState: State,
        stepIndex: Int,
        newTextField: TextFieldValue
    ): State {
        val newSteps = previousState.stepConfiguratorState.steps.mapIndexed { index, fieldValue ->
            if (index == stepIndex) newTextField
            else fieldValue
        }
        val newStepConfiguratorState = previousState.stepConfiguratorState.copy(steps = newSteps)

        return previousState.copy(stepConfiguratorState = newStepConfiguratorState)
    }

    private fun removeLastStepField(previousState: State): State {
        if (previousState.stepConfiguratorState.steps.size <= 1) return previousState

        val newSteps = previousState.stepConfiguratorState.steps.toMutableList().apply {
            removeAt(lastIndex)
        }
        val newStepConfiguratorState = previousState.stepConfiguratorState.copy(steps = newSteps)

        return previousState.copy(stepConfiguratorState = newStepConfiguratorState)
    }

    private fun addStepField(previousState: State): State {
        val newSteps = previousState.stepConfiguratorState.steps.toMutableList().apply {
            add(TextFieldValue())
        }
        val newStepConfiguratorState = previousState.stepConfiguratorState.copy(steps = newSteps)

        return previousState.copy(stepConfiguratorState = newStepConfiguratorState)
    }

    private fun updateCounterItemColor(
        previousState: State,
        newColor: CounterColor
    ) = previousState.copy(
        color = newColor,
        stepConfiguratorState = previousState.stepConfiguratorState.copy(btnColor = newColor)
    )
}