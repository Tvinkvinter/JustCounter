package com.atarusov.justcounter.common

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.random.Random

@Entity(tableName = "categories")
data class Category(
    val name: String,
    val position: Int = UNDEFINED_POSITION,
    @PrimaryKey(autoGenerate = true) val id: Int = 0
) {
    companion object {
        const val UNDEFINED_POSITION = -1

        fun getPreviewCategory() = Category(
            name = "PreviewCategory",
            id = Random.nextInt()
        )
    }
}
