package com.atarusov.justcounter.features.edit_dialog.presentation.mvi.entities

import androidx.compose.ui.text.input.TextFieldValue
import com.atarusov.justcounter.ui.theme.CounterColor

sealed class Action {
    data class TitleInput(val inputField: TextFieldValue) : Action()
    data class TitleInputDone(val input: String) : Action()
    data class ValueInput(val inputField: TextFieldValue) : Action()
    data class ValueInputDone(val input: String) : Action()
    data class StepInput(val stepIndex: Int, val inputField: TextFieldValue) : Action()
    data object StepInputDone : Action()
    data object RemoveStep: Action()
    data object AddStep : Action()
    data class ChangeColor(val newColor: CounterColor) : Action()
    data class CloseCounterEditDialog(val state: State, val saveChanges: Boolean = false) : Action()
}