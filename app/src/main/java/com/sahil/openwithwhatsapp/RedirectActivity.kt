package com.sahil.openwithwhatsapp

import android.app.Activity
import android.content.ActivityNotFoundException
import android.net.Uri
import android.os.Bundle
import android.widget.Toast

/**
 * Invisible. Receives the `tel:` intent from the system chooser, rewrites the
 * number and bounces it to WhatsApp without ever drawing a screen.
 */
class RedirectActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val number = intent?.data?.telNumber()
        if (number.isNullOrBlank()) {
            finishWith(getString(R.string.error_no_number))
            return
        }

        when (val result = PhoneNumberNormalizer.normalize(number, Settings.countryCode(this))) {
            is PhoneNumberNormalizer.Result.Invalid ->
                finishWith(getString(R.string.error_invalid_number, number))

            is PhoneNumberNormalizer.Result.Ok -> {
                val chat = WhatsApp.chatIntent(this, result.digits)
                if (chat == null) {
                    finishWith(getString(R.string.error_no_whatsapp))
                    return
                }
                try {
                    startActivity(chat)
                    finish()
                } catch (e: ActivityNotFoundException) {
                    finishWith(getString(R.string.error_no_whatsapp))
                }
            }
        }
    }

    /** `tel:` is an opaque URI, so the number lives in the scheme-specific part. */
    private fun Uri.telNumber(): String? = schemeSpecificPart ?: path

    private fun finishWith(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
        finish()
    }
}
