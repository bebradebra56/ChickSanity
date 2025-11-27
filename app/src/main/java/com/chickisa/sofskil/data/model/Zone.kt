package com.chickisa.sofskil.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "zones")
data class Zone(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val category: ZoneCategory,
    val lastCleaningTimestamp: Long,
    val cleaningFrequencyDays: Int,
    val notes: String = "",
    val isActive: Boolean = true
) {
    fun getNextCleaningTimestamp(): Long {
        return lastCleaningTimestamp + (cleaningFrequencyDays * 24 * 60 * 60 * 1000)
    }
    
    fun getDaysUntilNextCleaning(): Int {
        val now = System.currentTimeMillis()
        val daysLeft = ((getNextCleaningTimestamp() - now) / (24 * 60 * 60 * 1000)).toInt()
        return daysLeft
    }
    
    fun isClean(): Boolean {
        return getDaysUntilNextCleaning() >= 0
    }
    
    fun getDaysSinceLastCleaning(): Int {
        val now = System.currentTimeMillis()
        return ((now - lastCleaningTimestamp) / (24 * 60 * 60 * 1000)).toInt()
    }
}

enum class ZoneCategory(val displayName: String, val emoji: String) {
    COOP("Chicken Coop", "🐔"),
    BARN("Barn", "🪣"),
    SHED("Shed", "🌾"),
    EQUIPMENT("Equipment", "🔧"),
    TERRITORY("Territory", "🌿"),
    STORAGE("Storage", "📦")
}

