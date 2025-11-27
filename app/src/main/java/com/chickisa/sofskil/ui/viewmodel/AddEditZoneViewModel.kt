package com.chickisa.sofskil.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.chickisa.sofskil.data.database.AppDatabase
import com.chickisa.sofskil.data.model.AchievementType
import com.chickisa.sofskil.data.model.Zone
import com.chickisa.sofskil.data.model.ZoneCategory
import com.chickisa.sofskil.data.repository.AchievementRepository
import com.chickisa.sofskil.data.repository.ZoneRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class AddEditZoneViewModel(application: Application) : AndroidViewModel(application) {
    
    private val database = AppDatabase.getDatabase(application)
    private val zoneRepository = ZoneRepository(database.zoneDao())
    private val achievementRepository = AchievementRepository(database.achievementDao())
    
    private val _name = MutableStateFlow("")
    val name: StateFlow<String> = _name
    
    private val _category = MutableStateFlow(ZoneCategory.COOP)
    val category: StateFlow<ZoneCategory> = _category
    
    private val _cleaningFrequencyDays = MutableStateFlow(7)
    val cleaningFrequencyDays: StateFlow<Int> = _cleaningFrequencyDays
    
    private val _notes = MutableStateFlow("")
    val notes: StateFlow<String> = _notes
    
    private val _isEditMode = MutableStateFlow(false)
    val isEditMode: StateFlow<Boolean> = _isEditMode
    
    private var editingZoneId: Long? = null
    
    fun setName(value: String) {
        _name.value = value
    }
    
    fun setCategory(value: ZoneCategory) {
        _category.value = value
    }
    
    fun setCleaningFrequencyDays(value: Int) {
        _cleaningFrequencyDays.value = value.coerceIn(1, 365)
    }
    
    fun setNotes(value: String) {
        _notes.value = value
    }
    
    fun loadZone(zoneId: Long) {
        viewModelScope.launch {
            val zone = zoneRepository.getZoneByIdSync(zoneId)
            if (zone != null) {
                _name.value = zone.name
                _category.value = zone.category
                _cleaningFrequencyDays.value = zone.cleaningFrequencyDays
                _notes.value = zone.notes
                _isEditMode.value = true
                editingZoneId = zoneId
            }
        }
    }
    
    fun saveZone(onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                if (_name.value.isBlank()) return@launch
                
                val zone = Zone(
                    id = editingZoneId ?: 0,
                    name = _name.value.trim(),
                    category = _category.value,
                    lastCleaningTimestamp = if (_isEditMode.value) {
                        zoneRepository.getZoneByIdSync(editingZoneId!!)?.lastCleaningTimestamp 
                            ?: System.currentTimeMillis()
                    } else {
                        System.currentTimeMillis()
                    },
                    cleaningFrequencyDays = _cleaningFrequencyDays.value,
                    notes = _notes.value.trim()
                )
                
                if (_isEditMode.value) {
                    zoneRepository.updateZone(zone)
                } else {
                    zoneRepository.insertZone(zone)
                }
                
                // Call success callback immediately after save
                onSuccess()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
    
    fun resetForm() {
        _name.value = ""
        _category.value = ZoneCategory.COOP
        _cleaningFrequencyDays.value = 7
        _notes.value = ""
        _isEditMode.value = false
        editingZoneId = null
    }
}

