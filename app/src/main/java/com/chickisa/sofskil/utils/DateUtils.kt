package com.chickisa.sofskil.utils

import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit

object DateUtils {
    
    fun formatDate(timestamp: Long, format: String = "MMM dd, yyyy"): String {
        return SimpleDateFormat(format, Locale.getDefault()).format(Date(timestamp))
    }
    
    fun formatDateTime(timestamp: Long): String {
        return SimpleDateFormat("MMM dd, yyyy 'at' HH:mm", Locale.getDefault()).format(Date(timestamp))
    }
    
    fun getDaysAgo(timestamp: Long): Int {
        val now = System.currentTimeMillis()
        val diff = now - timestamp
        return TimeUnit.MILLISECONDS.toDays(diff).toInt()
    }
    
    fun getDaysUntil(timestamp: Long): Int {
        val now = System.currentTimeMillis()
        val diff = timestamp - now
        return TimeUnit.MILLISECONDS.toDays(diff).toInt()
    }
    
    fun getRelativeTimeString(timestamp: Long): String {
        val daysAgo = getDaysAgo(timestamp)
        return when {
            daysAgo == 0 -> "Today"
            daysAgo == 1 -> "Yesterday"
            daysAgo < 7 -> "$daysAgo days ago"
            daysAgo < 30 -> "${daysAgo / 7} ${if (daysAgo / 7 == 1) "week" else "weeks"} ago"
            daysAgo < 365 -> "${daysAgo / 30} ${if (daysAgo / 30 == 1) "month" else "months"} ago"
            else -> "${daysAgo / 365} ${if (daysAgo / 365 == 1) "year" else "years"} ago"
        }
    }
    
    fun isToday(timestamp: Long): Boolean {
        val calendar1 = Calendar.getInstance()
        val calendar2 = Calendar.getInstance()
        calendar2.timeInMillis = timestamp
        
        return calendar1.get(Calendar.YEAR) == calendar2.get(Calendar.YEAR) &&
                calendar1.get(Calendar.DAY_OF_YEAR) == calendar2.get(Calendar.DAY_OF_YEAR)
    }
    
    fun getStartOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }
    
    fun getEndOfDay(timestamp: Long = System.currentTimeMillis()): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
}

