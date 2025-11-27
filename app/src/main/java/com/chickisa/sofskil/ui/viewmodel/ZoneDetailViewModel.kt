package com.chickisa.sofskil.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.chickisa.sofskil.data.database.AppDatabase
import com.chickisa.sofskil.data.model.AchievementType
import com.chickisa.sofskil.data.model.CleaningHistory
import com.chickisa.sofskil.data.model.Zone
import com.chickisa.sofskil.data.repository.AchievementRepository
import com.chickisa.sofskil.data.repository.CleaningHistoryRepository
import com.chickisa.sofskil.data.repository.ZoneRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class ZoneDetailViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val zoneRepository = ZoneRepository(database.zoneDao())
    private val historyRepository = CleaningHistoryRepository(database.cleaningHistoryDao())
    private val achievementRepository = AchievementRepository(database.achievementDao())
    
    private val _currentZoneId = MutableStateFlow<Long?>(null)
    
    val zone: StateFlow<Zone?> = _currentZoneId.flatMapLatest { zoneId ->
        if (zoneId != null) {
            zoneRepository.getZoneById(zoneId)
        } else {
            flowOf(null)
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    
    val history: StateFlow<List<CleaningHistory>> = _currentZoneId.flatMapLatest { zoneId ->
        if (zoneId != null) {
            historyRepository.getHistoryForZone(zoneId)
        } else {
            flowOf(emptyList())
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    fun setZoneId(zoneId: Long) {
        _currentZoneId.value = zoneId
    }
    
    fun markAsCleaned() {
        viewModelScope.launch {
            val currentZone = zone.value
            if (currentZone != null) {
                val currentTime = System.currentTimeMillis()
                val updatedZone = currentZone.copy(lastCleaningTimestamp = currentTime)
                zoneRepository.updateZone(updatedZone)
                
                historyRepository.insertHistory(
                    CleaningHistory(
                        zoneId = currentZone.id,
                        cleaningTimestamp = currentTime
                    )
                )
                
                // Check achievements
                checkAchievements(currentTime)
            }
        }
    }
    
    private suspend fun checkAchievements(timestamp: Long) {
        // Check cleaning count achievements
        val totalCleanings = historyRepository.getTotalCleaningCount()
        achievementRepository.updateProgress(AchievementType.FIRST_CLEANING.id, totalCleanings)
        achievementRepository.updateProgress(AchievementType.DECENT_FARMER.id, totalCleanings)
        achievementRepository.updateProgress(AchievementType.CLEAN_MASTER.id, totalCleanings)
        
        // Check early bird
        val calendar = java.util.Calendar.getInstance()
        calendar.timeInMillis = timestamp
        val hour = calendar.get(java.util.Calendar.HOUR_OF_DAY)
        if (hour < 8) {
            achievementRepository.updateProgress(AchievementType.EARLY_BIRD.id, 1)
        }
    }
    
    fun updateZone(zone: Zone) {
        viewModelScope.launch {
            zoneRepository.updateZone(zone)
        }
    }
    
    fun deleteZone() {
        viewModelScope.launch {
            zone.value?.let {
                zoneRepository.deleteZone(it)
            }
        }
    }
}

