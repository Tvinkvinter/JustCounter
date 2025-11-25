package com.atarusov.justcounter.features.edit_dialog.presentation.mvi.entities

sealed class OneTimeEvent {
    data object ClearFocus : OneTimeEvent()
    data object ShowEmptyTitleTip : OneTimeEvent()
    data object CloseEditDialog : OneTimeEvent()
}