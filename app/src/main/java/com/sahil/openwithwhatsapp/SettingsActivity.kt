package com.sahil.openwithwhatsapp

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

/**
 * The launcher screen. Explains how the app is used and lets you change the
 * country code that local numbers are assumed to belong to.
 */
class SettingsActivity : AppCompatActivity() {

    private lateinit var countryCodeField: EditText
    private lateinit var testField: EditText
    private lateinit var testResult: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        countryCodeField = findViewById(R.id.country_code)
        testField = findViewById(R.id.test_number)
        testResult = findViewById(R.id.test_result)

        countryCodeField.setText(Settings.countryCode(this))

        findViewById<Button>(R.id.save).setOnClickListener {
            Settings.setCountryCode(this, countryCodeField.text.toString())
            val saved = Settings.countryCode(this)
            countryCodeField.setText(saved)
            Toast.makeText(this, getString(R.string.saved, saved), Toast.LENGTH_SHORT).show()
        }

        findViewById<Button>(R.id.test).setOnClickListener { showPreview() }
    }

    /** Runs a number through the real normalizer so you can see what would happen. */
    private fun showPreview() {
        val input = testField.text.toString()
        if (input.isBlank()) {
            testResult.text = ""
            return
        }
        // Preview against what is typed, not what was last saved.
        val code = countryCodeField.text.toString().filter(Char::isDigit)
            .ifEmpty { Settings.DEFAULT_COUNTRY_CODE }

        testResult.text = when (val result = PhoneNumberNormalizer.normalize(input, code)) {
            is PhoneNumberNormalizer.Result.Ok -> getString(R.string.test_ok, result.digits)
            is PhoneNumberNormalizer.Result.Invalid -> getString(R.string.test_failed, result.reason)
        }
    }
}
