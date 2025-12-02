package com.chickisa.sofskil.zxcvb.data.utils

import android.util.Log
import com.chickisa.sofskil.zxcvb.presentation.app.ChickSanityApplication
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.coroutines.delay
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class ChickSanityPushToken {

    suspend fun chickSanityGetToken(
        chickSanityMaxAttempts: Int = 3,
        chickSanityDelayMs: Long = 1500
    ): String {

        repeat(chickSanityMaxAttempts - 1) {
            try {
                val chickSanityToken = FirebaseMessaging.getInstance().token.await()
                return chickSanityToken
            } catch (e: Exception) {
                Log.e(ChickSanityApplication.CHICK_SANITY_MAIN_TAG, "Token error (attempt ${it + 1}): ${e.message}")
                delay(chickSanityDelayMs)
            }
        }

        return try {
            FirebaseMessaging.getInstance().token.await()
        } catch (e: Exception) {
            Log.e(ChickSanityApplication.CHICK_SANITY_MAIN_TAG, "Token error final: ${e.message}")
            "null"
        }
    }


}