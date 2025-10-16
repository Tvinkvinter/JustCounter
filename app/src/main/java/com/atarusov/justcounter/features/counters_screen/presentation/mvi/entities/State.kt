package com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.atarusov.justcounter.features.counters_screen.domain.Counter
import com.atarusov.justcounter.features.counters_screen.presentation.ui.edit_counter_dialog.EditDialogState

data class State(
    val removeMode: Boolean = false,
    val counterItems: List<CounterItem> = listOf(),
    val editDialog: EditDialogState? = null
)

data class CounterItem(
    val titleField: TextFieldValue,
    val valueField: TextFieldValue,
    val color: Color,
    val counterId: String
)

fun Counter.toCounterItem() = CounterItem(
    titleField = TextFieldValue(title, TextRange(title.length)),
    valueField = TextFieldValue(value.toString(), TextRange(value.toString().length)),
    color = color,
    counterId = id
)