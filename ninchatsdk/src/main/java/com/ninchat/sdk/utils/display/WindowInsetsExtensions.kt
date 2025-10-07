package com.ninchat.sdk.utils.display

import android.view.View
import android.view.ViewGroup
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updatePadding

private data class InitialPadding(val left: Int, val top: Int, val right: Int, val bottom: Int)
private data class InitialMargin(val left: Int, val top: Int, val right: Int, val bottom: Int)

fun View.applySystemBarPadding(
    applyLeft: Boolean = false,
    applyTop: Boolean = false,
    applyRight: Boolean = false,
    applyBottom: Boolean = false,
) {
    val initial = InitialPadding(paddingLeft, paddingTop, paddingRight, paddingBottom)
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val systemBars = insets.systemBarInsetsCompat()
        view.updatePadding(
            left = if (applyLeft) initial.left + systemBars.left else initial.left,
            top = if (applyTop) initial.top + systemBars.top else initial.top,
            right = if (applyRight) initial.right + systemBars.right else initial.right,
            bottom = if (applyBottom) initial.bottom + systemBars.bottom else initial.bottom,
        )
        insets
    }
    requestApplyInsetsWhenAttached()
}

fun View.applySystemBarMargins(
    applyLeft: Boolean = false,
    applyTop: Boolean = false,
    applyRight: Boolean = false,
    applyBottom: Boolean = false,
) {
    val layoutParams = layoutParams as? ViewGroup.MarginLayoutParams ?: return
    val initial = InitialMargin(
        left = layoutParams.leftMargin,
        top = layoutParams.topMargin,
        right = layoutParams.rightMargin,
        bottom = layoutParams.bottomMargin,
    )
    ViewCompat.setOnApplyWindowInsetsListener(this) { view, insets ->
        val marginParams = view.layoutParams as? ViewGroup.MarginLayoutParams
            ?: return@setOnApplyWindowInsetsListener insets
        val systemBars = insets.systemBarInsetsCompat()
        marginParams.leftMargin =
            if (applyLeft) initial.left + systemBars.left else initial.left
        marginParams.topMargin =
            if (applyTop) initial.top + systemBars.top else initial.top
        marginParams.rightMargin =
            if (applyRight) initial.right + systemBars.right else initial.right
        marginParams.bottomMargin =
            if (applyBottom) initial.bottom + systemBars.bottom else initial.bottom
        view.layoutParams = marginParams
        insets
    }
    requestApplyInsetsWhenAttached()
}

private data class SystemBarInsets(
    val left: Int,
    val top: Int,
    val right: Int,
    val bottom: Int,
)

@Suppress("DEPRECATION")
private fun WindowInsetsCompat.systemBarInsetsCompat(): SystemBarInsets {
    return SystemBarInsets(
        left = systemWindowInsetLeft,
        top = systemWindowInsetTop,
        right = systemWindowInsetRight,
        bottom = systemWindowInsetBottom,
    )
}

private fun View.requestApplyInsetsWhenAttached() {
    if (isAttachedToWindow) {
        requestApplyInsets()
    } else {
        addOnAttachStateChangeListener(object : View.OnAttachStateChangeListener {
            override fun onViewAttachedToWindow(v: View) {
                v.removeOnAttachStateChangeListener(this)
                v.requestApplyInsets()
            }

            override fun onViewDetachedFromWindow(v: View) = Unit
        })
    }
}
