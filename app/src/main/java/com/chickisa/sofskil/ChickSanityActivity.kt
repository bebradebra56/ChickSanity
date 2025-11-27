package com.chickisa.sofskil

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.chickisa.sofskil.zxcvb.ChickSanityGlobalLayoutUtil
import com.chickisa.sofskil.zxcvb.chickSanitySetupSystemBars
import com.chickisa.sofskil.zxcvb.presentation.app.ChickSanityApplication
import com.chickisa.sofskil.zxcvb.presentation.pushhandler.ChickSanityPushHandler
import org.koin.android.ext.android.inject

class ChickSanityActivity : AppCompatActivity() {

    private val chickSanityPushHandler by inject<ChickSanityPushHandler>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chickSanitySetupSystemBars()
        WindowCompat.setDecorFitsSystemWindows(window, false)

        setContentView(R.layout.activity_chick_sanity)

        val chickSanityRootView = findViewById<View>(android.R.id.content)
        ChickSanityGlobalLayoutUtil().chickSanityAssistActivity(this)
        ViewCompat.setOnApplyWindowInsetsListener(chickSanityRootView) { chickSanityView, chickSanityInsets ->
            val chickSanitySystemBars = chickSanityInsets.getInsets(WindowInsetsCompat.Type.systemBars())
            val chickSanityDisplayCutout = chickSanityInsets.getInsets(WindowInsetsCompat.Type.displayCutout())
            val chickSanityIme = chickSanityInsets.getInsets(WindowInsetsCompat.Type.ime())


            val chickSanityTopPadding = maxOf(chickSanitySystemBars.top, chickSanityDisplayCutout.top)
            val chickSanityLeftPadding = maxOf(chickSanitySystemBars.left, chickSanityDisplayCutout.left)
            val chickSanityRightPadding = maxOf(chickSanitySystemBars.right, chickSanityDisplayCutout.right)
            window.setSoftInputMode(ChickSanityApplication.chickSanityInputMode)

            if (window.attributes.softInputMode == WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN) {
                Log.d(ChickSanityApplication.CHICK_SANITY_MAIN_TAG, "ADJUST PUN")
                val chickSanityBottomInset = maxOf(chickSanitySystemBars.bottom, chickSanityDisplayCutout.bottom)

                chickSanityView.setPadding(chickSanityLeftPadding, chickSanityTopPadding, chickSanityRightPadding, 0)

                chickSanityView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = chickSanityBottomInset
                }
            } else {
                Log.d(ChickSanityApplication.CHICK_SANITY_MAIN_TAG, "ADJUST RESIZE")

                val chickSanityBottomInset = maxOf(chickSanitySystemBars.bottom, chickSanityDisplayCutout.bottom, chickSanityIme.bottom)

                chickSanityView.setPadding(chickSanityLeftPadding, chickSanityTopPadding, chickSanityRightPadding, 0)

                chickSanityView.updateLayoutParams<ViewGroup.MarginLayoutParams> {
                    bottomMargin = chickSanityBottomInset
                }
            }



            WindowInsetsCompat.CONSUMED
        }
        Log.d(ChickSanityApplication.CHICK_SANITY_MAIN_TAG, "Activity onCreate()")
        chickSanityPushHandler.chickSanityHandlePush(intent.extras)
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            chickSanitySetupSystemBars()
        }
    }

    override fun onResume() {
        super.onResume()
        chickSanitySetupSystemBars()
    }
}