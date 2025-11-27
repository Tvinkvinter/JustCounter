package com.atarusov.justcounter.features.edit_dialog.presentation.mvi.entities

import androidx.compose.ui.text.input.TextFieldValue
import com.atarusov.justcounter.common.Counter
import com.atarusov.justcounter.ui.theme.CounterColor

sealed class InternalAction {
    data object ClearFocus : InternalAction()
    data object CloseEditCounterDialog : InternalAction()
    data object ShowEmptyTitleTip : InternalAction()

    data class LoadCounterItem(val counter: Counter) : InternalAction()
    data class UpdateCounterItemTitleField(val newTextField: TextFieldValue) : InternalAction()
    data class UpdateCounterItemValueField(val newTextField: TextFieldValue) : InternalAction()
    data class UpdateStepConfiguratorField(val stepIndex: Int, val newTextField: TextFieldValue) : InternalAction()
    data object RemoveLastStepField : InternalAction()
    data object AddStepField : InternalAction()
    data class UpdateCounterItemColor(val newColor: CounterColor) : InternalAction()
}