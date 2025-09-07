package sysout.openups.controller.service

import io.quarkus.test.InjectMock
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import sysout.openups.controller.dto.SauceDTO
import sysout.openups.controller.entity.Sauce
import sysout.openups.controller.repository.SauceRepository
import java.util.*

@QuarkusTest
class SauceServiceTest {
    @InjectMock
    lateinit var sauceRepository: SauceRepository

    @Inject
    lateinit var sauceService: SauceService

    @Test
    fun `should list all sauces`() {
        val sauces = listOf(
            Sauce(UUID.randomUUID(), "Maionese Caseira", "maionese com limão"),
            Sauce(UUID.randomUUID(), "Mostarda e Mel", "mostarda com mel")
        )
        whenever(sauceRepository.listAll()).thenReturn(sauces)
        val result = sauceService.listAll()
        assertEquals(2, result.size)
        assertEquals("Maionese Caseira", result[0].name)
    }

    @Test
    fun `should filter sauces by flavour`() {
        val sauces = listOf(
            Sauce(UUID.randomUUID(), "Molho de Pimenta", "pimenta malagueta"),
            Sauce(UUID.randomUUID(), "Molho de Alho", "alho assado")
        )
        whenever(sauceRepository.filterSauces("pimenta")).thenReturn(listOf(sauces[0]))
        val result = sauceService.filterSauces("pimenta")
        assertEquals(1, result.size)
        assertEquals("Molho de Pimenta", result[0].name)
    }

    @Test
    fun `should return sauce by id`() {
        val id = UUID.randomUUID()
        val sauce = Sauce(id, "Catupiry", "queijo cremoso")
        whenever(sauceRepository.findById(id)).thenReturn(sauce)
        val result = sauceService.findById(id)
        assertNotNull(result)
        assertEquals("Catupiry", result!!.name)
    }

    @Test
    fun `should return null if sauce does not exist`() {
        val id = UUID.randomUUID()
        whenever(sauceRepository.findById(id)).thenReturn(null)
        val result = sauceService.findById(id)
        assertNull(result)
    }

    @Test
    fun `should add a sauce`() {
        val dto = SauceDTO(null, "Molho Rose", "maionese com catchup")
        val entity = Sauce(UUID.randomUUID(), "Molho Rose", "maionese com catchup")
        whenever(sauceRepository.save(any())).thenReturn(entity)
        val result = sauceService.add(dto)
        assertEquals("Molho Rose", result.name)
    }

    @Test
    fun `should update existing sauce`() {
        val id = UUID.randomUUID()
        val sauce = Sauce(id, "Molho Tártaro", "maionese com pepino")
        val dto = SauceDTO(id, "Molho Tártaro Especial", "maionese com pepino e ervas")
        whenever(sauceRepository.findById(id)).thenReturn(sauce)
        whenever(sauceRepository.update(id, sauce)).thenReturn(sauce)
        val result = sauceService.update(id, dto)
        assertNotNull(result)
    }

    @Test
    fun `should delete existing sauce`() {
        val id = UUID.randomUUID()
        val sauce = Sauce(id, "Molho Apimentado", "pimenta com azeite")
        whenever(sauceRepository.findById(id)).thenReturn(sauce)
        doNothing().whenever(sauceRepository).deleteById(id)
        val result = sauceService.delete(id)
        assertTrue(result)
    }

    @Test
    fun `should return false when delete non-existing sauce`() {
        val id = UUID.randomUUID()
        whenever(sauceRepository.findById(id)).thenReturn(null)
        val result = sauceService.delete(id)
        assertFalse(result)
    }
}
