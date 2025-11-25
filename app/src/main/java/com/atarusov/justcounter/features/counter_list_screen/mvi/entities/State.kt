package com.atarusov.justcounter.features.counter_list_screen.mvi.entities

import com.atarusov.justcounter.domain.Counter
import com.atarusov.justcounter.ui.theme.CounterColorProvider
import kotlin.uuid.ExperimentalUuidApi

data class State(
    val removeMode: Boolean = false,
    val counters: List<Counter> = listOf(),
) {
    companion object {
        fun getPreviewState(removeMode: Boolean) = State(
            removeMode = removeMode,
            counters = List(4) { getPreviewCounter() } + List(4) { getPreviewCounter(true) }
        )
    }
}

@OptIn(ExperimentalUuidApi::class)
fun getPreviewCounter(withCustomSteps: Boolean = false) = Counter (
    title = "Tvinkvinter",
    value = 128,
    color = CounterColorProvider.getRandomColor(),
    steps = if (withCustomSteps) listOf(1, 2, 300) else listOf(1),
)