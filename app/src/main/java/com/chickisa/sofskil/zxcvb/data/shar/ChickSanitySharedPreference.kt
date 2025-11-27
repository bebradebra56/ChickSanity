package com.chickisa.sofskil.zxcvb.data.shar

import android.content.Context
import androidx.core.content.edit

class ChickSanitySharedPreference(context: Context) {
    private val chickSanityPrefs = context.getSharedPreferences("chickSanitySharedPrefsAb", Context.MODE_PRIVATE)

    var chickSanitySavedUrl: String
        get() = chickSanityPrefs.getString(CHICK_SANITY_SAVED_URL, "") ?: ""
        set(value) = chickSanityPrefs.edit { putString(CHICK_SANITY_SAVED_URL, value) }

    var chickSanityExpired : Long
        get() = chickSanityPrefs.getLong(CHICK_SANITY_EXPIRED, 0L)
        set(value) = chickSanityPrefs.edit { putLong(CHICK_SANITY_EXPIRED, value) }

    var chickSanityAppState: Int
        get() = chickSanityPrefs.getInt(CHICK_SANITY_APPLICATION_STATE, 0)
        set(value) = chickSanityPrefs.edit { putInt(CHICK_SANITY_APPLICATION_STATE, value) }

    var chickSanityNotificationRequest: Long
        get() = chickSanityPrefs.getLong(CHICK_SANITY_NOTIFICAITON_REQUEST, 0L)
        set(value) = chickSanityPrefs.edit { putLong(CHICK_SANITY_NOTIFICAITON_REQUEST, value) }

    var chickSanityNotificationRequestedBefore: Boolean
        get() = chickSanityPrefs.getBoolean(CHICK_SANITY_NOTIFICATION_REQUEST_BEFORE, false)
        set(value) = chickSanityPrefs.edit { putBoolean(
            CHICK_SANITY_NOTIFICATION_REQUEST_BEFORE, value) }

    companion object {
        private const val CHICK_SANITY_SAVED_URL = "chickSanitySavedUrl"
        private const val CHICK_SANITY_EXPIRED = "chickSanityExpired"
        private const val CHICK_SANITY_APPLICATION_STATE = "chickSanityApplicationState"
        private const val CHICK_SANITY_NOTIFICAITON_REQUEST = "chickSanityNotificationRequest"
        private const val CHICK_SANITY_NOTIFICATION_REQUEST_BEFORE = "chickSanityNotificationRequestedBefore"
    }
}