package com.atarusov.justcounter.features.counter_full_screen.data.model

import androidx.room.Embedded
import com.atarusov.justcounter.common.Counter

data class CounterWithCategoryName(
    @Embedded val counter: Counter,
    val categoryName: String?
)