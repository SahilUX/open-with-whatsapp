package com.sahil.openwithwhatsapp

import android.content.Context

/** The one user-facing preference: which country code local numbers belong to. */
object Settings {

    const val DEFAULT_COUNTRY_CODE = "971"

    private const val PREFS = "open_with_whatsapp"
    private const val KEY_COUNTRY_CODE = "country_code"

    fun countryCode(context: Context): String =
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .getString(KEY_COUNTRY_CODE, DEFAULT_COUNTRY_CODE)
            ?.takeIf { it.isNotBlank() }
            ?: DEFAULT_COUNTRY_CODE

    fun setCountryCode(context: Context, value: String) {
        val digits = value.filter(Char::isDigit).ifEmpty { DEFAULT_COUNTRY_CODE }
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_COUNTRY_CODE, digits)
            .apply()
    }
}
