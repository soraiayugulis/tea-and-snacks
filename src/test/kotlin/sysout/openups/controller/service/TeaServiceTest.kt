package sysout.openups.controller.service

import io.quarkus.test.InjectMock
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.eq
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
    fun `should list all teas`() {
        val teas = listOf(
            Tea(UUID.randomUUID(), "Chá de Camomila", "brasil", "Chá calmante de camomila", TeaCategory.HERBAL, CaffeineLevel.NONE),
            Tea(UUID.randomUUID(), "Chá Mate", "brasil", "Chá mate tostado", TeaCategory.BLACK, CaffeineLevel.HIGH)
        )
        whenever(teaRepository.listAll()).thenReturn(teas)
        val result = teaService.listAll()
        assertEquals(2, result.size)
        assertTrue(result.any { it.category == TeaCategory.HERBAL })
    }

    @Test
    fun `should filter teas by category, caffeineLevel and origin`() {
        val tea = Tea().apply {
            name = "Capim Limão"
            origin = "brasil"
            description = "Chá natural de capim limão"
            category = TeaCategory.HERBAL
            caffeineLevel = CaffeineLevel.NONE
        }
        whenever(teaRepository.filterTeas(eq(TeaCategory.HERBAL), eq(CaffeineLevel.NONE), eq("brasil")))
            .thenReturn(listOf(tea))

        val result = teaService.filterTeas("HERBAL", "NONE", "brasil")

        assertEquals(1, result.size)
        assertEquals("Capim Limão", result[0].name)
        assertEquals("brasil", result[0].origin)
        assertEquals(TeaCategory.HERBAL, result[0].category)
        assertEquals(CaffeineLevel.NONE, result[0].caffeineLevel)
    }

    @Test
    fun `should return tea by id`() {
        val id = UUID.randomUUID()
        val tea = Tea(id, "Sencha", "japan", "Chá verde", TeaCategory.GREEN, CaffeineLevel.MEDIUM)
        whenever(teaRepository.findById(id)).thenReturn(tea)
        val result = teaService.findById(id)
        assertNotNull(result)
        assertEquals("Sencha", result!!.name)
    }

    @Test
    fun `should return null if getting a non-existing tea`() {
        val id = UUID.randomUUID()
        whenever(teaRepository.findById(id)).thenReturn(null)
        val result = teaService.findById(id)
        assertNull(result)
    }

    @Test
    fun `should add tea`() {
        val dto = TeaDTO(null, "Chá de Hortelã", "brasil", "Chá refrescante de hortelã", TeaCategory.HERBAL, CaffeineLevel.NONE)
        val entity = Tea(UUID.randomUUID(), "Chá de Hortelã", "brasil", "Chá refrescante de hortelã", TeaCategory.HERBAL, CaffeineLevel.NONE)
        whenever(teaRepository.save(any())).thenReturn(entity)
        val result = teaService.add(dto)
        assertEquals("Chá de Hortelã", result.name)
    }

    @Test
    fun `should update existing tea`() {
        val id = UUID.randomUUID()
        val entity = Tea(id, "Chá de Erva Cidreira", "brasil", "Chá calmante de erva cidreira", TeaCategory.HERBAL, CaffeineLevel.NONE)
        val dto = TeaDTO(id, "Chá de Melissa", "brasil", "Chá calmante de melissa", TeaCategory.HERBAL, CaffeineLevel.NONE)
        whenever(teaRepository.findById(id)).thenReturn(entity)
        whenever(teaRepository.update(id, entity)).thenReturn(entity)
        val result = teaService.update(id, dto)
        assertNotNull(result)
    }

    @Test
    fun `should return null when update non-existing tea`() {
        val id = UUID.randomUUID()
        val dto = TeaDTO(id, "Sencha Atualizado", "china", "Chá verde chinês", TeaCategory.GREEN, CaffeineLevel.LOW)
        whenever(teaRepository.findById(id)).thenReturn(null)
        val result = teaService.update(id, dto)
        assertNull(result)
    }

    @Test
    fun `should delete existing tea`() {
        val id = UUID.randomUUID()
        val tea = Tea(id, "Sencha", "japan", "Chá verde", TeaCategory.GREEN, CaffeineLevel.MEDIUM)
        whenever(teaRepository.findById(id)).thenReturn(tea)
        doNothing().`when`(teaRepository).deleteById(id)
        val result = teaService.delete(id)
        assertTrue(result)
    }

    @Test
    fun `should return false when delete non-existing tea`() {
        val id = UUID.randomUUID()
        whenever(teaRepository.findById(id)).thenReturn(null)
        val result = teaService.delete(id)
        assertFalse(result)
    }

    @Test
    fun `should delete all teas when no filter is set`() {
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
    fun `should delete all teas filter by category`() {
        val teas = listOf(
            Tea(UUID.randomUUID(), "Chá de Boldo", "brasil", "Chá digestivo de boldo", TeaCategory.HERBAL, CaffeineLevel.NONE),
            Tea(UUID.randomUUID(), "Chá de Carqueja", "brasil", "Chá digestivo de carqueja", TeaCategory.HERBAL, CaffeineLevel.NONE)
        )
        whenever(teaRepository.filterTeas(TeaCategory.HERBAL, null, null)).thenReturn(teas)

        teas.forEach { tea ->
            tea.id?.let { doNothing().`when`(teaRepository).deleteById(it) }
        }

        val result = teaService.deleteAll(TeaCategory.HERBAL, null, null)
        assertEquals(2, result)
    }

    @Test
    fun `should delete all teas filter by caffeine`() {
        val teas = listOf(
            Tea(UUID.randomUUID(), "Chá Preto", "brasil", "Chá preto forte", TeaCategory.BLACK, CaffeineLevel.HIGH),
            Tea(UUID.randomUUID(), "Ch�� Mate", "brasil", "Chá mate tostado", TeaCategory.BLACK, CaffeineLevel.HIGH)
        )
        whenever(teaRepository.filterTeas(null, CaffeineLevel.HIGH, null)).thenReturn(teas)

        teas.forEach { tea ->
            tea.id?.let { doNothing().`when`(teaRepository).deleteById(it) }
        }

        val result = teaService.deleteAll(null, CaffeineLevel.HIGH, null)
        assertEquals(2, result)
    }

    @Test
    fun `should delete all teas filter by origin`() {
        val teas = listOf(
            Tea(UUID.randomUUID(), "Chá de Cidreira", "brasil", "Chá natural de erva cidreira", TeaCategory.HERBAL, CaffeineLevel.NONE),
            Tea(UUID.randomUUID(), "Chá de Camomila", "brasil", "Chá calmante de camomila", TeaCategory.HERBAL, CaffeineLevel.NONE)
        )
        whenever(teaRepository.filterTeas(null, null, "brasil")).thenReturn(teas)

        teas.forEach { tea ->
            tea.id?.let { doNothing().`when`(teaRepository).deleteById(it) }
        }

        val result = teaService.deleteAll(null, null, "brasil")
        assertEquals(2, result)
    }

    @Test
    fun `should delete all teas filter by multiple criteria`() {
        val teas = listOf(
            Tea(UUID.randomUUID(), "Sencha", "japan", "Chá verde", TeaCategory.GREEN, CaffeineLevel.MEDIUM)
        )
        whenever(teaRepository.filterTeas(TeaCategory.GREEN, CaffeineLevel.MEDIUM, "japan")).thenReturn(teas)

        teas.forEach { tea ->
            tea.id?.let { doNothing().`when`(teaRepository).deleteById(it) }
        }

        val result = teaService.deleteAll(TeaCategory.GREEN, CaffeineLevel.MEDIUM, "japan")
        assertEquals(1, result)
    }

    @Test
    fun `should convert strings to enums and delete filtered teas`() {
        val teas = listOf(
            Tea(UUID.randomUUID(), "Sencha", "japan", "Chá verde", TeaCategory.GREEN, CaffeineLevel.MEDIUM)
        )
        whenever(teaRepository.filterTeas(TeaCategory.GREEN, CaffeineLevel.MEDIUM, "japan")).thenReturn(teas)

        teas.forEach { tea ->
            tea.id?.let { doNothing().`when`(teaRepository).deleteById(it) }
        }

        val result = teaService.deleteFiltered("GREEN", "MEDIUM", "japan")
        assertEquals(1, result)
    }

    @Test
    fun `should deal with invalid enum values when delete filter teas`() {
        whenever(teaRepository.filterTeas(null, null, "japan")).thenReturn(emptyList())
        val result = teaService.deleteFiltered("INVALID_CATEGORY", "INVALID_LEVEL", "japan")
        assertEquals(0, result)
    }
}
