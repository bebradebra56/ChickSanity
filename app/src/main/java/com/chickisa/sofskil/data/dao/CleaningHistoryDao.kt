package com.chickisa.sofskil.data.dao

import androidx.room.*
import com.chickisa.sofskil.data.model.CleaningHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface CleaningHistoryDao {
    @Query("SELECT * FROM cleaning_history WHERE zoneId = :zoneId ORDER BY cleaningTimestamp DESC")
    fun getHistoryForZone(zoneId: Long): Flow<List<CleaningHistory>>
    
    @Query("SELECT * FROM cleaning_history ORDER BY cleaningTimestamp DESC LIMIT 100")
    fun getAllHistory(): Flow<List<CleaningHistory>>
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: CleaningHistory): Long
    
    @Query("SELECT COUNT(*) FROM cleaning_history")
    suspend fun getTotalCleaningCount(): Int
    
    @Query("SELECT COUNT(*) FROM cleaning_history WHERE cleaningTimestamp >= :startTimestamp")
    suspend fun getCleaningCountSince(startTimestamp: Long): Int
    
    @Delete
    suspend fun deleteHistory(history: CleaningHistory)
}

