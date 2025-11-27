package com.chickisa.sofskil.utils

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.chickisa.sofskil.MainActivity
import com.chickisa.sofskil.R

//object NotificationHelper {
//
//    private var notificationId = 1000
//
//    fun showCleaningReminder(
//        context: Context,
//        zoneName: String,
//        zoneId: Long
//    ) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(
//                    context,
//                    Manifest.permission.POST_NOTIFICATIONS
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                return
//            }
//        }
//
//        val intent = Intent(context, MainActivity::class.java).apply {
//            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            putExtra("zone_id", zoneId)
//        }
//
//        val pendingIntent = PendingIntent.getActivity(
//            context,
//            zoneId.toInt(),
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        val notification = NotificationCompat.Builder(context, FarmCleanerApplication.CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setContentTitle("🧹 Time to Clean!")
//            .setContentText("$zoneName needs cleaning")
//            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//            .build()
//
//        NotificationManagerCompat.from(context).notify(notificationId++, notification)
//    }
//
//    fun showAchievementUnlocked(
//        context: Context,
//        achievementTitle: String,
//        achievementEmoji: String
//    ) {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
//            if (ContextCompat.checkSelfPermission(
//                    context,
//                    Manifest.permission.POST_NOTIFICATIONS
//                ) != PackageManager.PERMISSION_GRANTED
//            ) {
//                return
//            }
//        }
//
//        val intent = Intent(context, MainActivity::class.java)
//        val pendingIntent = PendingIntent.getActivity(
//            context,
//            0,
//            intent,
//            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
//        )
//
//        val notification = NotificationCompat.Builder(context, ChickSanityApplication.CHANNEL_ID)
//            .setSmallIcon(R.drawable.ic_launcher_foreground)
//            .setContentTitle("🏆 Achievement Unlocked!")
//            .setContentText("$achievementEmoji $achievementTitle")
//            .setPriority(NotificationCompat.PRIORITY_HIGH)
//            .setContentIntent(pendingIntent)
//            .setAutoCancel(true)
//            .build()
//
//        NotificationManagerCompat.from(context).notify(notificationId++, notification)
//    }
//}

