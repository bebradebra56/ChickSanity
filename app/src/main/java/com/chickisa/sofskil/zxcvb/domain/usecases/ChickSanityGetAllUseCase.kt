package com.chickisa.sofskil.zxcvb.domain.usecases

import android.util.Log
import com.chickisa.sofskil.zxcvb.data.repo.ChickSanityRepository
import com.chickisa.sofskil.zxcvb.data.utils.ChickSanityPushToken
import com.chickisa.sofskil.zxcvb.data.utils.ChickSanitySystemService
import com.chickisa.sofskil.zxcvb.domain.model.ChickSanityEntity
import com.chickisa.sofskil.zxcvb.domain.model.ChickSanityParam
import com.chickisa.sofskil.zxcvb.presentation.app.ChickSanityApplication

class ChickSanityGetAllUseCase(
    private val chickSanityRepository: ChickSanityRepository,
    private val chickSanitySystemService: ChickSanitySystemService,
    private val chickSanityPushToken: ChickSanityPushToken,
) {
    suspend operator fun invoke(conversion: MutableMap<String, Any>?) : ChickSanityEntity?{
        val params = ChickSanityParam(
            chickSanityLocale = chickSanitySystemService.chickSanityGetLocale(),
            chickSanityPushToken = chickSanityPushToken.chickSanityGetToken(),
            chickSanityAfId = chickSanitySystemService.chickSanityGetAppsflyerId()
        )
        Log.d(ChickSanityApplication.CHICK_SANITY_MAIN_TAG, "Params for request: $params")
        return chickSanityRepository.chickSanityGetClient(params, conversion)
    }



}