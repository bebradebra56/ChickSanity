package com.chickisa.sofskil

import com.chickisa.sofskil.data.model.AchievementType
import org.junit.Assert.*
import org.junit.Test

class AchievementTest {
    
    @Test
    fun achievementTypes_haveUniqueIds() {
        val ids = AchievementType.entries.map { it.id }
        val uniqueIds = ids.toSet()
        
        assertEquals(ids.size, uniqueIds.size)
    }
    
    @Test
    fun achievementTypes_haveValidProgress() {
        AchievementType.entries.forEach { achievement ->
            assertTrue(achievement.requiredProgress > 0)
        }
    }
    
    @Test
    fun firstCleaning_hasCorrectRequirement() {
        val firstCleaning = AchievementType.FIRST_CLEANING
        
        assertEquals("first_cleaning", firstCleaning.id)
        assertEquals("First Steps", firstCleaning.title)
        assertEquals(1, firstCleaning.requiredProgress)
    }
    
    @Test
    fun decentFarmer_hasCorrectRequirement() {
        val decentFarmer = AchievementType.DECENT_FARMER
        
        assertEquals("decent_farmer", decentFarmer.id)
        assertEquals("Decent Farmer", decentFarmer.title)
        assertEquals(50, decentFarmer.requiredProgress)
    }
    
    @Test
    fun cleanMaster_hasCorrectRequirement() {
        val cleanMaster = AchievementType.CLEAN_MASTER
        
        assertEquals("clean_master", cleanMaster.id)
        assertEquals("Clean Master", cleanMaster.title)
        assertEquals(100, cleanMaster.requiredProgress)
    }
}

