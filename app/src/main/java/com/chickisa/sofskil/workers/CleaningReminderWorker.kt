package com.chickisa.sofskil.workers

import android.content.Context
import androidx.work.*
import com.chickisa.sofskil.data.database.AppDatabase
import com.chickisa.sofskil.data.repository.ZoneRepository
import kotlinx.coroutines.flow.first
import java.util.concurrent.TimeUnit

class CleaningReminderWorker(
    context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {
    
    override suspend fun doWork(): Result {
        val database = AppDatabase.getDatabase(applicationContext)
        val zoneRepository = ZoneRepository(database.zoneDao())
        
        // Get all zones that need cleaning
        val zones = zoneRepository.getAllZones().first()
        val dirtyZones = zones.filter { !it.isClean() }
        
        // Send notifications for dirty zones
//        dirtyZones.forEach { zone ->
//            NotificationHelper.showCleaningReminder(
//                applicationContext,
//                zone.name,
//                zone.id
//            )
//        }
        
        return Result.success()
    }
    
    companion object {
        const val WORK_NAME = "cleaning_reminder_work"
        
        fun scheduleWork(context: Context) {
            val constraints = Constraints.Builder()
                .setRequiresBatteryNotLow(true)
                .build()
            
            val workRequest = PeriodicWorkRequestBuilder<CleaningReminderWorker>(
                24, TimeUnit.HOURS
            )
                .setConstraints(constraints)
                .setInitialDelay(1, TimeUnit.HOURS)
                .build()
            
            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }
        
        fun cancelWork(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
        }
    }
}

