package com.chickisa.sofskil.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.chickisa.sofskil.data.database.AppDatabase
import com.chickisa.sofskil.data.model.Achievement
import com.chickisa.sofskil.data.model.CleaningHistory
import com.chickisa.sofskil.data.model.Zone
import com.chickisa.sofskil.data.repository.AchievementRepository
import com.chickisa.sofskil.data.repository.CleaningHistoryRepository
import com.chickisa.sofskil.data.repository.ZoneRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.Calendar

data class StatisticsData(
    val totalCleanings: Int = 0,
    val cleanZones: Int = 0,
    val dirtyZones: Int = 0,
    val averageDaysBetweenCleanings: Float = 0f,
    val cleaningsThisWeek: Int = 0,
    val cleaningsThisMonth: Int = 0,
    val cleaningsByDay: Map<String, Int> = emptyMap()
)

class StatisticsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val zoneRepository = ZoneRepository(database.zoneDao())
    private val historyRepository = CleaningHistoryRepository(database.cleaningHistoryDao())
    private val achievementRepository = AchievementRepository(database.achievementDao())
    
    val zones: StateFlow<List<Zone>> = zoneRepository.getAllZones()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val history: StateFlow<List<CleaningHistory>> = historyRepository.getAllHistory()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val achievements: StateFlow<List<Achievement>> = achievementRepository.getAllAchievements()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    
    val statistics: StateFlow<StatisticsData> = combine(
        zones,
        history
    ) { zoneList, historyList ->
        calculateStatistics(zoneList, historyList)
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), StatisticsData())
    
    private fun calculateStatistics(zones: List<Zone>, history: List<CleaningHistory>): StatisticsData {
        val now = System.currentTimeMillis()
        val weekAgo = now - (7 * 24 * 60 * 60 * 1000)
        val monthAgo = now - (30 * 24 * 60 * 60 * 1000)
        
        val cleanZones = zones.count { it.isClean() }
        val dirtyZones = zones.size - cleanZones
        
        val cleaningsThisWeek = history.count { it.cleaningTimestamp >= weekAgo }
        val cleaningsThisMonth = history.count { it.cleaningTimestamp >= monthAgo }
        
        // Calculate average days between cleanings
        val zoneCounts = history.groupBy { it.zoneId }
        val averageDays = if (zoneCounts.isNotEmpty()) {
            val averages = zoneCounts.map { (_, cleanings) ->
                if (cleanings.size < 2) return@map 0f
                
                val sorted = cleanings.sortedBy { it.cleaningTimestamp }
                val intervals = mutableListOf<Long>()
                
                for (i in 1 until sorted.size) {
                    intervals.add(sorted[i].cleaningTimestamp - sorted[i - 1].cleaningTimestamp)
                }
                
                if (intervals.isEmpty()) 0f
                else (intervals.average() / (24 * 60 * 60 * 1000)).toFloat()
            }
            
            averages.filter { it > 0 }.average().toFloat().takeIf { !it.isNaN() } ?: 0f
        } else {
            0f
        }
        
        // Group cleanings by day
        val cleaningsByDay = history.groupBy { history ->
            val calendar = Calendar.getInstance()
            calendar.timeInMillis = history.cleaningTimestamp
            "${calendar.get(Calendar.YEAR)}-${calendar.get(Calendar.MONTH) + 1}-${calendar.get(Calendar.DAY_OF_MONTH)}"
        }.mapValues { it.value.size }
        
        return StatisticsData(
            totalCleanings = history.size,
            cleanZones = cleanZones,
            dirtyZones = dirtyZones,
            averageDaysBetweenCleanings = averageDays,
            cleaningsThisWeek = cleaningsThisWeek,
            cleaningsThisMonth = cleaningsThisMonth,
            cleaningsByDay = cleaningsByDay
        )
    }
}

