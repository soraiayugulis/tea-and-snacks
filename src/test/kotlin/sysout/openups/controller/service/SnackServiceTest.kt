package sysout.openups.controller.service

import io.quarkus.test.InjectMock
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import sysout.openups.controller.dto.SnackDTO
import sysout.openups.controller.entity.Sauce
import sysout.openups.controller.entity.Snack
import sysout.openups.controller.repository.SnackRepository
import java.util.*

@QuarkusTest
class SnackServiceTest {
    @InjectMock
    lateinit var snackRepository: SnackRepository

    @Inject
    lateinit var snackService: SnackService

    @Test
    fun `should list all snacks`() {
        val snacks = listOf(
            Snack().apply { id = UUID.randomUUID(); name = "Coxinha"; flavor = "frango"; vegan = false },
            Snack().apply { id = UUID.randomUUID(); name = "Kibe Vegano"; flavor = "soja"; vegan = true }
        )
        whenever(snackRepository.filterSnacks(null, null)).thenReturn(snacks)
        val result = snackService.listAll()
        assertEquals(2, result.size)
    }

    @Test
    fun `should filter snacks by vegan`() {
        val snacks = listOf(
            Snack().apply { id = UUID.randomUUID(); name = "Kibe Vegano"; flavor = "soja"; vegan = true }
        )
        whenever(snackRepository.filterSnacks(true, null)).thenReturn(snacks)
        val result = snackService.listAll(vegan = true)
        assertEquals(1, result.size)
        assertTrue(result[0].vegan)
    }

    @Test
    fun `should filter snacks by flavour`() {
        val snacks = listOf(
            Snack().apply { id = UUID.randomUUID(); name = "Coxinha"; flavor = "frango"; vegan = false }
        )
        whenever(snackRepository.filterSnacks(null, "frango")).thenReturn(snacks)
        val result = snackService.listAll(flavour = "frango")
        assertEquals(1, result.size)
        assertEquals("frango", result[0].flavor)
    }

    @Test
    fun `should return snack by id`() {
        val id = UUID.randomUUID()
        val snack = Snack().apply { this.id = id; name = "Coxinha"; flavor = "frango"; vegan = false }
        whenever(snackRepository.findById(id)).thenReturn(snack)
        val result = snackService.findById(id)
        assertNotNull(result)
        assertEquals("Coxinha", result!!.name)
    }

    @Test
    fun `should return null if id does not exist`() {
        val id = UUID.randomUUID()
        whenever(snackRepository.findById(id)).thenReturn(null)
        val result = snackService.findById(id)
        assertNull(result)
    }

    @Test
    fun `should add a snack`() {
        val dto = SnackDTO(null, "Coxinha", "frango", "salgado", false, emptyList())
        val entity = Snack().apply { id = UUID.randomUUID(); name = "Coxinha"; flavor = "frango"; vegan = false }
        whenever(snackRepository.save(any())).thenReturn(entity)
        val result = snackService.add(dto)
        assertEquals("Coxinha", result.name)
    }

    @Test
    fun `should update a existing snack`() {
        val id = UUID.randomUUID()
        val entity = Snack().apply { this.id = id; name = "Coxinha"; flavor = "frango"; vegan = false }
        val dto = SnackDTO(id, "Coxinha Atualizada", "soja", "salgado", true, emptyList())
        whenever(snackRepository.findById(id)).thenReturn(entity)
        whenever(snackRepository.update(id, entity)).thenReturn(entity)
        val result = snackService.update(id, dto)
        assertNotNull(result)
    }

    @Test
    fun `should return null when update existing snack`() {
        val id = UUID.randomUUID()
        val dto = SnackDTO(id, "Coxinha Atualizada", "soja", "salgado",true, emptyList())
        whenever(snackRepository.findById(id)).thenReturn(null)
        val result = snackService.update(id, dto)
        assertNull(result)
    }

    @Test
    fun `should delete existing snack`() {
        val id = UUID.randomUUID()
        whenever(snackRepository.findById(id)).thenReturn(Snack().apply { this.id = id })
        doNothing().whenever(snackRepository).deleteById(id)
        val result = snackService.delete(id)
        assertTrue(result)
    }

    @Test
    fun `should return false when delete non-existing snack`() {
        val id = UUID.randomUUID()
        whenever(snackRepository.findById(id)).thenReturn(null)
        val result = snackService.delete(id)
        assertFalse(result)
    }

    @Test
    fun `should return sauces from a specific snack`() {
        val id = UUID.randomUUID()
        val sauce = Sauce(UUID.randomUUID(), "Barbecue", "barbecue")
        val snack = Snack().apply { this.id = id; sides = mutableListOf(sauce) }
        whenever(snackRepository.findById(id)).thenReturn(snack)
        val result = snackService.getSauces(id)
        assertEquals(1, result.size)
        assertEquals("Barbecue", result[0].name)
    }

    @Test
    fun `should throw exception when getting sauces from a non-existing snack`() {
        val id = UUID.randomUUID()
        whenever(snackRepository.findById(id)).thenReturn(null)
        assertThrows(jakarta.ws.rs.NotFoundException::class.java) {
            snackService.getSauces(id)
        }
    }
}
