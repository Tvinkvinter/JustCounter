package com.atarusov.justcounter.features.counter_list_screen.data.model

import androidx.room.Embedded
import com.atarusov.justcounter.common.Counter

data class CounterWithCategory(
    @Embedded(prefix = "category_") val category: CategoryName?,
    @Embedded(prefix = "counter_") val counter: Counter
)
