package com.chickisa.sofskil.zxcvb.presentation.ui.load

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chickisa.sofskil.zxcvb.data.shar.ChickSanitySharedPreference
import com.chickisa.sofskil.zxcvb.data.utils.ChickSanitySystemService
import com.chickisa.sofskil.zxcvb.domain.usecases.ChickSanityGetAllUseCase
import com.chickisa.sofskil.zxcvb.presentation.app.ChickSanityAppsFlyerState
import com.chickisa.sofskil.zxcvb.presentation.app.ChickSanityApplication
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ChickSanityLoadViewModel(
    private val chickSanityGetAllUseCase: ChickSanityGetAllUseCase,
    private val chickSanitySharedPreference: ChickSanitySharedPreference,
    private val chickSanitySystemService: ChickSanitySystemService
) : ViewModel() {

    private val _chickSanityHomeScreenState: MutableStateFlow<ChickSanityHomeScreenState> =
        MutableStateFlow(ChickSanityHomeScreenState.ChickSanityLoading)
    val chickSanityHomeScreenState = _chickSanityHomeScreenState.asStateFlow()

    private var chickSanityGetApps = false


    init {
        viewModelScope.launch {
            when (chickSanitySharedPreference.chickSanityAppState) {
                0 -> {
                    if (chickSanitySystemService.chickSanityIsOnline()) {
                        ChickSanityApplication.chickSanityConversionFlow.collect {
                            when(it) {
                                ChickSanityAppsFlyerState.ChickSanityDefault -> {}
                                ChickSanityAppsFlyerState.ChickSanityError -> {
                                    chickSanitySharedPreference.chickSanityAppState = 2
                                    _chickSanityHomeScreenState.value =
                                        ChickSanityHomeScreenState.ChickSanityError
                                    chickSanityGetApps = true
                                }
                                is ChickSanityAppsFlyerState.ChickSanitySuccess -> {
                                    if (!chickSanityGetApps) {
                                        chickSanityGetData(it.chickSanityData)
                                        chickSanityGetApps = true
                                    }
                                }
                            }
                        }
                    } else {
                        _chickSanityHomeScreenState.value =
                            ChickSanityHomeScreenState.ChickSanityNotInternet
                    }
                }
                1 -> {
                    if (chickSanitySystemService.chickSanityIsOnline()) {
                        if (ChickSanityApplication.CHICK_SANITY_FB_LI != null) {
                            _chickSanityHomeScreenState.value =
                                ChickSanityHomeScreenState.ChickSanitySuccess(
                                    ChickSanityApplication.CHICK_SANITY_FB_LI.toString()
                                )
                        } else if (System.currentTimeMillis() / 1000 > chickSanitySharedPreference.chickSanityExpired) {
                            Log.d(ChickSanityApplication.CHICK_SANITY_MAIN_TAG, "Current time more then expired, repeat request")
                            ChickSanityApplication.chickSanityConversionFlow.collect {
                                when(it) {
                                    ChickSanityAppsFlyerState.ChickSanityDefault -> {}
                                    ChickSanityAppsFlyerState.ChickSanityError -> {
                                        _chickSanityHomeScreenState.value =
                                            ChickSanityHomeScreenState.ChickSanitySuccess(
                                                chickSanitySharedPreference.chickSanitySavedUrl
                                            )
                                        chickSanityGetApps = true
                                    }
                                    is ChickSanityAppsFlyerState.ChickSanitySuccess -> {
                                        if (!chickSanityGetApps) {
                                            chickSanityGetData(it.chickSanityData)
                                            chickSanityGetApps = true
                                        }
                                    }
                                }
                            }
                        } else {
                            Log.d(ChickSanityApplication.CHICK_SANITY_MAIN_TAG, "Current time less then expired, use saved url")
                            _chickSanityHomeScreenState.value =
                                ChickSanityHomeScreenState.ChickSanitySuccess(
                                    chickSanitySharedPreference.chickSanitySavedUrl
                                )
                        }
                    } else {
                        _chickSanityHomeScreenState.value =
                            ChickSanityHomeScreenState.ChickSanityNotInternet
                    }
                }
                2 -> {
                    _chickSanityHomeScreenState.value =
                        ChickSanityHomeScreenState.ChickSanityError
                }
            }
        }
    }


    private suspend fun chickSanityGetData(conversation: MutableMap<String, Any>?) {
        val chickSanityData = chickSanityGetAllUseCase.invoke(conversation)
        if (chickSanitySharedPreference.chickSanityAppState == 0) {
            if (chickSanityData == null) {
                chickSanitySharedPreference.chickSanityAppState = 2
                _chickSanityHomeScreenState.value =
                    ChickSanityHomeScreenState.ChickSanityError
            } else {
                chickSanitySharedPreference.chickSanityAppState = 1
                chickSanitySharedPreference.apply {
                    chickSanityExpired = chickSanityData.chickSanityExpires
                    chickSanitySavedUrl = chickSanityData.chickSanityUrl
                }
                _chickSanityHomeScreenState.value =
                    ChickSanityHomeScreenState.ChickSanitySuccess(chickSanityData.chickSanityUrl)
            }
        } else  {
            if (chickSanityData == null) {
                _chickSanityHomeScreenState.value =
                    ChickSanityHomeScreenState.ChickSanitySuccess(chickSanitySharedPreference.chickSanitySavedUrl)
            } else {
                chickSanitySharedPreference.apply {
                    chickSanityExpired = chickSanityData.chickSanityExpires
                    chickSanitySavedUrl = chickSanityData.chickSanityUrl
                }
                _chickSanityHomeScreenState.value =
                    ChickSanityHomeScreenState.ChickSanitySuccess(chickSanityData.chickSanityUrl)
            }
        }
    }


    sealed class ChickSanityHomeScreenState {
        data object ChickSanityLoading : ChickSanityHomeScreenState()
        data object ChickSanityError : ChickSanityHomeScreenState()
        data class ChickSanitySuccess(val data: String) : ChickSanityHomeScreenState()
        data object ChickSanityNotInternet: ChickSanityHomeScreenState()
    }
}