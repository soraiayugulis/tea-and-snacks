package sysout.openups.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import sysout.openups.product.entity.CaffeineLevel
import sysout.openups.product.entity.TeaCategory

class EnumConverterTest {

    @Test
    fun `should convert valid string to TeaCategory enum`() {
        val result = EnumConverter.fromString<TeaCategory>("GREEN")
        assertEquals(TeaCategory.GREEN, result)
    }

    @Test
    fun `should convert valid case insensitive string to TeaCategory enum`() {
        val result = EnumConverter.fromString<TeaCategory>("green")
        assertEquals(TeaCategory.GREEN, result)
    }

    @Test
    fun `should return null for invalid string when converting to TeaCategory`() {
        val result = EnumConverter.fromString<TeaCategory>("INVALID_CATEGORY")
        assertNull(result)
    }

    @Test
    fun `should return null for null string when converting to TeaCategory`() {
        val result = EnumConverter.fromString<TeaCategory>(null)
        assertNull(result)
    }

    @Test
    fun `should convert valid string to CaffeineLevel enum`() {
        val result = EnumConverter.fromString<CaffeineLevel>("MEDIUM")
        assertEquals(CaffeineLevel.MEDIUM, result)
    }

    @Test
    fun `should convert valid case insensitive string to CaffeineLevel enum`() {
        val result = EnumConverter.fromString<CaffeineLevel>("medium")
        assertEquals(CaffeineLevel.MEDIUM, result)
    }

    @Test
    fun `should return null for invalid string when converting to CaffeineLevel`() {
        val result = EnumConverter.fromString<CaffeineLevel>("INVALID_LEVEL")
        assertNull(result)
    }

    @Test
    fun `should return null for null string when converting to CaffeineLevel`() {
        val result = EnumConverter.fromString<CaffeineLevel>(null)
        assertNull(result)
    }

    @Test
    fun `should convert valid string to enum using non reified method`() {
        val result = EnumConverter.fromString("GREEN", TeaCategory::class.java)
        assertEquals(TeaCategory.GREEN, result)
    }

    @Test
    fun `should return null for invalid string using non reified method`() {
        val result = EnumConverter.fromString("INVALID_CATEGORY", TeaCategory::class.java)
        assertNull(result)
    }

    @Test
    fun `should return null for null string using non reified method`() {
        val result = EnumConverter.fromString(null, TeaCategory::class.java)
        assertNull(result)
    }
}
