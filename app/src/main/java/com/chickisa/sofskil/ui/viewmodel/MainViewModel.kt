package com.chickisa.sofskil.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.chickisa.sofskil.data.database.AppDatabase
import com.chickisa.sofskil.data.model.Achievement
import com.chickisa.sofskil.data.model.AchievementType
import com.chickisa.sofskil.data.model.CleaningHistory
import com.chickisa.sofskil.data.model.Zone
import com.chickisa.sofskil.data.repository.AchievementRepository
import com.chickisa.sofskil.data.repository.CleaningHistoryRepository
import com.chickisa.sofskil.data.repository.ZoneRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class MainViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val zoneRepository = ZoneRepository(database.zoneDao())
    private val historyRepository = CleaningHistoryRepository(database.cleaningHistoryDao())
    private val achievementRepository = AchievementRepository(database.achievementDao())
    
    val zones: StateFlow<List<Zone>> = zoneRepository.getAllZones()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val cleanZonesPercentage: StateFlow<Int> = zones.map { zoneList ->
        if (zoneList.isEmpty()) 0
        else (zoneList.count { it.isClean() } * 100 / zoneList.size)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)
    
    private val _filterCategory = MutableStateFlow<String?>(null)
    val filterCategory: StateFlow<String?> = _filterCategory
    
    private val _showOnlyDirty = MutableStateFlow(false)
    val showOnlyDirty: StateFlow<Boolean> = _showOnlyDirty
    
    val filteredZones: StateFlow<List<Zone>> = combine(
        zones,
        _filterCategory,
        _showOnlyDirty
    ) { zoneList, category, onlyDirty ->
        var filtered = zoneList
        
        if (category != null) {
            filtered = filtered.filter { it.category.name == category }
        }
        
        if (onlyDirty) {
            filtered = filtered.filter { !it.isClean() }
        }
        
        filtered.sortedBy { it.getDaysUntilNextCleaning() }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    init {
        // Initialize achievements on ViewModel creation
        viewModelScope.launch {
            achievementRepository.initializeAchievements()
        }
        
        // Check zone collector achievement whenever zones change
        viewModelScope.launch {
            zones.collect { zoneList ->
                if (zoneList.isNotEmpty()) {
                    achievementRepository.updateProgress(AchievementType.ZONE_COLLECTOR.id, zoneList.size)
                }
            }
        }
    }
    
    fun setFilterCategory(category: String?) {
        _filterCategory.value = category
    }
    
    fun toggleShowOnlyDirty() {
        _showOnlyDirty.value = !_showOnlyDirty.value
    }
    
    fun markZoneAsCleaned(zone: Zone) {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            
            // Update zone
            val updatedZone = zone.copy(lastCleaningTimestamp = currentTime)
            zoneRepository.updateZone(updatedZone)
            
            // Add to history
            historyRepository.insertHistory(
                CleaningHistory(
                    zoneId = zone.id,
                    cleaningTimestamp = currentTime
                )
            )
            
            // Check achievements
            checkCleaningAchievements()
            checkTimeBasedAchievements()
            checkEarlyBirdAchievement(currentTime)
        }
    }
    
    private suspend fun checkCleaningAchievements() {
        val totalCleanings = historyRepository.getTotalCleaningCount()
        
        // First cleaning
        achievementRepository.updateProgress(AchievementType.FIRST_CLEANING.id, totalCleanings)
        
        // Decent Farmer - 50 cleanings
        achievementRepository.updateProgress(AchievementType.DECENT_FARMER.id, totalCleanings)
        
        // Clean Master - 100 cleanings
        achievementRepository.updateProgress(AchievementType.CLEAN_MASTER.id, totalCleanings)
    }
    
    private suspend fun checkTimeBasedAchievements() {
        val now = System.currentTimeMillis()
        val sevenDaysAgo = now - (7 * 24 * 60 * 60 * 1000)
        val thirtyDaysAgo = now - (30 * 24 * 60 * 60 * 1000)
        
        // Check if all zones are clean
        val allZones = zones.value
        if (allZones.isEmpty()) return
        
        val allClean = allZones.all { it.isClean() }
        
        if (allClean) {
            // Shining Farm & Perfect Week - check if maintained for 7 days
            val recentCleanings = historyRepository.getCleaningCountSince(sevenDaysAgo)
            
            // Calculate consecutive clean days
            var consecutiveDays = 0
            for (i in 0 until 7) {
                val dayStart = now - (i * 24 * 60 * 60 * 1000)
                val dayEnd = dayStart + (24 * 60 * 60 * 1000)
                
                val allZonesCleanThatDay = allZones.all { zone ->
                    zone.lastCleaningTimestamp >= dayStart - (zone.cleaningFrequencyDays * 24 * 60 * 60 * 1000)
                }
                
                if (allZonesCleanThatDay) {
                    consecutiveDays++
                } else {
                    break
                }
            }
            
            // Shining Farm - 7 days all clean
            if (consecutiveDays >= 7) {
                achievementRepository.updateProgress(AchievementType.SHINING_FARM.id, 7)
            }
            
            // Perfect Week - no missed cleanings for a week
            if (consecutiveDays >= 7) {
                achievementRepository.updateProgress(AchievementType.PERFECT_WEEK.id, 7)
            }
            
            // Organized - 30 days
            val monthCleanings = historyRepository.getCleaningCountSince(thirtyDaysAgo)
            if (monthCleanings >= allZones.size * 4) { // At least 4 cleanings per zone in 30 days
                achievementRepository.updateProgress(AchievementType.ORGANIZED.id, 30)
            }
        }
    }
    
    private suspend fun checkEarlyBirdAchievement(timestamp: Long) {
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        
        // Early Bird - cleaning before 8 AM
        if (hour < 8) {
            achievementRepository.updateProgress(AchievementType.EARLY_BIRD.id, 1)
        }
    }
    
    fun deleteZone(zone: Zone) {
        viewModelScope.launch {
            zoneRepository.deleteZone(zone)
        }
    }
}

