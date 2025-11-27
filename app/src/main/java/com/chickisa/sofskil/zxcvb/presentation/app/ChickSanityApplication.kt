package com.chickisa.sofskil.zxcvb.presentation.app

import android.app.Application
import android.util.Log
import android.view.WindowManager
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.appsflyer.attribution.AppsFlyerRequestListener
import com.appsflyer.deeplink.DeepLink
import com.appsflyer.deeplink.DeepLinkListener
import com.appsflyer.deeplink.DeepLinkResult
import com.chickisa.sofskil.zxcvb.presentation.di.chickSanityModule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.Query


sealed interface ChickSanityAppsFlyerState {
    data object ChickSanityDefault : ChickSanityAppsFlyerState
    data class ChickSanitySuccess(val chickSanityData: MutableMap<String, Any>?) :
        ChickSanityAppsFlyerState

    data object ChickSanityError : ChickSanityAppsFlyerState
}

interface ChickSanityAppsApi {
    @Headers("Content-Type: application/json")
    @GET(CHICK_SANITY_LIN)
    fun chickSanityGetClient(
        @Query("devkey") devkey: String,
        @Query("device_id") deviceId: String,
    ): Call<MutableMap<String, Any>?>
}

private const val CHICK_SANITY_APP_DEV = "PWBThfNXSxJfcps3jErJe4"
private const val CHICK_SANITY_LIN = "com.chickisa.sofskil"

class ChickSanityApplication : Application() {
    private var chickSanityIsResumed = false
    private var chickSanityConversionTimeoutJob: Job? = null
    private var chickSanityDeepLinkData: MutableMap<String, Any>? = null

    override fun onCreate() {
        super.onCreate()

        val appsflyer = AppsFlyerLib.getInstance()
        chickSanitySetDebufLogger(appsflyer)
        chickSanityMinTimeBetween(appsflyer)

        AppsFlyerLib.getInstance().subscribeForDeepLink(object : DeepLinkListener {
            override fun onDeepLinking(p0: DeepLinkResult) {
                when (p0.status) {
                    DeepLinkResult.Status.FOUND -> {
                        chickSanityExtractDeepMap(p0.deepLink)
                        Log.d(CHICK_SANITY_MAIN_TAG, "onDeepLinking found: ${p0.deepLink}")

                    }

                    DeepLinkResult.Status.NOT_FOUND -> {
                        Log.d(CHICK_SANITY_MAIN_TAG, "onDeepLinking not found: ${p0.deepLink}")
                    }

                    DeepLinkResult.Status.ERROR -> {
                        Log.d(CHICK_SANITY_MAIN_TAG, "onDeepLinking error: ${p0.error}")
                    }
                }
            }

        })


        appsflyer.init(
            CHICK_SANITY_APP_DEV,
            object : AppsFlyerConversionListener {
                override fun onConversionDataSuccess(p0: MutableMap<String, Any>?) {
                    chickSanityConversionTimeoutJob?.cancel()
                    Log.d(CHICK_SANITY_MAIN_TAG, "onConversionDataSuccess: $p0")

                    val afStatus = p0?.get("af_status")?.toString() ?: "null"
                    if (afStatus == "Organic") {
                        CoroutineScope(Dispatchers.IO).launch {
                            try {
                                delay(5000)
                                val api = chickSanityGetApi(
                                    "https://gcdsdk.appsflyer.com/install_data/v4.0/",
                                    null
                                )
                                val response = api.chickSanityGetClient(
                                    devkey = CHICK_SANITY_APP_DEV,
                                    deviceId = chickSanityGetAppsflyerId()
                                ).awaitResponse()

                                val resp = response.body()
                                Log.d(CHICK_SANITY_MAIN_TAG, "After 5s: $resp")
                                if (resp?.get("af_status") == "Organic" || resp?.get("af_status") == null) {
                                    chickSanityResume(ChickSanityAppsFlyerState.ChickSanityError)
                                } else {
                                    chickSanityResume(
                                        ChickSanityAppsFlyerState.ChickSanitySuccess(resp)
                                    )
                                }
                            } catch (d: Exception) {
                                Log.d(CHICK_SANITY_MAIN_TAG, "Error: ${d.message}")
                                chickSanityResume(ChickSanityAppsFlyerState.ChickSanityError)
                            }
                        }
                    } else {
                        chickSanityResume(ChickSanityAppsFlyerState.ChickSanitySuccess(p0))
                    }
                }

                override fun onConversionDataFail(p0: String?) {
                    chickSanityConversionTimeoutJob?.cancel()
                    Log.d(CHICK_SANITY_MAIN_TAG, "onConversionDataFail: $p0")
                    chickSanityResume(ChickSanityAppsFlyerState.ChickSanityError)
                }

                override fun onAppOpenAttribution(p0: MutableMap<String, String>?) {
                    Log.d(CHICK_SANITY_MAIN_TAG, "onAppOpenAttribution")
                }

                override fun onAttributionFailure(p0: String?) {
                    Log.d(CHICK_SANITY_MAIN_TAG, "onAttributionFailure: $p0")
                }
            },
            this
        )

        appsflyer.start(this, CHICK_SANITY_APP_DEV, object :
            AppsFlyerRequestListener {
            override fun onSuccess() {
                Log.d(CHICK_SANITY_MAIN_TAG, "AppsFlyer started")
            }

            override fun onError(p0: Int, p1: String) {
                Log.d(CHICK_SANITY_MAIN_TAG, "AppsFlyer start error: $p0 - $p1")
            }
        })
        chickSanityStartConversionTimeout()
        startKoin {
            androidLogger(Level.DEBUG)
            androidContext(this@ChickSanityApplication)
            modules(
                listOf(
                    chickSanityModule
                )
            )
        }
    }

