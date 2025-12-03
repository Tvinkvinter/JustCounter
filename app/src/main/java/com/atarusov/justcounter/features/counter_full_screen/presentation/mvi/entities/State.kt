package com.atarusov.justcounter.features.counter_full_screen.presentation.mvi.entities

import com.atarusov.justcounter.common.Counter
import com.atarusov.justcounter.common.Counter.Companion.getPreviewCounter

data class State(
    val removeMode: Boolean = false,
    val counter: Counter
) {
    companion object {
        fun getPreviewState(removeMode: Boolean) = State(
            removeMode = removeMode,
            counter = getPreviewCounter()
        )
    }
}