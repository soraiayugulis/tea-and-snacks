package sysout.openups.product.service

import io.quarkus.test.InjectMock
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import jakarta.ws.rs.NotFoundException
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.kotlin.*
import sysout.openups.common.Constants.Message.Error.Entity.SAUCE_NOT_FOUND
import sysout.openups.common.Constants.Message.Error.Entity.SNACK_NOT_FOUND
import sysout.openups.product.dto.SnackDTO
import sysout.openups.product.entity.Sauce
import sysout.openups.product.entity.Snack
import sysout.openups.product.repository.SauceRepository
import sysout.openups.product.repository.SnackRepository
import java.util.*

@QuarkusTest
class SnackServiceTest {
    @InjectMock
    lateinit var snackRepository: SnackRepository

    @InjectMock
    lateinit var sauceRepository: SauceRepository

    @Inject
    lateinit var snackService: SnackService

    private fun createSnack(name: String, description: String, flavor: String, vegan: Boolean): Snack {
        return Snack().apply {
            this.id = UUID.randomUUID()
            this.name = name
            this.description = description
            this.flavor = flavor
            this.vegan = vegan
            this.sides = mutableListOf()
        }
    }

    @Test
    fun `should list all snacks`() {
        val snacks = listOf(
            createSnack("Coxinha", "Massa crocante recheada com frango", "frango", false),
            createSnack("Kibe Vegano", "Kibe de soja com hortelã", "soja", true)
        )
        whenever(snackRepository.listAll()).thenReturn(snacks)

        val result = snackService.listAll()

        assertEquals(2, result.size)
        assertEquals("Coxinha", result[0].name)
        assertEquals("Kibe Vegano", result[1].name)
    }

    @Test
    fun `should filter snacks by vegan`() {
        val snacks = listOf(
            createSnack("Kibe Vegano", "Kibe de soja com hortelã", "soja", true),
            createSnack("Coxinha", "Massa crocante recheada com frango", "frango", false)
        )
        whenever(snackRepository.filterSnacks(eq(true), isNull())).thenReturn(listOf(snacks[0]))

        val result = snackService.listFiltered(vegan = true)

        assertEquals(1, result.size)
        assertEquals("Kibe Vegano", result[0].name)
        assertTrue(result[0].vegan)
    }

    @Test
    fun `should filter snacks by flavour`() {
        val snacks = listOf(
            createSnack("Pastel de Queijo", "Massa crocante com queijo derretido", "queijo", false),
            createSnack("Pastel de Carne", "Massa crocante com carne moída", "carne", false)
        )
        whenever(snackRepository.filterSnacks(isNull(), eq("queijo"))).thenReturn(listOf(snacks[0]))

        val result = snackService.listFiltered(flavour = "queijo")

        assertEquals(1, result.size)
        assertEquals("Pastel de Queijo", result[0].name)
        assertEquals("queijo", result[0].flavor)
    }

    @Test
    fun `should filter snacks by sauce flavour`() {
        val sauce = Sauce(
            name = "BBQ",
            flavour = "smoky"
        )
        val snack = createSnack("Wings", "Description", "spicy", false)
        snack.sides.add(sauce)

        whenever(snackRepository.filterSnacks(isNull(), isNull())).thenReturn(listOf(snack))

        val result = snackService.listFiltered(sauceFlavour = "smoky")

        assertEquals(1, result.size)
        assertEquals("Wings", result[0].name)
    }

    @Test
    fun `should find snacks with partial sauce flavour match`() {
        val sauce1 = Sauce(name = "BBQ", flavour = "smoky bbq")
        val sauce2 = Sauce(name = "Sweet BBQ", flavour = "sweet bbq")

        val snack1 = createSnack("Wings 1", "Description 1", "spicy", false)
        snack1.sides.add(sauce1)
        val snack2 = createSnack("Wings 2", "Description 2", "sweet", false)
        snack2.sides.add(sauce2)

        whenever(snackRepository.filterSnacks(isNull(), isNull())).thenReturn(listOf(snack1, snack2))

        val result = snackService.listFiltered(sauceFlavour = "bbq")

        assertEquals(2, result.size)
        assertTrue(result.any { it.name == "Wings 1" })
        assertTrue(result.any { it.name == "Wings 2" })
    }

