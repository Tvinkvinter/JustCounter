package com.atarusov.justcounter.features.counter_list_screen.presentation.mvi.entities

import com.atarusov.justcounter.common.Counter
import com.atarusov.justcounter.common.Counter.Companion.getPreviewCounter

data class State(
    val removeMode: Boolean = false,
    val categoryId: Int? = null,
    val counters: List<Counter> = listOf(),
) {
    companion object {
        fun getPreviewState(removeMode: Boolean) = State(
            removeMode = removeMode,
            categoryId = null,
            counters = List(4) { getPreviewCounter() } + List(4) { getPreviewCounter(true) }
        )
    }
}