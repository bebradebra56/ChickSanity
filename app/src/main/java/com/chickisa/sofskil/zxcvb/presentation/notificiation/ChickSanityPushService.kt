package com.chickisa.sofskil.zxcvb.presentation.notificiation

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.os.bundleOf
import com.chickisa.sofskil.ChickSanityActivity
import com.chickisa.sofskil.R
import com.chickisa.sofskil.zxcvb.presentation.app.ChickSanityApplication
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

private const val CHICK_SANITY_CHANNEL_ID = "chick_sanity_notifications"
private const val CHICK_SANITY_CHANNEL_NAME = "ChickSanity Notifications"
private const val CHICK_SANITY_NOT_TAG = "ChickSanity"

class ChickSanityPushService : FirebaseMessagingService(){
    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)

        // Обработка notification payload
        remoteMessage.notification?.let {
            if (remoteMessage.data.contains("url")) {
                chickSanityShowNotification(it.title ?: CHICK_SANITY_NOT_TAG, it.body ?: "", data = remoteMessage.data["url"])
            } else {
                chickSanityShowNotification(it.title ?: CHICK_SANITY_NOT_TAG, it.body ?: "", data = null)
            }
        }

        // Обработка data payload
        if (remoteMessage.data.isNotEmpty()) {
            chickSanityHandleDataPayload(remoteMessage.data)
        }
    }

    private fun chickSanityShowNotification(title: String, message: String, data: String?) {
        val chickSanityNotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Создаем канал уведомлений для Android 8+
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHICK_SANITY_CHANNEL_ID,
                CHICK_SANITY_CHANNEL_NAME,
                NotificationManager.IMPORTANCE_HIGH
            )
            chickSanityNotificationManager.createNotificationChannel(channel)
        }

        val chickSanityIntent = Intent(this, ChickSanityActivity::class.java).apply {
            putExtras(bundleOf(
                "url" to data
            ))
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        }
        val chickSanityPendingIntent = PendingIntent.getActivity(
            this,
            0,
            chickSanityIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val chickSanityNotification = NotificationCompat.Builder(this, CHICK_SANITY_CHANNEL_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.chick_sanity_noti_icon)
            .setAutoCancel(true)
            .setContentIntent(chickSanityPendingIntent)
            .build()

        chickSanityNotificationManager.notify(System.currentTimeMillis().toInt(), chickSanityNotification)
    }

    private fun chickSanityHandleDataPayload(data: Map<String, String>) {
        data.forEach { (key, value) ->
            Log.d(ChickSanityApplication.CHICK_SANITY_MAIN_TAG, "Data key=$key value=$value")
        }
    }
}