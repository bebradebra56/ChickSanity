package com.chickisa.sofskil.zxcvb.data.utils

import android.util.Log
import com.chickisa.sofskil.zxcvb.presentation.app.ChickSanityApplication
import com.google.firebase.messaging.FirebaseMessaging
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ChickSanityPushToken {

    suspend fun chickSanityGetToken(): String = suspendCoroutine { continuation ->
        try {
            FirebaseMessaging.getInstance().token.addOnCompleteListener {
                if (!it.isSuccessful) {
                    continuation.resume(it.result)
                    Log.d(ChickSanityApplication.CHICK_SANITY_MAIN_TAG, "Token error: ${it.exception}")
                } else {
                    continuation.resume(it.result)
                }
            }
        } catch (e: Exception) {
            Log.d(ChickSanityApplication.CHICK_SANITY_MAIN_TAG, "FirebaseMessagingPushToken = null")
            continuation.resume("")
        }
    }


}