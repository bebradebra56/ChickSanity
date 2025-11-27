package com.chickisa.sofskil.data.repository

import com.chickisa.sofskil.data.dao.ZoneDao
import com.chickisa.sofskil.data.model.Zone
import kotlinx.coroutines.flow.Flow

class ZoneRepository(private val zoneDao: ZoneDao) {
    
    fun getAllZones(): Flow<List<Zone>> = zoneDao.getAllZones()
    
    fun getZoneById(zoneId: Long): Flow<Zone?> = zoneDao.getZoneById(zoneId)
    
    suspend fun getZoneByIdSync(zoneId: Long): Zone? = zoneDao.getZoneByIdSync(zoneId)
    
    suspend fun insertZone(zone: Zone): Long = zoneDao.insertZone(zone)
    
    suspend fun updateZone(zone: Zone) = zoneDao.updateZone(zone)
    
    suspend fun deleteZone(zone: Zone) = zoneDao.deleteZone(zone)
    
    suspend fun deactivateZone(zoneId: Long) = zoneDao.deactivateZone(zoneId)
    
    fun getActiveZoneCount(): Flow<Int> = zoneDao.getActiveZoneCount()
    
    suspend fun markZoneAsCleaned(zoneId: Long) {
        val zone = zoneDao.getZoneByIdSync(zoneId)
        zone?.let {
            val updatedZone = it.copy(lastCleaningTimestamp = System.currentTimeMillis())
            zoneDao.updateZone(updatedZone)
        }
    }
}

