package com.chickisa.sofskil.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "reminders",
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
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val zoneId: Long,
    val reminderTimeHour: Int = 8,
    val reminderTimeMinute: Int = 0,
    val isEnabled: Boolean = true,
    val daysBefore: Int = 0
)

