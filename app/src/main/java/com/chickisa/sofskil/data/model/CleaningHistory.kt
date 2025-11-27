package com.chickisa.sofskil.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "cleaning_history",
    foreignKeys = [
        ForeignKey(
            entity = Zone::class,
            parentColumns = ["id"],
            childColumns = ["zoneId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("zoneId")]
)
data class CleaningHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val zoneId: Long,
    val cleaningTimestamp: Long,
    val notes: String = ""
)

