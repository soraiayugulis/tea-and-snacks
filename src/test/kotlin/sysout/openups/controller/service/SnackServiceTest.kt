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
        val snack = Snack().apply {
            this.id = id
            name = "Coxinha"
            flavor = "frango"
            vegan = false
        }
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
        val entity = Snack().apply {
            id = UUID.randomUUID()
            name = "Coxinha"
            flavor = "frango"
            vegan = false
        }
        whenever(snackRepository.save(any())).thenReturn(entity)

        val result = snackService.add(dto)

        assertEquals("Coxinha", result.name)
        verify(snackRepository).save(any())
    }

    @Test
    fun `should update an existing snack`() {
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

        val result = snackService.update(id, dto)

        assertNotNull(result)
        verify(snackRepository).update(any(), any())
    }

    @Test
    fun `should delete existing snack`() {
        val id = UUID.randomUUID()
        whenever(snackRepository.findById(id)).thenReturn(Snack().apply { this.id = id })
        doNothing().whenever(snackRepository).deleteById(id)

        val result = snackService.delete(id)

        assertTrue(result)
        verify(snackRepository).deleteById(id)
    }

    @Test
    fun `should return sauces from a specific snack`() {
        val id = UUID.randomUUID()
        val sauce = Sauce(UUID.randomUUID(), "Barbecue", "barbecue")
        val snack = Snack().apply {
            this.id = id
            sides = mutableListOf(sauce)
        }
        whenever(snackRepository.findById(id)).thenReturn(snack)

        val result = snackService.getSauces(id)

        assertEquals(1, result.size)
        assertEquals("Barbecue", result[0].name)
    }

    @Test
    fun `should throw exception when getting sauces from a non-existing snack`() {
        val id = UUID.randomUUID()
        whenever(snackRepository.findById(id)).thenReturn(null)

        val exception = assertThrows(NotFoundException::class.java) {
            snackService.getSauces(id)
        }
        assertEquals(SNACK_NOT_FOUND, exception.message)
    }

    @Test
    fun `should add sauce to snack successfully`() {
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

        val result = snackService.addSauce(snackId, sauceId)

        assertNotNull(result)
        assertTrue(snack.sides.contains(sauce))
        assertEquals(1, snack.sides.size)
        assertEquals(sauce, snack.sides[0])
        verify(snackRepository).save(snack)
    }

    @Test
    fun `should not add duplicate sauce to snack`() {
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

        val result = snackService.addSauce(snackId, sauceId)

        assertNotNull(result)
        assertEquals(1, snack.sides.size)
        verify(snackRepository).save(snack)
    }

    @Test
    fun `should remove sauce from snack successfully`() {
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

        val result = snackService.removeSauce(snackId, sauceId)

        assertNotNull(result)
        assertFalse(snack.sides.contains(sauce))
        assertTrue(snack.sides.isEmpty())
        verify(snackRepository).save(snack)
    }

    @Test
    fun `should throw NotFoundException when adding sauce to non-existent snack`() {
        val snackId = UUID.randomUUID()
        val sauceId = UUID.randomUUID()
        whenever(snackRepository.findById(snackId)).thenReturn(null)

        val exception = assertThrows(NotFoundException::class.java) {
            snackService.addSauce(snackId, sauceId)
        }
        assertEquals(SNACK_NOT_FOUND, exception.message)
    }

    @Test
    fun `should throw NotFoundException when adding non-existent sauce`() {
        val snackId = UUID.randomUUID()
        val sauceId = UUID.randomUUID()
        val snack = Snack().apply {
            id = snackId
            name = "Hot Dog"
        }

        whenever(snackRepository.findById(snackId)).thenReturn(snack)
        whenever(sauceRepository.findById(sauceId)).thenReturn(null)

        val exception = assertThrows(NotFoundException::class.java) {
            snackService.addSauce(snackId, sauceId)
        }
        assertEquals(SAUCE_NOT_FOUND, exception.message)
    }

    @Test
    fun `should throw NotFoundException when removing sauce from non-existent snack`() {
        val snackId = UUID.randomUUID()
        val sauceId = UUID.randomUUID()
        whenever(snackRepository.findById(snackId)).thenReturn(null)

        val exception = assertThrows(NotFoundException::class.java) {
            snackService.removeSauce(snackId, sauceId)
        }
        assertEquals(SNACK_NOT_FOUND, exception.message)
    }

    @Test
    fun `should throw NotFoundException when removing non-existent sauce`() {
        val snackId = UUID.randomUUID()
        val sauceId = UUID.randomUUID()
        val snack = Snack().apply {
            id = snackId
            name = "Hot Dog"
        }

        whenever(snackRepository.findById(snackId)).thenReturn(snack)
        whenever(sauceRepository.findById(sauceId)).thenReturn(null)

        val exception = assertThrows(NotFoundException::class.java) {
            snackService.removeSauce(snackId, sauceId)
        }
        assertEquals(SAUCE_NOT_FOUND, exception.message)
    }

    @Test
    fun `should filter snacks by sauce flavour`() {
        val cheeseId = UUID.randomUUID()
        val cheeseSauce = Sauce().apply {
            id = cheeseId
            name = "American Cheese"
            flavour = "cheese"
        }

        val snacks = listOf(
            Snack().apply {
                id = UUID.randomUUID()
                name = "Batata Frita"
                flavor = "batata"
                vegan = true
                sides = mutableListOf(cheeseSauce)
            }
        )
        whenever(snackRepository.filterSnacks(null, null)).thenReturn(snacks)

        val result = snackService.listAll(sauceFlavour = "cheese")

        assertEquals(1, result.size)
        assertEquals("Batata Frita", result[0].name)
    }

    @Test
    fun `should find snacks with partial sauce flavour match`() {
        val cheese1 = Sauce().apply {
            id = UUID.randomUUID()
            name = "American Cheese"
            flavour = "american cheese"
        }
        val cheese2 = Sauce().apply {
            id = UUID.randomUUID()
            name = "Mozzarella"
            flavour = "mozzarella cheese"
        }

        val snacks = listOf(
            Snack().apply {
                id = UUID.randomUUID()
                name = "Batata Frita 1"
                flavor = "batata"
                sides = mutableListOf(cheese1)
            },
            Snack().apply {
                id = UUID.randomUUID()
                name = "Batata Frita 2"
                flavor = "batata"
                sides = mutableListOf(cheese2)
            }
        )
        whenever(snackRepository.filterSnacks(null, null)).thenReturn(snacks)

        val result = snackService.listAll(sauceFlavour = "cheese")

        assertEquals(2, result.size)
        assertTrue(result.any { it.name == "Batata Frita 1" })
        assertTrue(result.any { it.name == "Batata Frita 2" })
    }

    @Test
    fun `should combine filters for vegan, flavour and sauce`() {
        val cheeseSauce = Sauce().apply {
            id = UUID.randomUUID()
            name = "Vegan Cheese"
            flavour = "vegan cheese"
        }

        val snacks = listOf(
            Snack().apply {
                id = UUID.randomUUID()
                name = "Batata Vegana"
                flavor = "batata"
                vegan = true
                sides = mutableListOf(cheeseSauce)
            }
        )
        whenever(snackRepository.filterSnacks(true, "batata")).thenReturn(snacks)

        val result = snackService.listAll(vegan = true, flavour = "batata", sauceFlavour = "cheese")

        assertEquals(1, result.size)
        assertTrue(result[0].vegan)
        assertEquals("batata", result[0].flavor)
        assertEquals("Batata Vegana", result[0].name)
    }
}
