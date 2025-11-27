package com.chickisa.sofskil.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.chickisa.sofskil.data.dao.AchievementDao
import com.chickisa.sofskil.data.dao.CleaningHistoryDao
import com.chickisa.sofskil.data.dao.ReminderDao
import com.chickisa.sofskil.data.dao.ZoneDao
import com.chickisa.sofskil.data.model.Achievement
import com.chickisa.sofskil.data.model.CleaningHistory
import com.chickisa.sofskil.data.model.Reminder
import com.chickisa.sofskil.data.model.Zone

@Database(
    entities = [
        Zone::class,
        CleaningHistory::class,
        Achievement::class,
        Reminder::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun zoneDao(): ZoneDao
    abstract fun cleaningHistoryDao(): CleaningHistoryDao
    abstract fun achievementDao(): AchievementDao
    abstract fun reminderDao(): ReminderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "farm_cleaner_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}

