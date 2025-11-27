package com.chickisa.sofskil.zxcvb.domain.model

import com.google.gson.annotations.SerializedName


data class ChickSanityEntity (
    @SerializedName("ok")
    val chickSanityOk: String,
    @SerializedName("url")
    val chickSanityUrl: String,
    @SerializedName("expires")
    val chickSanityExpires: Long,
)