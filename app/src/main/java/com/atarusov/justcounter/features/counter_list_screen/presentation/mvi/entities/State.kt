package com.atarusov.justcounter.features.counter_list_screen.presentation.mvi.entities

import com.atarusov.justcounter.common.Counter
import com.atarusov.justcounter.common.Counter.Companion.getPreviewCounter
import com.atarusov.justcounter.features.counter_list_screen.data.model.CategoryName

data class State(
    val removeMode: Boolean = false,
    val category: CategoryName? = null,
    val counters: List<Counter> = listOf(),
) {
    companion object {
        fun getPreviewState(removeMode: Boolean) = State(
            removeMode = removeMode,
            category = null,
            counters = List(4) { getPreviewCounter() } + List(4) { getPreviewCounter(true) }
        )
    }
}