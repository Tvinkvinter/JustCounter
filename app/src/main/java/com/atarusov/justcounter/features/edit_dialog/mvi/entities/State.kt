package com.atarusov.justcounter.features.edit_dialog.mvi.entities

import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.input.TextFieldValue
import com.atarusov.justcounter.domain.Counter
import com.atarusov.justcounter.ui.theme.CounterColor


data class State(
    val titleField: TextFieldValue,
    val valueField: TextFieldValue,
    val color: CounterColor,
    val stepConfiguratorState: StepConfiguratorState,
    val counterId: String,
) {
    constructor(counter: Counter) : this (
        titleField = TextFieldValue(counter.title),
        valueField = TextFieldValue(counter.value.toString()),
        color =  counter.color,
        stepConfiguratorState = StepConfiguratorState(counter),
        counterId =  counter.id,
    )

    constructor() : this(
        titleField = TextFieldValue(),
        valueField = TextFieldValue(),
        color = CounterColor.Blue,
        stepConfiguratorState = StepConfiguratorState(listOf(TextFieldValue("1")), CounterColor.Blue),
        counterId =  "",
    )

    companion object {
        fun getPreviewState() = State(
            titleField = TextFieldValue("Tvinkvinter"),
            valueField = TextFieldValue("128"),
            color = CounterColor.Blue,
            stepConfiguratorState = StepConfiguratorState(
                steps = listOf(TextFieldValue("1"), TextFieldValue("2"), TextFieldValue("128")),
                btnColor = CounterColor.Blue
            ),
            counterId = "",
        )
    }
}

data class StepConfiguratorState(
    val steps: List<TextFieldValue>,
    val btnColor: CounterColor
) {
    val removeBtnEnabled: Boolean get() = steps.size > 1
    val addBtnEnabled: Boolean get() = steps.size < 3

    constructor(counter: Counter) : this (
        steps = counter.steps.map { TextFieldValue(it.toString(), TextRange(it.toString().length)) },
        btnColor = counter.color
    )
}