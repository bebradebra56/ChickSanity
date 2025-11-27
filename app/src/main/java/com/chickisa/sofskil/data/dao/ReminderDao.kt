package com.chickisa.sofskil.data.dao

import androidx.room.*
import com.chickisa.sofskil.data.model.Reminder
import kotlinx.coroutines.flow.Flow

@Dao
interface ReminderDao {
    @Query("SELECT * FROM reminders WHERE isEnabled = 1")
    fun getAllActiveReminders(): Flow<List<Reminder>>
    
    @Query("SELECT * FROM reminders WHERE zoneId = :zoneId")
    fun getRemindersForZone(zoneId: Long): Flow<List<Reminder>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReminder(reminder: Reminder): Long
    
    @Update
    suspend fun updateReminder(reminder: Reminder)
    
    @Delete
    suspend fun deleteReminder(reminder: Reminder)
    
    @Query("DELETE FROM reminders WHERE zoneId = :zoneId")
    suspend fun deleteRemindersForZone(zoneId: Long)
}

