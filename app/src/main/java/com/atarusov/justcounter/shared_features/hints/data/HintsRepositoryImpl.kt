package com.atarusov.justcounter.shared_features.hints.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class HintsRepositoryImpl @Inject constructor(
    private val context: Context
) : HintsRepository {

    private object Keys {
        val EDIT_DELETE_DISMISSED = booleanPreferencesKey("edit_delete_hint_dismissed")
        val MOVE_DISMISSED = booleanPreferencesKey("move_hint_dismissed")
    }

    override fun getHintsState(): Flow<HintsState> =
        context.hintsDataStore.data.map { prefs ->
            HintsState(
                editDeleteHintDismissed = prefs[Keys.EDIT_DELETE_DISMISSED] ?: false,
                moveHintDismissed = prefs[Keys.MOVE_DISMISSED] ?: false,
            )
        }

    override suspend fun dismissEditDeleteHint() {
        context.hintsDataStore.edit { it[Keys.EDIT_DELETE_DISMISSED] = true }
    }

    override suspend fun dismissMoveHint() {
        context.hintsDataStore.edit { it[Keys.MOVE_DISMISSED] = true }
    }
}

private val Context.hintsDataStore: DataStore<Preferences> by preferencesDataStore(name = "hints_prefs")
