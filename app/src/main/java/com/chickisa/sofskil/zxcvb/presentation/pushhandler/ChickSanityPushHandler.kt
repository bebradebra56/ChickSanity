package com.chickisa.sofskil.zxcvb.presentation.pushhandler

import android.os.Bundle
import android.util.Log
import com.chickisa.sofskil.zxcvb.presentation.app.ChickSanityApplication

class ChickSanityPushHandler {
    fun chickSanityHandlePush(extras: Bundle?) {
        Log.d(ChickSanityApplication.CHICK_SANITY_MAIN_TAG, "Extras from Push = ${extras?.keySet()}")
        if (extras != null) {
            val map = chickSanityBundleToMap(extras)
            Log.d(ChickSanityApplication.CHICK_SANITY_MAIN_TAG, "Map from Push = $map")
            map?.let {
                if (map.containsKey("url")) {
                    ChickSanityApplication.CHICK_SANITY_FB_LI = map["url"]
                    Log.d(ChickSanityApplication.CHICK_SANITY_MAIN_TAG, "UrlFromActivity = $map")
                }
            }
        } else {
            Log.d(ChickSanityApplication.CHICK_SANITY_MAIN_TAG, "Push data no!")
        }
    }

    private fun chickSanityBundleToMap(extras: Bundle): Map<String, String?>? {
        val map: MutableMap<String, String?> = HashMap()
        val ks = extras.keySet()
        val iterator: Iterator<String> = ks.iterator()
        while (iterator.hasNext()) {
            val key = iterator.next()
            map[key] = extras.getString(key)
        }
        return map
    }

}