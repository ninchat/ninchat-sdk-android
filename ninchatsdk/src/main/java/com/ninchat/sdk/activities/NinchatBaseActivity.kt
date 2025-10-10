package com.ninchat.sdk.activities

import android.Manifest
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.ninchat.sdk.R
import com.ninchat.sdk.utils.display.applySystemBarInsets
import com.ninchat.sdk.utils.misc.Broadcast

abstract class NinchatBaseActivity : AppCompatActivity() {
    @get:LayoutRes
    protected abstract val layoutRes: Int

    protected open fun allowBackButton(): Boolean {
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        if (Build.VERSION.SDK_INT >= 30) {
            window.setDecorFitsSystemWindows(false) // Android 11+
        } else {
            @Suppress("DEPRECATION")
            window.decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_STABLE or
                        View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
        }

        if (Build.VERSION.SDK_INT >= 21) {
            window.statusBarColor = Color.TRANSPARENT
            window.navigationBarColor = Color.TRANSPARENT
        }

        setContentView(layoutRes)
        findViewById<ViewGroup>(android.R.id.content)
            ?.let { it.getChildAt(0) ?: it }
            ?.applySystemBarInsets(
                applyLeft = true,
                applyRight = true
            )

        LocalBroadcastManager.getInstance(applicationContext).run {
            registerReceiver(closeActivityReceiver, IntentFilter(Broadcast.CLOSE_NINCHAT_ACTIVITY))
        }
    }

    override fun onDestroy() {
        LocalBroadcastManager.getInstance(applicationContext).run {
            unregisterReceiver(closeActivityReceiver)
        }
        super.onDestroy()
    }

    protected fun showError(@IdRes layoutId: Int, @StringRes message: Int) {
        findViewById<TextView>(R.id.error_message)?.run {
            setText(message)
        }
        findViewById<View>(R.id.error_close)?.run {
            setOnClickListener {
                findViewById<View>(layoutId)?.run { visibility = View.GONE }
            }
        }
        findViewById<View>(layoutId)?.run {
            applySystemBarInsets(applyLeft = true, applyRight = true, applyBottom = false)
            visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        if (allowBackButton()) {
            super.onBackPressed()
        }
    }

    private val closeActivityReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent?) {
            if (intent?.action == Broadcast.CLOSE_NINCHAT_ACTIVITY) {
                setResult(RESULT_CANCELED, null)
                finish()
            }
        }
    }

    companion object {
        @JvmField
        val STORAGE_PERMISSION_REQUEST_CODE = "ExternalStorage".hashCode() and 0xffff
    }
}
