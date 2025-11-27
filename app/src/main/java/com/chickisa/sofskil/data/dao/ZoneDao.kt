package com.chickisa.sofskil.data.dao

import androidx.room.*
import com.chickisa.sofskil.data.model.Zone
import kotlinx.coroutines.flow.Flow

@Dao
interface ZoneDao {
    @Query("SELECT * FROM zones WHERE isActive = 1 ORDER BY lastCleaningTimestamp DESC")
    fun getAllZones(): Flow<List<Zone>>
    
    @Query("SELECT * FROM zones WHERE id = :zoneId")
    fun getZoneById(zoneId: Long): Flow<Zone?>
    
    @Query("SELECT * FROM zones WHERE id = :zoneId")
    suspend fun getZoneByIdSync(zoneId: Long): Zone?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertZone(zone: Zone): Long
    
    @Update
    suspend fun updateZone(zone: Zone)
    
    @Delete
    suspend fun deleteZone(zone: Zone)
    
    @Query("UPDATE zones SET isActive = 0 WHERE id = :zoneId")
    suspend fun deactivateZone(zoneId: Long)
    
    @Query("SELECT COUNT(*) FROM zones WHERE isActive = 1")
    fun getActiveZoneCount(): Flow<Int>
}

