package sysout.openups.controller.service

import io.quarkus.test.InjectMock
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import sysout.openups.controller.dto.TeaDTO
import sysout.openups.controller.entity.Tea
import sysout.openups.controller.repository.TeaRepository
import java.util.*

@QuarkusTest
class TeaServiceTest {
    @InjectMock
    lateinit var teaRepository: TeaRepository

    @Inject
    lateinit var teaService: TeaService

    @Test
    fun `deve listar todos os teas`() {
        val teas = listOf(
            Tea(UUID.randomUUID(), "Sencha", "japan", "Chá verde", "green", "MEDIUM"),
            Tea(UUID.randomUUID(), "Earl Grey", "england", "Chá preto", "black", "HIGH")
        )
        whenever(teaRepository.listAll()).thenReturn(teas)
        val result = teaService.listAll()
        assertEquals(2, result.size)
        assertTrue(result.any { it.category == "green" })
    }

    @Test
    fun `deve filtrar teas por category, caffeineLevel e origin`() {
        val teas = listOf(
            Tea(UUID.randomUUID(), "Sencha", "japan", "Chá verde", "green", "MEDIUM"),
            Tea(UUID.randomUUID(), "Earl Grey", "england", "Chá preto", "black", "HIGH")
        )
        whenever(teaRepository.filterTeas("green", "MEDIUM", "japan")).thenReturn(teas.filter {
            it.category == "green" && it.caffeineLevel == "MEDIUM" && it.origin == "japan"
        })
        val result = teaService.filterTeas("green", "MEDIUM", "japan")
        assertEquals(1, result.size)
        assertEquals("Sencha", result[0].name)
    }

    @Test
    fun `deve retornar tea por id`() {
        val id = UUID.randomUUID()
        val tea = Tea(id, "Sencha", "japan", "Chá verde", "green", "MEDIUM")
        whenever(teaRepository.findById(id)).thenReturn(tea)
        val result = teaService.findById(id)
        assertNotNull(result)
        assertEquals("Sencha", result!!.name)
    }

    @Test
    fun `deve retornar null se id nao existir`() {
        val id = UUID.randomUUID()
        whenever(teaRepository.findById(id)).thenReturn(null)
        val result = teaService.findById(id)
        assertNull(result)
    }

    @Test
    fun `deve adicionar tea`() {
        val dto = TeaDTO(null, "Sencha", "japan", "Chá verde", "green", "MEDIUM")
        val entity = Tea(UUID.randomUUID(), "Sencha", "japan", "Chá verde", "green", "MEDIUM")
        whenever(teaRepository.save(any())).thenReturn(entity)
        val result = teaService.add(dto)
        assertEquals("Sencha", result.name)
    }

    @Test
    fun `deve atualizar tea existente`() {
        val id = UUID.randomUUID()
        val entity = Tea(id, "Sencha", "japan", "Chá verde", "green", "MEDIUM")
        val dto = TeaDTO(id, "Sencha Atualizado", "china", "Chá verde chinês", "green", "LOW")
        whenever(teaRepository.findById(id)).thenReturn(entity)
        whenever(teaRepository.update(id, entity)).thenReturn(entity)
        val result = teaService.update(id, dto)
        assertNotNull(result)
    }

    @Test
    fun `deve retornar null ao atualizar tea inexistente`() {
        val id = UUID.randomUUID()
        val dto = TeaDTO(id, "Sencha Atualizado", "china", "Chá verde chinês", "green", "LOW")
        whenever(teaRepository.findById(id)).thenReturn(null)
        val result = teaService.update(id, dto)
        assertNull(result)
    }

    @Test
    fun `deve deletar tea existente`() {
        val id = UUID.randomUUID()
        whenever(teaRepository.findById(id)).thenReturn(Tea(id, "Sencha", "japan", "Chá verde", "green", "MEDIUM"))
        doNothing().whenever(teaRepository).deleteById(id)
        val result = teaService.delete(id)
        assertTrue(result)
    }

    @Test
    fun `deve retornar false ao deletar tea inexistente`() {
        val id = UUID.randomUUID()
        whenever(teaRepository.findById(id)).thenReturn(null)
        val result = teaService.delete(id)
        assertFalse(result)
    }
}
