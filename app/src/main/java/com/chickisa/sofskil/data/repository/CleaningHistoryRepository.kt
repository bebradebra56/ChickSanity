package com.chickisa.sofskil.data.repository

import com.chickisa.sofskil.data.dao.CleaningHistoryDao
import com.chickisa.sofskil.data.model.CleaningHistory
import kotlinx.coroutines.flow.Flow

class CleaningHistoryRepository(private val cleaningHistoryDao: CleaningHistoryDao) {
    
    fun getHistoryForZone(zoneId: Long): Flow<List<CleaningHistory>> = 
        cleaningHistoryDao.getHistoryForZone(zoneId)
    
    fun getAllHistory(): Flow<List<CleaningHistory>> = cleaningHistoryDao.getAllHistory()
    
    suspend fun insertHistory(history: CleaningHistory): Long = 
        cleaningHistoryDao.insertHistory(history)
    
    suspend fun getTotalCleaningCount(): Int = cleaningHistoryDao.getTotalCleaningCount()
    
    suspend fun getCleaningCountSince(startTimestamp: Long): Int = 
        cleaningHistoryDao.getCleaningCountSince(startTimestamp)
    
    suspend fun deleteHistory(history: CleaningHistory) = 
        cleaningHistoryDao.deleteHistory(history)
}

