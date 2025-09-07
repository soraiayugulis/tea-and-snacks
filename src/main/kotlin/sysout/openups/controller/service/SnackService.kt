package sysout.openups.controller.service

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.NotFoundException
import sysout.openups.controller.common.Constants.Message.Error.Entity.SAUCE_NOT_FOUND
import sysout.openups.controller.common.Constants.Message.Error.Entity.SNACK_NOT_FOUND
import sysout.openups.controller.common.PaginatedResponse
import sysout.openups.controller.common.PaginationUtils
import sysout.openups.controller.dto.SnackDTO
import sysout.openups.controller.entity.Sauce
import sysout.openups.controller.entity.Snack
import sysout.openups.controller.repository.SauceRepository
import sysout.openups.controller.repository.SnackRepository
import java.util.*

@ApplicationScoped
class SnackService @Inject constructor(
    private val snackRepository: SnackRepository,
    private val sauceRepository: SauceRepository
) {
    fun listAll(
        vegan: Boolean? = null,
        flavour: String? = null,
        sauceFlavour: String? = null,
        page: Int = 0,
        size: Int = 10
    ): PaginatedResponse<SnackDTO> {
        val validatedPage = PaginationUtils.validateAndGetPageNumber(page)
        val validatedSize = PaginationUtils.validateAndGetPageSize(size)

        val totalElements = snackRepository.countFilteredSnacks(vegan, flavour)
        var snacks = snackRepository.filterSnacksPaginated(vegan, flavour, validatedPage, validatedSize)

        if (!sauceFlavour.isNullOrBlank()) {
            snacks = snacks.filter { snack ->
                snack.sides.any { sauce ->
                    sauce.flavour.contains(sauceFlavour, ignoreCase = true)
                }
            }
        }

        return PaginationUtils.createPaginatedResponse(
            data = snacks.map { toDTO(it) },
            totalElements = totalElements,
            pageSize = validatedSize,
            currentPage = validatedPage
        )
    }

    fun listAll(): List<SnackDTO> {
        return snackRepository.listAll().map { toDTO(it) }
    }

    fun listFiltered(vegan: Boolean? = null, flavour: String? = null, sauceFlavour: String? = null): List<SnackDTO> {
        var snacks = snackRepository.filterSnacks(vegan, flavour)
        if (!sauceFlavour.isNullOrBlank()) {
            snacks = snacks.filter { snack ->
                snack.sides.any { sauce ->
                    sauce.flavour.contains(sauceFlavour, ignoreCase = true)
                }
            }
        }
        return snacks.map { toDTO(it) }
    }

    fun findById(id: UUID): SnackDTO? = snackRepository.findById(id)?.let { toDTO(it) }

    fun add(dto: SnackDTO): SnackDTO {
        val snack = Snack().apply {
            name = dto.name
            description = dto.description
            flavor = dto.flavor
            vegan = dto.vegan
            sides = dto.sides.mapNotNull { sauceRepository.findById(it) }.toMutableList()
        }
        val saved = snackRepository.save(snack)
        return toDTO(saved)
    }

    fun update(id: UUID, dto: SnackDTO): SnackDTO? {
        val entity = snackRepository.findById(id) ?: return null
        entity.apply {
            name = dto.name
            description = dto.description
            flavor = dto.flavor
            vegan = dto.vegan
            sides = dto.sides.mapNotNull { sauceRepository.findById(it) }.toMutableList()
        }
        val result = snackRepository.update(id, entity)
        return result?.let { toDTO(it) }
    }

    fun delete(id: UUID): Boolean {
        val exists = snackRepository.findById(id) != null
        if (exists) snackRepository.deleteById(id)
        return exists
    }

    fun deleteAll() {
        snackRepository.deleteAll()
    }

    fun getSauces(id: UUID): List<Sauce> {
        val snack = snackRepository.findById(id) ?: throw NotFoundException(SNACK_NOT_FOUND)
        return snack.sides
    }

    fun addSauce(snackId: UUID, sauceId: UUID): SnackDTO {
        val snack = snackRepository.findById(snackId) ?: throw NotFoundException(SNACK_NOT_FOUND)
        val sauce = sauceRepository.findById(sauceId) ?: throw NotFoundException(SAUCE_NOT_FOUND)

        if (!snack.sides.contains(sauce)) {
            snack.sides.add(sauce)
        }
        snackRepository.update(snackId, snack)
        return toDTO(snack)
    }

    fun removeSauce(snackId: UUID, sauceId: UUID): SnackDTO {
        val snack = snackRepository.findById(snackId) ?: throw NotFoundException(SNACK_NOT_FOUND)
        val sauce = sauceRepository.findById(sauceId) ?: throw NotFoundException(SAUCE_NOT_FOUND)

        if (snack.sides.contains(sauce)) {
            snack.sides.remove(sauce)
            snackRepository.update(snackId, snack)
        }
        return toDTO(snack)
    }

    private fun toDTO(snack: Snack) = SnackDTO(
        id = snack.id,
        name = snack.name,
        description = snack.description,
        flavor = snack.flavor,
        vegan = snack.vegan,
        sides = snack.sides.mapNotNull { it.id }
    )
}
