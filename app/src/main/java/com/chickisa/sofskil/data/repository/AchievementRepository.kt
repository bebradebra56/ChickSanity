package com.chickisa.sofskil.data.repository

import com.chickisa.sofskil.data.dao.AchievementDao
import com.chickisa.sofskil.data.model.Achievement
import com.chickisa.sofskil.data.model.AchievementType
import kotlinx.coroutines.flow.Flow

class AchievementRepository(private val achievementDao: AchievementDao) {
    
    fun getAllAchievements(): Flow<List<Achievement>> = achievementDao.getAllAchievements()
    
    suspend fun getAchievementById(achievementId: String): Achievement? = 
        achievementDao.getAchievementById(achievementId)
    
    suspend fun insertAchievement(achievement: Achievement) = 
        achievementDao.insertAchievement(achievement)
    
    suspend fun updateAchievement(achievement: Achievement) = 
        achievementDao.updateAchievement(achievement)
    
    fun getUnlockedCount(): Flow<Int> = achievementDao.getUnlockedCount()
    
    suspend fun initializeAchievements() {
        AchievementType.entries.forEach { type ->
            val existing = achievementDao.getAchievementById(type.id)
            if (existing == null) {
                achievementDao.insertAchievement(
                    Achievement(
                        id = type.id,
                        isUnlocked = false,
                        unlockedTimestamp = null,
                        progress = 0
                    )
                )
            }
        }
    }
    
    suspend fun updateProgress(achievementId: String, progress: Int) {
        val achievement = achievementDao.getAchievementById(achievementId)
        val achievementType = AchievementType.entries.find { it.id == achievementId }
        
        if (achievement != null && achievementType != null) {
            val shouldUnlock = !achievement.isUnlocked && progress >= achievementType.requiredProgress
            
            achievementDao.updateAchievement(
                achievement.copy(
                    progress = progress,
                    isUnlocked = shouldUnlock || achievement.isUnlocked,
                    unlockedTimestamp = if (shouldUnlock) System.currentTimeMillis() else achievement.unlockedTimestamp
                )
            )
        }
    }
}

