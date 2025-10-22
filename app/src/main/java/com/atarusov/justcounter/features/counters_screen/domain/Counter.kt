package com.atarusov.justcounter.features.counters_screen.domain

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import com.atarusov.justcounter.CounterProto
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalUuidApi::class)
data class Counter(
    val title: String,
    val value: Int,
    val color: Color,
    val steps: List<Int>,
    val id: String = Uuid.random().toString()
)

fun CounterProto.toDomain() = Counter(
    title = this.title,
    value = this.value,
    color = Color(this.color),
    steps = this.stepsList,
    id = this.id
)

fun Counter.toProto(): CounterProto = CounterProto.newBuilder()
    .setTitle(this.title)
    .setValue(this.value)
    .setColor(this.color.toArgb())
    .addAllSteps(this.steps)
    .setId(this.id)
    .build()