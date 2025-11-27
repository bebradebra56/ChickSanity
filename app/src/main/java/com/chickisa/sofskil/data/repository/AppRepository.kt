package com.chickisa.sofskil.data.repository

import android.content.Context
import com.chickisa.sofskil.data.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class AppRepository(private val context: Context) {
    
    private val database = AppDatabase.getDatabase(context)
    
    suspend fun clearAllData() = withContext(Dispatchers.IO) {
        database.clearAllTables()
    }
    
    suspend fun exportData(): String = withContext(Dispatchers.IO) {
        // Future implementation for data export
        "Data export not yet implemented"
    }
    
    suspend fun importData(data: String): Boolean = withContext(Dispatchers.IO) {
        // Future implementation for data import
        false
    }
    
    suspend fun getDatabaseSize(): Long = withContext(Dispatchers.IO) {
        val dbFile = context.getDatabasePath(AppDatabase::class.java.simpleName)
        dbFile.length()
    }
}