    @Test
    fun `should combine filters for vegan, flavour and sauce`() {
        val sauce = Sauce(name = "Vegan Mayo", flavour = "vegan mayo")
        val snack = createSnack("Salad", "Vegan salad", "fresh", true)
        snack.sides.add(sauce)

        whenever(snackRepository.filterSnacks(eq(true), eq("fresh"))).thenReturn(listOf(snack))

        val result = snackService.listFiltered(true, "fresh", "mayo")

        assertEquals(1, result.size)
        assertEquals("Salad", result[0].name)
        assertTrue(result[0].vegan)
        assertEquals("fresh", result[0].flavor)
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
        val dto = SnackDTO(null, "Pão de Queijo", "Pão de queijo mineiro quentinho", "queijo", false, emptyList())
        val entity = Snack().apply {
            id = UUID.randomUUID()
            name = "Pão de Queijo"
            description = "Pão de queijo mineiro quentinho"
            flavor = "queijo"
            vegan = false
        }
        whenever(snackRepository.save(any())).thenReturn(entity)

        val result = snackService.add(dto)

        assertEquals("Pão de Queijo", result.name)
        verify(snackRepository).save(any())
    }

    @Test
    fun `should update an existing snack`() {
        val id = UUID.randomUUID()
        val entity = Snack().apply {
            this.id = id
            name = "Acarajé"
            description = "Bolinho de feijão fradinho"
            flavor = "camarão"
            vegan = false
        }
        val dto = SnackDTO(
            id,
            "Acarajé Vegano",
            "Bolinho de feijão fradinho sem camarão",
            "feijão",
            true,
            emptyList()
        )
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
        val sauce = Sauce(UUID.randomUUID(), "Maionese Caseira", "maionese com limão")
        val snack = Snack().apply {
            this.id = id
            name = "Batata Frita"
            description = "Batata frita crocante"
            flavor = "batata"
            sides = mutableListOf(sauce)
        }
        whenever(snackRepository.findById(id)).thenReturn(snack)

        val result = snackService.getSauces(id)

        assertEquals(1, result.size)
        assertEquals("Maionese Caseira", result[0].name)
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
            name = "Molho de Pimenta"
            flavour = "pimenta malagueta"
        }
        val snack = Snack().apply {
            id = snackId
            name = "Pastel de Carne"
            description = "Pastel recheado com carne moída temperada"
            flavor = "carne"
            sides = mutableListOf()
        }

        whenever(snackRepository.findById(snackId)).thenReturn(snack)
        whenever(sauceRepository.findById(sauceId)).thenReturn(sauce)
        whenever(snackRepository.update(any(), any())).thenReturn(snack)

        val result = snackService.addSauce(snackId, sauceId)

        assertNotNull(result)
        assertTrue(snack.sides.contains(sauce))
        assertEquals(1, snack.sides.size)
        assertEquals(sauce, snack.sides[0])
        verify(snackRepository).update(any(), any())
    }

    @Test
    fun `should not add duplicate sauce to snack`() {
        val snackId = UUID.randomUUID()
        val sauceId = UUID.randomUUID()
        val sauce = Sauce().apply {
            id = sauceId
            name = "Catupiry"
            flavour = "queijo cremoso"
        }
        val snack = Snack().apply {
            id = snackId
            name = "Coxinha"
            description = "Massa crocante recheada com frango"
            flavor = "frango"
            sides = mutableListOf(sauce)
        }

        whenever(snackRepository.findById(snackId)).thenReturn(snack)
        whenever(sauceRepository.findById(sauceId)).thenReturn(sauce)
        whenever(snackRepository.update(any(), any())).thenReturn(snack)

        val result = snackService.addSauce(snackId, sauceId)

        assertNotNull(result)
        assertEquals(1, snack.sides.size)
        verify(snackRepository).update(any(), any())
    }

    @Test
    fun `should remove sauce from snack successfully`() {
        val snackId = UUID.randomUUID()
        val sauceId = UUID.randomUUID()
        val sauce = Sauce().apply {
            id = sauceId
            name = "Ketchup"
            flavour = "tomate"
        }
        val snack = Snack().apply {
            id = snackId
            name = "Batata Frita"
            description = "Batata frita crocante"
            flavor = "batata"
            sides = mutableListOf(sauce)
        }

        whenever(snackRepository.findById(snackId)).thenReturn(snack)
        whenever(sauceRepository.findById(sauceId)).thenReturn(sauce)
        whenever(snackRepository.update(any(), any())).thenReturn(snack)

        val result = snackService.removeSauce(snackId, sauceId)

        assertNotNull(result)
        assertFalse(snack.sides.contains(sauce))
        assertTrue(snack.sides.isEmpty())
        verify(snackRepository).update(any(), any())
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
}
