package com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.TextFieldValue
import com.atarusov.justcounter.features.counters_screen.presentation.ui.edit_counter_dialog.EditDialogState

sealed class Action {
    data object GetAllCounters : Action()
    data object AddCounter : Action()
    data class RemoveCounter(val counterId: String) : Action()

    data class ChangeColor(val counterId: String, val newColor: Color) : Action()
    data class MinusClick(val counterId: String, val step: Int) : Action()
    data class PlusClick(val counterId: String, val step: Int) : Action()
    data object RemoveStep: Action()
    data object AddStep : Action()
    data class TitleInput(val counterId: String, val inputTextField: TextFieldValue) : Action()
    data class TitleInputDone(val counterId: String, val input: String) : Action()
    data class ValueInput(val counterId: String, val inputTextField: TextFieldValue) : Action()
    data class ValueInputDone(val counterId: String, val input: String) : Action()
    data class StepInput(val counterId: String, val stepIndex: Int, val input: TextFieldValue) : Action()
    data object StepInputDone : Action()

    data object SwitchRemoveMode : Action()
    data class OpenCounterEditDialog(val counterId: String) : Action()
    data class CloseCounterEditDialog(
        val editDialogState: EditDialogState,
        val restoreInitialItemState: Boolean = false
    ) : Action()
}