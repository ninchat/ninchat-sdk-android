package com.ninchat.sdk.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.IdRes
import androidx.annotation.LayoutRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.ninchat.sdk.R
import com.ninchat.sdk.utils.display.applySystemBarMargins
import com.ninchat.sdk.utils.display.applySystemBarPadding
import com.ninchat.sdk.utils.misc.Broadcast

abstract class NinchatBaseActivity : AppCompatActivity() {
    @get:LayoutRes
    protected abstract val layoutRes: Int

    protected open val edgeToEdgeInsets: List<EdgeToEdgeInset> = emptyList()

    protected open fun allowBackButton(): Boolean {
        return false
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(layoutRes)
        applyEdgeToEdgeInsets()
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
        findViewById<View>(layoutId)?.run { visibility = View.VISIBLE }
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

    private fun applyEdgeToEdgeInsets() {
        edgeToEdgeInsets.forEach { inset ->
            val targetView = findViewById<View>(inset.viewId) ?: return@forEach
            if (inset.paddingLeft || inset.paddingTop || inset.paddingRight || inset.paddingBottom) {
                targetView.applySystemBarPadding(
                    applyLeft = inset.paddingLeft,
                    applyTop = inset.paddingTop,
                    applyRight = inset.paddingRight,
                    applyBottom = inset.paddingBottom,
                    types = inset.types,
                )
            }
            if (inset.marginLeft || inset.marginTop || inset.marginRight || inset.marginBottom) {
                targetView.applySystemBarMargins(
                    applyLeft = inset.marginLeft,
                    applyTop = inset.marginTop,
                    applyRight = inset.marginRight,
                    applyBottom = inset.marginBottom,
                    types = inset.types,
                )
            }
        }
    }

    companion object {
        @JvmField
        val STORAGE_PERMISSION_REQUEST_CODE = "ExternalStorage".hashCode() and 0xffff
    }
}

data class EdgeToEdgeInset(
    @IdRes val viewId: Int,
    val paddingLeft: Boolean = false,
    val paddingTop: Boolean = false,
    val paddingRight: Boolean = false,
    val paddingBottom: Boolean = false,
    val marginLeft: Boolean = false,
    val marginTop: Boolean = false,
    val marginRight: Boolean = false,
    val marginBottom: Boolean = false,
    val types: Int = WindowInsetsCompat.Type.systemBars(),
)
