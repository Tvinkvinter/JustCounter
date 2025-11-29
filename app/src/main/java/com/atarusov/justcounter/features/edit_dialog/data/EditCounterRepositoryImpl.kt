package com.atarusov.justcounter.features.edit_dialog.data

import com.atarusov.justcounter.common.Counter
import javax.inject.Inject

class EditCounterRepositoryImpl @Inject constructor(
    val editCounterDao: EditCounterDao
): EditCounterRepository {

    override suspend fun setCounter(counter: Counter) {
        editCounterDao.setCounter(counter)
    }
}