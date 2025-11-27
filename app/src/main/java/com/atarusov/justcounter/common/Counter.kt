package com.atarusov.justcounter.common

import com.atarusov.justcounter.CounterProto
import com.atarusov.justcounter.ui.theme.CounterColor
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Counter(
    val title: String,
    val value: Int,
    val color: CounterColor,
    val steps: List<Int>,
    val id: String = Uuid.random().toString()
)

fun CounterProto.toCounter() = Counter(
    title = this.title,
    value = this.value,
    color = CounterColor.valueOf(this.color),
    steps = this.stepsList,
    id = this.id
)

fun Counter.toProto(): CounterProto = CounterProto.newBuilder()
    .setTitle(this.title)
    .setValue(this.value)
    .setColor(this.color.name)
    .addAllSteps(this.steps)
    .setId(this.id)
    .build()