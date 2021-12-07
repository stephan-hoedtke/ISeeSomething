package com.stho.isee.encryption.ui

import android.app.AlertDialog
import android.app.KeyguardManager
import android.content.Context
import androidx.viewbinding.BuildConfig
import com.stho.isee.R
import kotlin.system.exitProcess

import android.content.Intent
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity


/**
 * use in Activity.onStart():
 *      if (!systemServices.isDeviceSecure()) {
 *         deviceSecurityAlert = systemServices.showDeviceSecurityAlert()
 *      }
 */
class SystemServices(private var activity: AppCompatActivity) {

    private val keyguardManager = activity.getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager

    val isDeviceSecure: Boolean
        get() = keyguardManager.isDeviceSecure

    val isDeviceNotSecure: Boolean
        get() = !keyguardManager.isDeviceSecure

    // Used to block application if no lock screen is setup.
    fun showDeviceSecurityAlert(): AlertDialog {
        return AlertDialog.Builder(activity)
            .setTitle(R.string.lock_alert_title)
            .setMessage(R.string.lock_alert_body)
            .setPositiveButton(R.string.lock_alert_settings) { _, _ -> activity.openLockScreenSettings() }
            .setNegativeButton(R.string.lock_alert_exit) { _, _ -> exitProcess(0) }
            .setCancelable(BuildConfig.DEBUG)
            .show()
    }
}

private fun Context.openLockScreenSettings() =
    startActivity(Intent(Settings.ACTION_SECURITY_SETTINGS))
