package com.atarusov.justcounter.features.counters_screen.presentation.mvi.entities

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.atarusov.justcounter.features.counters_screen.domain.Counter
import com.atarusov.justcounter.features.counters_screen.presentation.ui.edit_counter_dialog.EditDialogState
import com.atarusov.justcounter.ui.theme.CounterColor
import com.atarusov.justcounter.ui.theme.CounterColorProvider

data class State(
    val removeMode: Boolean = false,
    val counterItems: List<CounterItem> = listOf(),
    val editDialog: EditDialogState? = null
)

data class CounterItem(
    val titleField: TextFieldValue,
    val valueField: TextFieldValue,
    val color: CounterColor,
    val steps: List<Int>,
    val counterId: String
) {
    companion object {
        fun getPreviewCounterItem(withCustomSteps: Boolean = false) = CounterItem (
            titleField = TextFieldValue("Tvinkvinter"),
            valueField = TextFieldValue("128"),
            color = CounterColorProvider.getRandomColor(),
            steps = if (withCustomSteps) listOf(1, 2, 300) else listOf(1),
            ""
        )
    }
}

fun Counter.toCounterItem() = CounterItem(
    titleField = TextFieldValue(title, TextRange(title.length)),
    valueField = TextFieldValue(value.toString(), TextRange(value.toString().length)),
    color = color,
    steps = steps,
    counterId = id
)

fun CounterItem.toCounter() = Counter(
    id = counterId,
    title = titleField.text,
    value = valueField.text.toIntOrNull() ?: 0,
    color = color,
    steps = steps
)