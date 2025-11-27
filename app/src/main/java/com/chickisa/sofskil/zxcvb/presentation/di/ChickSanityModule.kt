package com.chickisa.sofskil.zxcvb.presentation.di

import com.chickisa.sofskil.zxcvb.data.repo.ChickSanityRepository
import com.chickisa.sofskil.zxcvb.data.shar.ChickSanitySharedPreference
import com.chickisa.sofskil.zxcvb.data.utils.ChickSanityPushToken
import com.chickisa.sofskil.zxcvb.data.utils.ChickSanitySystemService
import com.chickisa.sofskil.zxcvb.domain.usecases.ChickSanityGetAllUseCase
import com.chickisa.sofskil.zxcvb.presentation.pushhandler.ChickSanityPushHandler
import com.chickisa.sofskil.zxcvb.presentation.ui.load.ChickSanityLoadViewModel
import com.chickisa.sofskil.zxcvb.presentation.ui.view.ChickSanityViFun
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

val chickSanityModule = module {
    factory {
        ChickSanityPushHandler()
    }
    single {
        ChickSanityRepository()
    }
    single {
        ChickSanitySharedPreference(get())
    }
    factory {
        ChickSanityPushToken()
    }
    factory {
        ChickSanitySystemService(get())
    }
    factory {
        ChickSanityGetAllUseCase(
            get(), get(), get()
        )
    }
    factory {
        ChickSanityViFun(get())
    }
    viewModel {
        ChickSanityLoadViewModel(get(), get(), get())
    }
}