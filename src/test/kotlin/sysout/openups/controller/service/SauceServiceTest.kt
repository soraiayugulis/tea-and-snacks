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
    fun `deve listar todos os sauces`() {
        val sauces = listOf(
            Sauce(UUID.randomUUID(), "Barbecue", "barbecue"),
            Sauce(UUID.randomUUID(), "Cheese", "cheese")
        )
        whenever(sauceRepository.listAll()).thenReturn(sauces)
        val result = sauceService.listAll()
        assertEquals(2, result.size)
        assertTrue(result.any { it.flavour == "barbecue" })
    }

    @Test
    fun `deve filtrar sauces por flavour`() {
        val sauces = listOf(
            Sauce(UUID.randomUUID(), "Barbecue", "barbecue"),
            Sauce(UUID.randomUUID(), "Cheese", "cheese")
        )
        whenever(sauceRepository.filterSauces("cheese")).thenReturn(sauces.filter { it.flavour == "cheese" })
        val result = sauceService.filterSauces("cheese")
        assertEquals(1, result.size)
        assertEquals("cheese", result[0].flavour)
    }

    @Test
    fun `deve retornar sauce por id`() {
        val id = UUID.randomUUID()
        val sauce = Sauce(id, "Barbecue", "barbecue")
        whenever(sauceRepository.findById(id)).thenReturn(sauce)
        val result = sauceService.findById(id)
        assertNotNull(result)
        assertEquals("barbecue", result!!.flavour)
    }

    @Test
    fun `deve retornar null se id nao existir`() {
        val id = UUID.randomUUID()
        whenever(sauceRepository.findById(id)).thenReturn(null)
        val result = sauceService.findById(id)
        assertNull(result)
    }

    @Test
    fun `deve adicionar sauce`() {
        val dto = SauceDTO(null, "Barbecue", "barbecue")
        val entity = Sauce(UUID.randomUUID(), "Barbecue", "barbecue")
        whenever(sauceRepository.save(any())).thenReturn(entity)
        val result = sauceService.add(dto)
        assertEquals("barbecue", result.flavour)
    }

    @Test
    fun `deve atualizar sauce existente`() {
        val id = UUID.randomUUID()
        val entity = Sauce(id, "Barbecue", "barbecue")
        val dto = SauceDTO(id, "Barbecue Updated", "spicy")
        whenever(sauceRepository.findById(id)).thenReturn(entity)
        whenever(sauceRepository.update(id, entity)).thenReturn(entity)
        val result = sauceService.update(id, dto)
        assertNotNull(result)
    }

    @Test
    fun `deve retornar null ao atualizar sauce inexistente`() {
        val id = UUID.randomUUID()
        val dto = SauceDTO(id, "Barbecue Updated", "spicy")
        whenever(sauceRepository.findById(id)).thenReturn(null)
        val result = sauceService.update(id, dto)
        assertNull(result)
    }

    @Test
    fun `deve deletar sauce existente`() {
        val id = UUID.randomUUID()
        whenever(sauceRepository.findById(id)).thenReturn(Sauce(id, "Barbecue", "barbecue"))
        doNothing().whenever(sauceRepository).deleteById(id)
        val result = sauceService.delete(id)
        assertTrue(result)
    }

    @Test
    fun `deve retornar false ao deletar sauce inexistente`() {
        val id = UUID.randomUUID()
        whenever(sauceRepository.findById(id)).thenReturn(null)
        val result = sauceService.delete(id)
        assertFalse(result)
    }
}
