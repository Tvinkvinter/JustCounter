package com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities

import com.atarusov.justcounter.common.Counter
import com.atarusov.justcounter.common.Counter.Companion.getPreviewCounter

sealed class State {
    data object Loading : State()
    data class Loaded(
        val removeMode: Boolean = false,
        val categoryName: String?,
        val counter: Counter
    ) : State()
    data object ItemRemoved : State()

    companion object {
        fun getPreviewState(removeMode: Boolean) = Loaded(
            removeMode = removeMode,
            categoryName = "PreviewCategory",
            counter = getPreviewCounter()
        )
    }
}