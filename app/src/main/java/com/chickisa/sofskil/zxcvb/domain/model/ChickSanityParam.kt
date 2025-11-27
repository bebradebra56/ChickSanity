package com.chickisa.sofskil.zxcvb.domain.model

import com.google.gson.annotations.SerializedName


private const val CHICK_SANITY_A = "com.chickisa.sofskil"
private const val CHICK_SANITY_B = "chicksanity-c02c6"
data class ChickSanityParam (
    @SerializedName("af_id")
    val chickSanityAfId: String,
    @SerializedName("bundle_id")
    val chickSanityBundleId: String = CHICK_SANITY_A,
    @SerializedName("os")
    val chickSanityOs: String = "Android",
    @SerializedName("store_id")
    val chickSanityStoreId: String = CHICK_SANITY_A,
    @SerializedName("locale")
    val chickSanityLocale: String,
    @SerializedName("push_token")
    val chickSanityPushToken: String,
    @SerializedName("firebase_project_id")
    val chickSanityFirebaseProjectId: String = CHICK_SANITY_B,

    )