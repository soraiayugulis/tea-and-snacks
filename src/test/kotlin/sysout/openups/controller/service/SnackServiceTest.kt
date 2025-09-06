package sysout.openups.controller.service

import io.quarkus.test.InjectMock
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import jakarta.ws.rs.NotFoundException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever
import sysout.openups.controller.common.Constants.Message.Error.Entity.SAUCE_NOT_FOUND
import sysout.openups.controller.common.Constants.Message.Error.Entity.SNACK_NOT_FOUND
import sysout.openups.controller.dto.SnackDTO
import sysout.openups.controller.entity.Sauce
import sysout.openups.controller.entity.Snack
import sysout.openups.controller.repository.SauceRepository
import sysout.openups.controller.repository.SnackRepository
import java.util.*

@QuarkusTest
class SnackServiceTest {
    @InjectMock
    lateinit var snackRepository: SnackRepository

    @InjectMock
    lateinit var sauceRepository: SauceRepository

    @Inject
    lateinit var snackService: SnackService

    // CRUD Tests
    @Test
    fun `should list all snacks`() {
        // Arrange
        val snacks = listOf(
            Snack().apply { id = UUID.randomUUID(); name = "Coxinha"; flavor = "frango"; vegan = false },
            Snack().apply { id = UUID.randomUUID(); name = "Kibe Vegano"; flavor = "soja"; vegan = true }
        )
        whenever(snackRepository.filterSnacks(null, null)).thenReturn(snacks)

        // Act
        val result = snackService.listAll()

        // Assert
        assertEquals(2, result.size)
    }

    @Test
    fun `should filter snacks by vegan`() {
        // Arrange
        val snacks = listOf(
            Snack().apply { id = UUID.randomUUID(); name = "Kibe Vegano"; flavor = "soja"; vegan = true }
        )
        whenever(snackRepository.filterSnacks(true, null)).thenReturn(snacks)

        // Act
        val result = snackService.listAll(vegan = true)

        // Assert
        assertEquals(1, result.size)
        assertTrue(result[0].vegan)
    }

    @Test
    fun `should filter snacks by flavour`() {
        // Arrange
        val snacks = listOf(
            Snack().apply { id = UUID.randomUUID(); name = "Coxinha"; flavor = "frango"; vegan = false }
        )
        whenever(snackRepository.filterSnacks(null, "frango")).thenReturn(snacks)

        // Act
        val result = snackService.listAll(flavour = "frango")

        // Assert
        assertEquals(1, result.size)
        assertEquals("frango", result[0].flavor)
    }

    @Test
    fun `should return snack by id`() {
        // Arrange
        val id = UUID.randomUUID()
        val snack = Snack().apply {
            this.id = id
            name = "Coxinha"
            flavor = "frango"
            vegan = false
        }
        whenever(snackRepository.findById(id)).thenReturn(snack)

        // Act
        val result = snackService.findById(id)

        // Assert
        assertNotNull(result)
        assertEquals("Coxinha", result!!.name)
    }

    @Test
    fun `should return null if id does not exist`() {
        // Arrange
        val id = UUID.randomUUID()
        whenever(snackRepository.findById(id)).thenReturn(null)

        // Act
        val result = snackService.findById(id)

        // Assert
        assertNull(result)
    }

    @Test
    fun `should add a snack`() {
        // Arrange
        val dto = SnackDTO(null, "Coxinha", "frango", "salgado", false, emptyList())
        val entity = Snack().apply {
            id = UUID.randomUUID()
            name = "Coxinha"
            flavor = "frango"
            vegan = false
        }
        whenever(snackRepository.save(any())).thenReturn(entity)

        // Act
        val result = snackService.add(dto)

        // Assert
        assertEquals("Coxinha", result.name)
        verify(snackRepository).save(any())
    }

    @Test
    fun `should update an existing snack`() {
        // Arrange
        val id = UUID.randomUUID()
        val entity = Snack().apply {
            this.id = id
            name = "Coxinha"
            flavor = "frango"
            vegan = false
        }
        val dto = SnackDTO(id, "Coxinha Atualizada", "soja", "salgado", true, emptyList())
        whenever(snackRepository.findById(id)).thenReturn(entity)
        whenever(snackRepository.update(id, entity)).thenReturn(entity)

        // Act
        val result = snackService.update(id, dto)

        // Assert
        assertNotNull(result)
        verify(snackRepository).update(any(), any())
    }

    @Test
    fun `should delete existing snack`() {
        // Arrange
        val id = UUID.randomUUID()
        whenever(snackRepository.findById(id)).thenReturn(Snack().apply { this.id = id })
        doNothing().whenever(snackRepository).deleteById(id)

        // Act
        val result = snackService.delete(id)

        // Assert
        assertTrue(result)
        verify(snackRepository).deleteById(id)
    }

    // Sauce-related Tests
    @Test
    fun `should return sauces from a specific snack`() {
        // Arrange
        val id = UUID.randomUUID()
        val sauce = Sauce(UUID.randomUUID(), "Barbecue", "barbecue")
        val snack = Snack().apply {
            this.id = id
            sides = mutableListOf(sauce)
        }
        whenever(snackRepository.findById(id)).thenReturn(snack)

        // Act
        val result = snackService.getSauces(id)

        // Assert
        assertEquals(1, result.size)
        assertEquals("Barbecue", result[0].name)
    }

