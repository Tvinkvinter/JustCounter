package com.atarusov.justcounter.features.edit_dialog.data

import androidx.datastore.core.DataStore
import com.atarusov.justcounter.CounterListProto
import com.atarusov.justcounter.CounterProto
import javax.inject.Inject

class EditCounterDataSource @Inject constructor(
    val dataStore: DataStore<CounterListProto>
) {

    suspend fun setCounter(counter: CounterProto) {
        dataStore.updateData { counters ->
            val index = counters.countersList.indexOfFirst { it.id == counter.id }
            if (index == -1) return@updateData counters

            counters.toBuilder()
                .setCounters(index, counter)
                .build()
        }
    }
}