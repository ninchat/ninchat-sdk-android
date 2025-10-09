package com.ninchat.sdk.utils.display

import android.app.Activity
import android.content.res.Configuration
import android.os.Build
import android.util.DisplayMetrics
import android.view.View
import android.view.WindowInsets
import kotlinx.android.synthetic.main.activity_ninchat_chat.*
import kotlinx.android.synthetic.main.activity_ninchat_chat.view.*


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
        if (Build.VERSION.SDK_INT >= 30) {
            val bars = insets.getInsets(WindowInsets.Type.systemBars())
            v.setPadding(
                startP[0] + if (applyLeft) bars.left else 0,
                startP[1] + if (applyTop) bars.top else 0,
                startP[2] + if (applyRight) bars.right else 0,
                startP[3] + if (applyBottom) bars.bottom else 0
            )
            insets
        } else {
            @Suppress("DEPRECATION")
            v.setPadding(
                startP[0] + if (applyLeft) insets.systemWindowInsetLeft else 0,
                startP[1] + if (applyTop) insets.systemWindowInsetTop else 0,
                startP[2] + if (applyRight) insets.systemWindowInsetRight else 0,
                startP[3] + if (applyBottom) insets.systemWindowInsetBottom else 0
            )
            @Suppress("DEPRECATION") insets
        }
    }
    requestApplyInsets()
}

