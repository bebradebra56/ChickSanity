package com.chickisa.sofskil.zxcvb.presentation.ui.view

import android.annotation.SuppressLint
import android.widget.FrameLayout
import androidx.lifecycle.ViewModel

class ChickSanityDataStore : ViewModel(){
    val chickSanityViList: MutableList<ChickSanityVi> = mutableListOf()
    var chickSanityIsFirstCreate = true
    @SuppressLint("StaticFieldLeak")
    lateinit var chickSanityContainerView: FrameLayout
    @SuppressLint("StaticFieldLeak")
    lateinit var chickSanityView: ChickSanityVi

}