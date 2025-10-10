package com.ninchat.sdk.utils.display

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowInsets
import kotlin.math.max


fun Activity.getScreenHeight(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = windowManager.currentWindowMetrics
        val insets =
            windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
        windowMetrics.bounds.height() - insets.top - insets.bottom
    } else {
        val displayMetrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.heightPixels
    }
}


fun Activity.getScreenWidth(): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        val windowMetrics = windowManager.currentWindowMetrics
        val insets =
            windowMetrics.windowInsets.getInsetsIgnoringVisibility(WindowInsets.Type.systemBars())
        windowMetrics.bounds.width() - insets.left - insets.right
    } else {
        val displayMetrics = DisplayMetrics()
        @Suppress("DEPRECATION")
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        displayMetrics.widthPixels
    }
}


fun Activity.isPortrait(): Boolean {
    return resources.configuration.orientation == Configuration.ORIENTATION_PORTRAIT
}

fun Activity.getStatusBarHeight(): Int {
    var result = 0
    val resourceId = resources.getIdentifier("status_bar_height", "dimen", "android")
    if (resourceId > 0) {
        result = resources.getDimensionPixelSize(resourceId)
    }
    return result
}

fun View.applySystemBarInsets(
    applyLeft: Boolean = false,
    applyTop: Boolean = true,
    applyRight: Boolean = false,
    applyBottom: Boolean = true
) {
    val startP = intArrayOf(paddingLeft, paddingTop, paddingRight, paddingBottom)
    setOnApplyWindowInsetsListener { v, insets ->
        val (leftInset, topInset, rightInset, bottomInset) =
            if (Build.VERSION.SDK_INT >= 30) {
                val systemBars = insets.getInsets(WindowInsets.Type.systemBars())
                val ime = insets.getInsets(WindowInsets.Type.ime())
                intArrayOf(systemBars.left, systemBars.top, systemBars.right, max(systemBars.bottom, ime.bottom))
            } else {
                @Suppress("DEPRECATION")
                intArrayOf(
                    insets.systemWindowInsetLeft,
                    insets.systemWindowInsetTop,
                    insets.systemWindowInsetRight,
                    insets.systemWindowInsetBottom
                )
            }

        val effectiveLeft = if (applyLeft) leftInset else 0
        val effectiveTop = if (applyTop) topInset else 0
        val effectiveRight = if (applyRight) rightInset else 0
        val effectiveBottom = if (applyBottom) bottomInset else 0

        v.setPadding(
            startP[0] + effectiveLeft,
            startP[1] + effectiveTop,
            startP[2] + effectiveRight,
            startP[3] + effectiveBottom
        )

        @Suppress("DEPRECATION")
        insets
    }
    requestApplyInsets()
}
