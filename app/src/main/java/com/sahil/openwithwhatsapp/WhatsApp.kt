package com.sahil.openwithwhatsapp

import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri

/** Hands a normalized number to WhatsApp, preferring the app over the browser. */
object WhatsApp {

    /** Consumer WhatsApp first, then WhatsApp Business. Must match <queries> in the manifest. */
    private val PACKAGES = listOf("com.whatsapp", "com.whatsapp.w4b")

    fun chatIntent(context: Context, digits: String): Intent? {
        val uri = Uri.parse("https://wa.me/$digits")
        val installed = PACKAGES.firstOrNull { context.isInstalled(it) }

        val intent = Intent(Intent.ACTION_VIEW, uri).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            if (installed != null) setPackage(installed)
        }

        // With no WhatsApp installed we fall back to a browser, which wa.me redirects
        // to the install page -- but only if something can actually open a link.
        return if (intent.resolveActivity(context.packageManager) != null) intent else null
    }

    private fun Context.isInstalled(packageName: String): Boolean =
        try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
}
