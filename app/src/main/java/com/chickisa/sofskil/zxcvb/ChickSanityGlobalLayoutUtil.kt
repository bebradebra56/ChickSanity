package com.chickisa.sofskil.zxcvb

import android.app.Activity
import android.graphics.Rect
import android.view.View
import android.widget.FrameLayout
import com.chickisa.sofskil.zxcvb.presentation.app.ChickSanityApplication

class ChickSanityGlobalLayoutUtil {

    private var chickSanityMChildOfContent: View? = null
    private var chickSanityUsableHeightPrevious = 0

    fun chickSanityAssistActivity(activity: Activity) {
        val content = activity.findViewById<FrameLayout>(android.R.id.content)
        chickSanityMChildOfContent = content.getChildAt(0)

        chickSanityMChildOfContent?.viewTreeObserver?.addOnGlobalLayoutListener {
            possiblyResizeChildOfContent(activity)
        }
    }

    private fun possiblyResizeChildOfContent(activity: Activity) {
        val chickSanityUsableHeightNow = chickSanityComputeUsableHeight()
        if (chickSanityUsableHeightNow != chickSanityUsableHeightPrevious) {
            val chickSanityUsableHeightSansKeyboard = chickSanityMChildOfContent?.rootView?.height ?: 0
            val chickSanityHeightDifference = chickSanityUsableHeightSansKeyboard - chickSanityUsableHeightNow

            if (chickSanityHeightDifference > (chickSanityUsableHeightSansKeyboard / 4)) {
                activity.window.setSoftInputMode(ChickSanityApplication.chickSanityInputMode)
            } else {
                activity.window.setSoftInputMode(ChickSanityApplication.chickSanityInputMode)
            }
//            mChildOfContent?.requestLayout()
            chickSanityUsableHeightPrevious = chickSanityUsableHeightNow
        }
    }

    private fun chickSanityComputeUsableHeight(): Int {
        val r = Rect()
        chickSanityMChildOfContent?.getWindowVisibleDisplayFrame(r)
        return r.bottom - r.top  // Visible height без status bar
    }
}