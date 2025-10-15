package com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities

import androidx.compose.ui.graphics.Color
import com.atarusov.justcounter.features.counters_screen.domain.Counter
import com.atarusov.justcounter.features.counters_screen.presentation.ui.edit_counter_dialog.EditDialogState

data class State(
    val removeMode: Boolean = false,
    val counterItems: List<CounterItem> = listOf(),
    val editDialog: EditDialogState? = null
)

data class CounterItem(
    val titleField: String,
    val valueField: String,
    val color: Color,
    val counterId: String
) {
    constructor(counter: Counter) : this(
        titleField = counter.title,
        valueField = counter.value.toString(),
        color = counter.color,
        counterId = counter.id
    )
}