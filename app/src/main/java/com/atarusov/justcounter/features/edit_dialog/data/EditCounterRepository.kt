package com.atarusov.justcounter.features.edit_dialog.data

import com.atarusov.justcounter.common.Counter

interface EditCounterRepository {
    suspend fun setCounter(counter: Counter)
}