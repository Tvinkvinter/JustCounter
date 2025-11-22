package com.atarusov.justcounter.features.counters_screen.mvi.entities

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.atarusov.justcounter.domain.Counter
import com.atarusov.justcounter.ui.theme.CounterColor
import com.atarusov.justcounter.ui.theme.CounterColorProvider
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

data class State(
    val removeMode: Boolean = false,
    val counterItems: List<CounterItem> = listOf(),
) {
    companion object {
        fun getPreviewState(removeMode: Boolean) = State(
            removeMode = removeMode,
            counterItems = List(12) { CounterItem.getPreviewCounterItem() }
        )
    }
}

data class CounterItem(
    val titleField: TextFieldValue,
    val valueField: TextFieldValue,
    val color: CounterColor,
    val steps: List<Int>,
    val counterId: String
) {
    companion object {
        @OptIn(ExperimentalUuidApi::class)
        fun getPreviewCounterItem(withCustomSteps: Boolean = false) = CounterItem (
            titleField = TextFieldValue("Tvinkvinter"),
            valueField = TextFieldValue("128"),
            color = CounterColorProvider.getRandomColor(),
            steps = if (withCustomSteps) listOf(1, 2, 300) else listOf(1),
            counterId = Uuid.random().toString()
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