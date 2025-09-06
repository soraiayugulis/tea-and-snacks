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
import sysout.openups.controller.entity.CaffeineLevel
import sysout.openups.controller.entity.Tea
import sysout.openups.controller.entity.TeaCategory
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
            Tea(UUID.randomUUID(), "Sencha", "japan", "Chá verde", TeaCategory.GREEN, CaffeineLevel.MEDIUM),
            Tea(UUID.randomUUID(), "Earl Grey", "england", "Chá preto", TeaCategory.BLACK, CaffeineLevel.HIGH)
        )
        whenever(teaRepository.listAll()).thenReturn(teas)
        val result = teaService.listAll()
        assertEquals(2, result.size)
        assertTrue(result.any { it.category == TeaCategory.GREEN })
    }

    @Test
    fun `deve filtrar teas por category, caffeineLevel e origin`() {
        val teas = listOf(
            Tea(UUID.randomUUID(), "Sencha", "japan", "Chá verde", TeaCategory.GREEN, CaffeineLevel.MEDIUM),
            Tea(UUID.randomUUID(), "Earl Grey", "england", "Chá preto", TeaCategory.BLACK, CaffeineLevel.HIGH)
        )
        whenever(teaRepository.filterTeas(TeaCategory.GREEN, CaffeineLevel.MEDIUM, "japan")).thenReturn(teas.filter {
            it.category == TeaCategory.GREEN && it.caffeineLevel == CaffeineLevel.MEDIUM && it.origin == "japan"
        })
        val result = teaService.filterTeas("GREEN", "MEDIUM", "japan")
        assertEquals(1, result.size)
        assertEquals("Sencha", result[0].name)
    }

    @Test
    fun `deve retornar tea por id`() {
        val id = UUID.randomUUID()
        val tea = Tea(id, "Sencha", "japan", "Chá verde", TeaCategory.GREEN, CaffeineLevel.MEDIUM)
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
        val dto = TeaDTO(null, "Sencha", "japan", "Chá verde", TeaCategory.GREEN, CaffeineLevel.MEDIUM)
        val entity = Tea(UUID.randomUUID(), "Sencha", "japan", "Chá verde", TeaCategory.GREEN, CaffeineLevel.MEDIUM)
        whenever(teaRepository.save(any())).thenReturn(entity)
        val result = teaService.add(dto)
        assertEquals("Sencha", result.name)
    }

    @Test
    fun `deve atualizar tea existente`() {
        val id = UUID.randomUUID()
        val entity = Tea(id, "Sencha", "japan", "Chá verde", TeaCategory.GREEN, CaffeineLevel.MEDIUM)
        val dto = TeaDTO(id, "Sencha Atualizado", "china", "Chá verde chinês", TeaCategory.GREEN, CaffeineLevel.LOW)
        whenever(teaRepository.findById(id)).thenReturn(entity)
        whenever(teaRepository.update(id, entity)).thenReturn(entity)
        val result = teaService.update(id, dto)
        assertNotNull(result)
    }

    @Test
    fun `deve retornar null ao atualizar tea inexistente`() {
        val id = UUID.randomUUID()
        val dto = TeaDTO(id, "Sencha Atualizado", "china", "Chá verde chinês", TeaCategory.GREEN, CaffeineLevel.LOW)
        whenever(teaRepository.findById(id)).thenReturn(null)
        val result = teaService.update(id, dto)
        assertNull(result)
    }

    @Test
    fun `deve deletar tea existente`() {
        val id = UUID.randomUUID()
        val tea = Tea(id, "Sencha", "japan", "Chá verde", TeaCategory.GREEN, CaffeineLevel.MEDIUM)
        whenever(teaRepository.findById(id)).thenReturn(tea)
        doNothing().`when`(teaRepository).deleteById(id)
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

    @Test
    fun `deve deletar todos os teas quando nenhum filtro é fornecido`() {
        val teas = listOf(
            Tea(UUID.randomUUID(), "Sencha", "japan", "Chá verde", TeaCategory.GREEN, CaffeineLevel.MEDIUM),
            Tea(UUID.randomUUID(), "Earl Grey", "england", "Chá preto", TeaCategory.BLACK, CaffeineLevel.HIGH)
        )
        whenever(teaRepository.listAll()).thenReturn(teas)
        doNothing().`when`(teaRepository).deleteAll()
        val result = teaService.deleteAll(null, null, null)
        assertEquals(2, result)
    }

    @Test
    fun `deve deletar teas filtrados por categoria`() {
        val teas = listOf(
            Tea(UUID.randomUUID(), "Sencha", "japan", "Chá verde", TeaCategory.GREEN, CaffeineLevel.MEDIUM),
            Tea(UUID.randomUUID(), "Matcha", "japan", "Chá verde em pó", TeaCategory.GREEN, CaffeineLevel.HIGH)
        )
        whenever(teaRepository.filterTeas(TeaCategory.GREEN, null, null)).thenReturn(teas)

        // Setup mocks for each tea ID
        teas.forEach { tea ->
            tea.id?.let { doNothing().`when`(teaRepository).deleteById(it) }
        }

        val result = teaService.deleteAll(TeaCategory.GREEN, null, null)
        assertEquals(2, result)
    }

    @Test
    fun `deve deletar teas filtrados por nível de cafeína`() {
        val teas = listOf(
            Tea(UUID.randomUUID(), "Sencha", "japan", "Chá verde", TeaCategory.GREEN, CaffeineLevel.MEDIUM),
            Tea(UUID.randomUUID(), "Hojicha", "japan", "Chá verde tostado", TeaCategory.GREEN, CaffeineLevel.MEDIUM)
        )
        whenever(teaRepository.filterTeas(null, CaffeineLevel.MEDIUM, null)).thenReturn(teas)

        // Setup mocks for each tea ID
        teas.forEach { tea ->
            tea.id?.let { doNothing().`when`(teaRepository).deleteById(it) }
        }

        val result = teaService.deleteAll(null, CaffeineLevel.MEDIUM, null)
        assertEquals(2, result)
    }

    @Test
    fun `deve deletar teas filtrados por origem`() {
        val teas = listOf(
            Tea(UUID.randomUUID(), "Sencha", "japan", "Chá verde", TeaCategory.GREEN, CaffeineLevel.MEDIUM),
            Tea(UUID.randomUUID(), "Matcha", "japan", "Chá verde em pó", TeaCategory.GREEN, CaffeineLevel.HIGH)
        )
        whenever(teaRepository.filterTeas(null, null, "japan")).thenReturn(teas)

        // Setup mocks for cada tea ID
        teas.forEach { tea ->
            tea.id?.let { doNothing().`when`(teaRepository).deleteById(it) }
        }

        val result = teaService.deleteAll(null, null, "japan")
        assertEquals(2, result)
    }

    @Test
    fun `deve deletar teas filtrados por múltiplos critérios`() {
        val teas = listOf(
            Tea(UUID.randomUUID(), "Sencha", "japan", "Chá verde", TeaCategory.GREEN, CaffeineLevel.MEDIUM)
        )
        whenever(teaRepository.filterTeas(TeaCategory.GREEN, CaffeineLevel.MEDIUM, "japan")).thenReturn(teas)

        // Setup mocks for cada tea ID
        teas.forEach { tea ->
            tea.id?.let { doNothing().`when`(teaRepository).deleteById(it) }
        }

        val result = teaService.deleteAll(TeaCategory.GREEN, CaffeineLevel.MEDIUM, "japan")
        assertEquals(1, result)
    }

    @Test
    fun `deve converter strings para enums e deletar teas filtrados`() {
        val teas = listOf(
            Tea(UUID.randomUUID(), "Sencha", "japan", "Chá verde", TeaCategory.GREEN, CaffeineLevel.MEDIUM)
        )
        whenever(teaRepository.filterTeas(TeaCategory.GREEN, CaffeineLevel.MEDIUM, "japan")).thenReturn(teas)

        // Setup mocks para cada tea ID
        teas.forEach { tea ->
            tea.id?.let { doNothing().`when`(teaRepository).deleteById(it) }
        }

        val result = teaService.deleteFiltered("GREEN", "MEDIUM", "japan")
        assertEquals(1, result)
    }

    @Test
    fun `deve lidar com valores de enum inválidos ao deletar teas filtrados`() {
        whenever(teaRepository.filterTeas(null, null, "japan")).thenReturn(emptyList())
        val result = teaService.deleteFiltered("INVALID_CATEGORY", "INVALID_LEVEL", "japan")
        assertEquals(0, result)
    }
}
