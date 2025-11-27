package com.chickisa.sofskil.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "achievements")
data class Achievement(
    @PrimaryKey
    val id: String,
    val isUnlocked: Boolean = false,
    val unlockedTimestamp: Long? = null,
    val progress: Int = 0
)

enum class AchievementType(
    val id: String,
    val title: String,
    val description: String,
    val emoji: String,
    val requiredProgress: Int
) {
    SHINING_FARM("shining_farm", "Shining Farm", "All zones clean for 7 days in a row", "✨", 7),
    DECENT_FARMER("decent_farmer", "Decent Farmer", "Complete 50 cleanings", "🏆", 50),
    FIRST_CLEANING("first_cleaning", "First Steps", "Complete your first cleaning", "🪣", 1),
    PERFECT_WEEK("perfect_week", "Perfect Week", "No missed cleanings for a week", "⭐", 7),
    CLEAN_MASTER("clean_master", "Clean Master", "Complete 100 cleanings", "👑", 100),
    ZONE_COLLECTOR("zone_collector", "Zone Collector", "Create 10 zones", "📋", 10),
    EARLY_BIRD("early_bird", "Early Bird", "Complete a cleaning before 8 AM", "🌅", 1),
    ORGANIZED("organized", "Organized", "Keep all zones clean for 30 days", "📊", 30)
}

