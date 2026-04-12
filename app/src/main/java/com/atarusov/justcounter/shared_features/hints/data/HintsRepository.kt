package com.atarusov.justcounter.shared_features.hints.data

import kotlinx.coroutines.flow.Flow

interface HintsRepository {
    fun getHintsState(): Flow<HintsState>
    suspend fun dismissEditDeleteHint()
    suspend fun dismissMoveHint()
}