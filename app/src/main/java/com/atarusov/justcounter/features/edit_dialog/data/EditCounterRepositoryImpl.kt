package com.atarusov.justcounter.features.edit_dialog.data

import com.atarusov.justcounter.common.Counter
import com.atarusov.justcounter.common.toProto
import javax.inject.Inject

class EditCounterRepositoryImpl @Inject constructor(
    val dataSource: EditCounterDataSource
): EditCounterRepository {

    override suspend fun setCounter(counter: Counter) {
        dataSource.setCounter(counter.toProto())
    }
}