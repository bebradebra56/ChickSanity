package com.chickisa.sofskil

import com.chickisa.sofskil.data.model.Zone
import com.chickisa.sofskil.data.model.ZoneCategory
import org.junit.Assert.*
import org.junit.Test

class ZoneModelTest {
    
    @Test
    fun zone_isClean_whenWithinFrequency() {
        val zone = Zone(
            id = 1,
            name = "Test Coop",
            category = ZoneCategory.COOP,
            lastCleaningTimestamp = System.currentTimeMillis() - (2 * 24 * 60 * 60 * 1000), // 2 days ago
            cleaningFrequencyDays = 7
        )
        
        assertTrue(zone.isClean())
    }
    
    @Test
    fun zone_isDirty_whenBeyondFrequency() {
        val zone = Zone(
            id = 1,
            name = "Test Barn",
            category = ZoneCategory.BARN,
            lastCleaningTimestamp = System.currentTimeMillis() - (10 * 24 * 60 * 60 * 1000), // 10 days ago
            cleaningFrequencyDays = 7
        )
        
        assertFalse(zone.isClean())
    }
    
    @Test
    fun zone_calculatesDaysSinceLastCleaning() {
        val twoDaysAgo = System.currentTimeMillis() - (2 * 24 * 60 * 60 * 1000)
        val zone = Zone(
            id = 1,
            name = "Test Shed",
            category = ZoneCategory.SHED,
            lastCleaningTimestamp = twoDaysAgo,
            cleaningFrequencyDays = 7
        )
        
        assertEquals(2, zone.getDaysSinceLastCleaning())
    }
    
    @Test
    fun zone_calculatesNextCleaningDate() {
        val now = System.currentTimeMillis()
        val zone = Zone(
            id = 1,
            name = "Test Zone",
            category = ZoneCategory.COOP,
            lastCleaningTimestamp = now,
            cleaningFrequencyDays = 7
        )
        
        val expectedNextCleaning = now + (7 * 24 * 60 * 60 * 1000)
        val actualNextCleaning = zone.getNextCleaningTimestamp()
        
        // Allow 1 second difference due to processing time
        assertTrue(Math.abs(expectedNextCleaning - actualNextCleaning) < 1000)
    }
    
    @Test
    fun zoneCategory_hasCorrectEmojis() {
        assertEquals("🐔", ZoneCategory.COOP.emoji)
        assertEquals("🪣", ZoneCategory.BARN.emoji)
        assertEquals("🌾", ZoneCategory.SHED.emoji)
        assertEquals("🔧", ZoneCategory.EQUIPMENT.emoji)
        assertEquals("🌿", ZoneCategory.TERRITORY.emoji)
        assertEquals("📦", ZoneCategory.STORAGE.emoji)
    }
}

