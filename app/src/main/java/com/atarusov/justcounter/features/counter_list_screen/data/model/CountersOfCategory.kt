package com.atarusov.justcounter.features.counter_list_screen.data.model

import com.atarusov.justcounter.common.Counter

data class CountersOfCategory (
    val category: CategoryName?,
    val counters: List<Counter>
)