package com.atarusov.justcounter.features.edit_dialog.data

import com.atarusov.justcounter.domain.Counter

interface EditCounterRepository {
    suspend fun setCounter(counter: Counter)
}