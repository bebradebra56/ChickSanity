package com.chickisa.sofskil.data.repository

import com.chickisa.sofskil.data.dao.ReminderDao
import com.chickisa.sofskil.data.model.Reminder
import kotlinx.coroutines.flow.Flow

class ReminderRepository(private val reminderDao: ReminderDao) {
    
    fun getAllActiveReminders(): Flow<List<Reminder>> = reminderDao.getAllActiveReminders()
    
    fun getRemindersForZone(zoneId: Long): Flow<List<Reminder>> = 
        reminderDao.getRemindersForZone(zoneId)
    
    suspend fun insertReminder(reminder: Reminder): Long = reminderDao.insertReminder(reminder)
    
    suspend fun updateReminder(reminder: Reminder) = reminderDao.updateReminder(reminder)
    
    suspend fun deleteReminder(reminder: Reminder) = reminderDao.deleteReminder(reminder)
    
    suspend fun deleteRemindersForZone(zoneId: Long) = 
        reminderDao.deleteRemindersForZone(zoneId)
}

