package com.chickisa.sofskil.zxcvb.data.repo

import android.util.Log
import com.chickisa.sofskil.zxcvb.domain.model.ChickSanityEntity
import com.chickisa.sofskil.zxcvb.domain.model.ChickSanityParam
import com.chickisa.sofskil.zxcvb.presentation.app.ChickSanityApplication.Companion.CHICK_SANITY_MAIN_TAG
import com.google.gson.Gson
import com.google.gson.JsonElement
import com.google.gson.JsonObject
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.awaitResponse
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST

interface ChickSanityApi {
    @Headers("Content-Type: application/json")
    @POST("config.php")
    fun chickSanityGetClient(
        @Body jsonString: JsonObject,
    ): Call<ChickSanityEntity>
}


private const val CHICK_SANITY_MAIN = "https://chicksanity.com/"
class ChickSanityRepository {

    suspend fun chickSanityGetClient(
        chickSanityParam: ChickSanityParam,
        chickSanityConversion: MutableMap<String, Any>?
    ): ChickSanityEntity? {
        val gson = Gson()
        val api = chickSanityGetApi(CHICK_SANITY_MAIN, null)

        val chickSanityJsonObject = gson.toJsonTree(chickSanityParam).asJsonObject
        chickSanityConversion?.forEach { (key, value) ->
            val element: JsonElement = gson.toJsonTree(value)
            chickSanityJsonObject.add(key, element)
        }
        return try {
            val chickSanityRequest: Call<ChickSanityEntity> = api.chickSanityGetClient(
                jsonString = chickSanityJsonObject,
            )
            val chickSanityResult = chickSanityRequest.awaitResponse()
            Log.d(CHICK_SANITY_MAIN_TAG, "Retrofit: Result code: ${chickSanityResult.code()}")
            if (chickSanityResult.code() == 200) {
                Log.d(CHICK_SANITY_MAIN_TAG, "Retrofit: Get request success")
                Log.d(CHICK_SANITY_MAIN_TAG, "Retrofit: Code = ${chickSanityResult.code()}")
                Log.d(CHICK_SANITY_MAIN_TAG, "Retrofit: ${chickSanityResult.body()}")
                chickSanityResult.body()
            } else {
                null
            }
        } catch (e: java.lang.Exception) {
            Log.d(CHICK_SANITY_MAIN_TAG, "Retrofit: Get request failed")
            Log.d(CHICK_SANITY_MAIN_TAG, "Retrofit: ${e.message}")
            null
        }
    }


    private fun chickSanityGetApi(url: String, client: OkHttpClient?) : ChickSanityApi {
        val retrofit = Retrofit.Builder()
            .baseUrl(url)
            .client(client ?: OkHttpClient.Builder().build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create()
    }


}