    private fun chickSanityExtractDeepMap(dl: DeepLink) {
        val map = mutableMapOf<String, Any>()
        dl.deepLinkValue?.let { map["deep_link_value"] = it }
        dl.mediaSource?.let { map["media_source"] = it }
        dl.campaign?.let { map["campaign"] = it }
        dl.campaignId?.let { map["campaign_id"] = it }
        dl.afSub1?.let { map["af_sub1"] = it }
        dl.afSub2?.let { map["af_sub2"] = it }
        dl.afSub3?.let { map["af_sub3"] = it }
        dl.afSub4?.let { map["af_sub4"] = it }
        dl.afSub5?.let { map["af_sub5"] = it }
        dl.matchType?.let { map["match_type"] = it }
        dl.clickHttpReferrer?.let { map["click_http_referrer"] = it }
        dl.getStringValue("timestamp")?.let { map["timestamp"] = it }
        dl.isDeferred?.let { map["is_deferred"] = it }
        for (i in 1..10) {
            val key = "deep_link_sub$i"
            dl.getStringValue(key)?.let {
                if (!map.containsKey(key)) {
                    map[key] = it
                }
            }
        }
        Log.d(CHICK_SANITY_MAIN_TAG, "Extracted DeepLink data: $map")
        chickSanityDeepLinkData = map
    }

    private fun chickSanityStartConversionTimeout() {
        chickSanityConversionTimeoutJob = CoroutineScope(Dispatchers.Main).launch {
            delay(30000)
            if (!chickSanityIsResumed) {
                Log.d(CHICK_SANITY_MAIN_TAG, "TIMEOUT: No conversion data received in 30s")
                chickSanityResume(ChickSanityAppsFlyerState.ChickSanityError)
            }
        }
    }

    private fun chickSanityResume(state: ChickSanityAppsFlyerState) {
        chickSanityConversionTimeoutJob?.cancel()
        if (state is ChickSanityAppsFlyerState.ChickSanitySuccess) {
            val convData = state.chickSanityData ?: mutableMapOf()
            val deepData = chickSanityDeepLinkData ?: mutableMapOf()
            val merged = mutableMapOf<String, Any>().apply {
                putAll(convData)
                for ((key, value) in deepData) {
                    if (!containsKey(key)) {
                        put(key, value)
                    }
                }
            }
            if (!chickSanityIsResumed) {
                chickSanityIsResumed = true
                chickSanityConversionFlow.value = ChickSanityAppsFlyerState.ChickSanitySuccess(merged)
            }
        } else {
            if (!chickSanityIsResumed) {
                chickSanityIsResumed = true
                chickSanityConversionFlow.value = state
            }
        }
    }

    private fun chickSanityGetAppsflyerId(): String {
        val appsflyrid = AppsFlyerLib.getInstance().getAppsFlyerUID(this) ?: ""
        Log.d(CHICK_SANITY_MAIN_TAG, "AppsFlyer: AppsFlyer Id = $appsflyrid")
        return appsflyrid
    }

    private fun chickSanitySetDebufLogger(appsflyer: AppsFlyerLib) {
        appsflyer.setDebugLog(true)
    }

    private fun chickSanityMinTimeBetween(appsflyer: AppsFlyerLib) {
        appsflyer.setMinTimeBetweenSessions(0)
    }

    private fun chickSanityGetApi(url: String, client: OkHttpClient?): ChickSanityAppsApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }

    companion object {
        var chickSanityInputMode = WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN
        val chickSanityConversionFlow: MutableStateFlow<ChickSanityAppsFlyerState> = MutableStateFlow(
            ChickSanityAppsFlyerState.ChickSanityDefault
        )
        var CHICK_SANITY_FB_LI: String? = null
        const val CHICK_SANITY_MAIN_TAG = "ChickSanityMainTag"
    }
}