package sysout.openups.controller.util

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Test
import sysout.openups.controller.entity.CaffeineLevel
import sysout.openups.controller.entity.TeaCategory

class EnumConverterTest {

    @Test
    fun `deve converter string válida para enum TeaCategory`() {
        val result = EnumConverter.fromString<TeaCategory>("GREEN")
        assertEquals(TeaCategory.GREEN, result)
    }

    @Test
    fun `deve converter string válida case insensitive para enum TeaCategory`() {
        val result = EnumConverter.fromString<TeaCategory>("green")
        assertEquals(TeaCategory.GREEN, result)
    }

    @Test
    fun `deve retornar null para string inválida ao converter para TeaCategory`() {
        val result = EnumConverter.fromString<TeaCategory>("INVALID_CATEGORY")
        assertNull(result)
    }

    @Test
    fun `deve retornar null para string null ao converter para TeaCategory`() {
        val result = EnumConverter.fromString<TeaCategory>(null)
        assertNull(result)
    }

    @Test
    fun `deve converter string válida para enum CaffeineLevel`() {
        val result = EnumConverter.fromString<CaffeineLevel>("MEDIUM")
        assertEquals(CaffeineLevel.MEDIUM, result)
    }

    @Test
    fun `deve converter string válida case insensitive para enum CaffeineLevel`() {
        val result = EnumConverter.fromString<CaffeineLevel>("medium")
        assertEquals(CaffeineLevel.MEDIUM, result)
    }

    @Test
    fun `deve retornar null para string inválida ao converter para CaffeineLevel`() {
        val result = EnumConverter.fromString<CaffeineLevel>("INVALID_LEVEL")
        assertNull(result)
    }

    @Test
    fun `deve retornar null para string null ao converter para CaffeineLevel`() {
        val result = EnumConverter.fromString<CaffeineLevel>(null)
        assertNull(result)
    }

    @Test
    fun `deve converter string válida para enum usando método não reificado`() {
        val result = EnumConverter.fromString("GREEN", TeaCategory::class.java)
        assertEquals(TeaCategory.GREEN, result)
    }

    @Test
    fun `deve retornar null para string inválida usando método não reificado`() {
        val result = EnumConverter.fromString("INVALID_CATEGORY", TeaCategory::class.java)
        assertNull(result)
    }

    @Test
    fun `deve retornar null para string null usando método não reificado`() {
        val result = EnumConverter.fromString(null, TeaCategory::class.java)
        assertNull(result)
    }
}
