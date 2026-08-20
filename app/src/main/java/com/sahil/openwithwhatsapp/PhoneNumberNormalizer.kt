package com.sahil.openwithwhatsapp

/**
 * Turns whatever a `tel:` link happens to contain into the bare international
 * digit string WhatsApp expects (no `+`, no spaces, no leading zeros).
 *
 * A number that already carries a country code is never rewritten; the default
 * country code is only prepended to numbers that are clearly local.
 */
object PhoneNumberNormalizer {

    /** E.164 allows at most 15 digits; anything shorter than 8 is a short code. */
    private const val MIN_DIGITS = 8
    private const val MAX_DIGITS = 15

    /**
     * A bare number (no `+`, no `00`, no leading `0`) this long is already
     * international -- local subscriber numbers don't reach 11 digits.
     */
    private const val BARE_INTERNATIONAL_LENGTH = 11

    sealed interface Result {
        /** Digits only, ready to drop into a wa.me link. */
        data class Ok(val digits: String) : Result
        data class Invalid(val reason: String) : Result
    }

    fun normalize(raw: String, defaultCountryCode: String): Result {
        // Drop DTMF extensions: "+97142223333,,101" dials an extension we can't use.
        var input = raw.trim()
        val extension = input.indexOfFirst { it == ',' || it == ';' }
        if (extension >= 0) input = input.substring(0, extension)

        // "+971 (0) 50 123 4567" -- the bracketed trunk zero is not part of the number.
        input = input.replace(Regex("""\(\s*0\s*\)"""), "")

        val isExplicitlyInternational = input.startsWith("+")
        val digits = input.filter(Char::isDigit)
        if (digits.isEmpty()) return Result.Invalid("no digits in \"$raw\"")

        val countryCode = defaultCountryCode.filter(Char::isDigit)

        val international = when {
            isExplicitlyInternational -> digits
            digits.startsWith("00") -> digits.drop(2)
            digits.startsWith("0") -> countryCode + digits.drop(1)
            digits.length >= BARE_INTERNATIONAL_LENGTH -> digits
            else -> countryCode + digits
        }

        // "+971 050 123 4567" -- a trunk zero survives the country code in the wild,
        // but E.164 never keeps it.
        val cleaned =
            if (countryCode.isNotEmpty() && international.startsWith("${countryCode}0")) {
                countryCode + international.drop(countryCode.length + 1)
            } else {
                international
            }

        return when {
            cleaned.length < MIN_DIGITS -> Result.Invalid("\"$raw\" is too short to be a mobile number")
            cleaned.length > MAX_DIGITS -> Result.Invalid("\"$raw\" is too long to be a phone number")
            else -> Result.Ok(cleaned)
        }
    }
}
