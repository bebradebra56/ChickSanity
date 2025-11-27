package com.chickisa.sofskil.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.chickisa.sofskil.data.database.AppDatabase
import com.chickisa.sofskil.data.preferences.PreferencesManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    
    private val preferencesManager = PreferencesManager(application)
    private val database = AppDatabase.getDatabase(application)
    
    val themeMode: StateFlow<String> = preferencesManager.themeMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "light")
    
    val notificationsEnabled: StateFlow<Boolean> = preferencesManager.notificationsEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)
    
    val dateFormat: StateFlow<String> = preferencesManager.dateFormat
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "MMM dd, yyyy")
    
    fun setThemeMode(mode: String) {
        viewModelScope.launch {
            preferencesManager.setThemeMode(mode)
        }
    }
    
    fun setNotificationsEnabled(enabled: Boolean) {
        viewModelScope.launch {
            preferencesManager.setNotificationsEnabled(enabled)
        }
    }
    
    fun setDateFormat(format: String) {
        viewModelScope.launch {
            preferencesManager.setDateFormat(format)
        }
    }
    
    fun clearAllData(onComplete: () -> Unit) {
        viewModelScope.launch {
            try {
                // Clear all database tables
                database.clearAllTables()
                
                // Wait for database to finish clearing
                kotlinx.coroutines.delay(300)
                
                // Re-initialize achievements to ensure clean state
                val achievementDao = database.achievementDao()
                com.chickisa.sofskil.data.model.AchievementType.entries.forEach { type ->
                    achievementDao.insertAchievement(
                        com.chickisa.sofskil.data.model.Achievement(
                            id = type.id,
                            isUnlocked = false,
                            unlockedTimestamp = null,
                            progress = 0
                        )
                    )
                }
                
                // Notify completion
                onComplete()
            } catch (e: Exception) {
                e.printStackTrace()
                onComplete()
            }
        }
    }
}

