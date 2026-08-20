package com.sahil.openwithwhatsapp

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class PhoneNumberNormalizerTest {

    private fun normalize(raw: String, code: String = "971") =
        PhoneNumberNormalizer.normalize(raw, code)

    private fun digitsOf(raw: String, code: String = "971"): String {
        val result = normalize(raw, code)
        assertTrue("expected $raw to normalize, got $result", result is PhoneNumberNormalizer.Result.Ok)
        return (result as PhoneNumberNormalizer.Result.Ok).digits
    }

    private fun assertInvalid(raw: String) {
        assertTrue("expected $raw to be rejected", normalize(raw) is PhoneNumberNormalizer.Result.Invalid)
    }

    @Test
    fun `local number gets the default country code`() {
        assertEquals("971501234567", digitsOf("0501234567"))
        assertEquals("971501234567", digitsOf("050 123 4567"))
        assertEquals("971501234567", digitsOf("050-123-4567"))
    }

    @Test
    fun `bare subscriber number gets the default country code`() {
        assertEquals("971501234567", digitsOf("501234567"))
    }

    @Test
    fun `existing country code is kept`() {
        assertEquals("971501234567", digitsOf("+971501234567"))
        assertEquals("971501234567", digitsOf("+971 50 123 4567"))
        assertEquals("971501234567", digitsOf("00971501234567"))
        assertEquals("971501234567", digitsOf("971501234567"))
    }

    @Test
    fun `foreign number is never rewritten to the default country code`() {
        assertEquals("447911123456", digitsOf("+44 7911 123456"))
        assertEquals("447911123456", digitsOf("0044 7911 123456"))
        // No plus sign, but too long to be a local number.
        assertEquals("447911123456", digitsOf("447911123456"))
        assertEquals("14155552671", digitsOf("+1 415 555 2671"))
    }

    @Test
    fun `trunk zero after the country code is dropped`() {
        assertEquals("971501234567", digitsOf("+971 (0) 50 123 4567"))
        assertEquals("971501234567", digitsOf("+971 050 123 4567"))
    }

    @Test
    fun `dtmf extension is stripped`() {
        assertEquals("97142223333", digitsOf("+971 4 222 3333,,101"))
        assertEquals("97142223333", digitsOf("+971 4 222 3333;101"))
    }

    @Test
    fun `country code is configurable`() {
        assertEquals("919876543210", digitsOf("09876543210", code = "91"))
        assertEquals("6591234567", digitsOf("91234567", code = "65"))
    }

    @Test
    fun `short codes and junk are rejected`() {
        assertInvalid("999")
        assertInvalid("*100#")
        assertInvalid("800")
        assertInvalid("")
        assertInvalid("not a number")
    }

    @Test
    fun `over-long input is rejected`() {
        assertInvalid("+1234567890123456789")
    }
}