    @Test
    fun `should throw exception when getting sauces from a non-existing snack`() {
        // Arrange
        val id = UUID.randomUUID()
        whenever(snackRepository.findById(id)).thenReturn(null)

        // Act & Assert
        val exception = assertThrows(NotFoundException::class.java) {
            snackService.getSauces(id)
        }
        assertEquals(SNACK_NOT_FOUND, exception.message)
    }

    @Test
    fun `should add sauce to snack successfully`() {
        // Arrange
        val snackId = UUID.randomUUID()
        val sauceId = UUID.randomUUID()
        val sauce = Sauce().apply {
            id = sauceId
            name = "Mustard"
        }
        val snack = Snack().apply {
            id = snackId
            name = "Hot Dog"
            flavor = "meat"
            sides = mutableListOf()
        }

        whenever(snackRepository.findById(snackId)).thenReturn(snack)
        whenever(sauceRepository.findById(sauceId)).thenReturn(sauce)
        whenever(snackRepository.save(any())).thenReturn(snack)

        // Act
        val result = snackService.addSauce(snackId, sauceId)

        // Assert
        assertNotNull(result)
        assertTrue(snack.sides.contains(sauce))
        assertEquals(1, snack.sides.size)
        assertEquals(sauce, snack.sides[0])
        verify(snackRepository).save(snack)
    }

    @Test
    fun `should not add duplicate sauce to snack`() {
        // Arrange
        val snackId = UUID.randomUUID()
        val sauceId = UUID.randomUUID()
        val sauce = Sauce().apply {
            id = sauceId
            name = "Mustard"
        }
        val snack = Snack().apply {
            id = snackId
            name = "Hot Dog"
            sides = mutableListOf(sauce)
        }

        whenever(snackRepository.findById(snackId)).thenReturn(snack)
        whenever(sauceRepository.findById(sauceId)).thenReturn(sauce)
        whenever(snackRepository.save(any())).thenReturn(snack)

        // Act
        val result = snackService.addSauce(snackId, sauceId)

        // Assert
        assertNotNull(result)
        assertEquals(1, snack.sides.size)
        verify(snackRepository).save(snack)
    }

    @Test
    fun `should remove sauce from snack successfully`() {
        // Arrange
        val snackId = UUID.randomUUID()
        val sauceId = UUID.randomUUID()
        val sauce = Sauce().apply {
            id = sauceId
            name = "Mustard"
        }
        val snack = Snack().apply {
            id = snackId
            name = "Hot Dog"
            sides = mutableListOf(sauce)
        }

        whenever(snackRepository.findById(snackId)).thenReturn(snack)
        whenever(sauceRepository.findById(sauceId)).thenReturn(sauce)
        whenever(snackRepository.save(any())).thenReturn(snack)

        // Act
        val result = snackService.removeSauce(snackId, sauceId)

        // Assert
        assertNotNull(result)
        assertFalse(snack.sides.contains(sauce))
        assertTrue(snack.sides.isEmpty())
        verify(snackRepository).save(snack)
    }

    // Error cases
    @Test
    fun `should throw NotFoundException when adding sauce to non-existent snack`() {
        // Arrange
        val snackId = UUID.randomUUID()
        val sauceId = UUID.randomUUID()
        whenever(snackRepository.findById(snackId)).thenReturn(null)

        // Act & Assert
        val exception = assertThrows(NotFoundException::class.java) {
            snackService.addSauce(snackId, sauceId)
        }
        assertEquals(SNACK_NOT_FOUND, exception.message)
    }

    @Test
    fun `should throw NotFoundException when adding non-existent sauce`() {
        // Arrange
        val snackId = UUID.randomUUID()
        val sauceId = UUID.randomUUID()
        val snack = Snack().apply {
            id = snackId
            name = "Hot Dog"
        }

        whenever(snackRepository.findById(snackId)).thenReturn(snack)
        whenever(sauceRepository.findById(sauceId)).thenReturn(null)

        // Act & Assert
        val exception = assertThrows(NotFoundException::class.java) {
            snackService.addSauce(snackId, sauceId)
        }
        assertEquals(SAUCE_NOT_FOUND, exception.message)
    }

    @Test
    fun `should throw NotFoundException when removing sauce from non-existent snack`() {
        // Arrange
        val snackId = UUID.randomUUID()
        val sauceId = UUID.randomUUID()
        whenever(snackRepository.findById(snackId)).thenReturn(null)

        // Act & Assert
        val exception = assertThrows(NotFoundException::class.java) {
            snackService.removeSauce(snackId, sauceId)
        }
        assertEquals(SNACK_NOT_FOUND, exception.message)
    }

    @Test
    fun `should throw NotFoundException when removing non-existent sauce`() {
        // Arrange
        val snackId = UUID.randomUUID()
        val sauceId = UUID.randomUUID()
        val snack = Snack().apply {
            id = snackId
            name = "Hot Dog"
        }

        whenever(snackRepository.findById(snackId)).thenReturn(snack)
        whenever(sauceRepository.findById(sauceId)).thenReturn(null)

        // Act & Assert
        val exception = assertThrows(NotFoundException::class.java) {
            snackService.removeSauce(snackId, sauceId)
        }
        assertEquals(SAUCE_NOT_FOUND, exception.message)
    }
}
