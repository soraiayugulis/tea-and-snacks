package sysout.openups.controller.service

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.ws.rs.NotFoundException
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
    fun listAll(): List<SnackDTO> = snackRepository.listAll().map { toDTO(it) }

    fun findById(id: UUID): SnackDTO? = snackRepository.findById(id)?.let { toDTO(it) }

    fun add(dto: SnackDTO): SnackDTO {
        val snack = Snack()
        snack.name = dto.name
        snack.description = dto.description
        snack.flavor = dto.flavor
        snack.vegan = dto.vegan
        snack.sides = dto.sides.mapNotNull { sauceRepository.findById(it) }.toMutableList()
        val saved = snackRepository.save(snack)
        return toDTO(saved)
    }

    fun update(id: UUID, dto: SnackDTO): SnackDTO? {
        val entity = snackRepository.findById(id) ?: return null
        entity.name = dto.name
        entity.description = dto.description
        entity.flavor = dto.flavor
        entity.vegan = dto.vegan
        entity.sides = dto.sides.mapNotNull { sauceRepository.findById(it) }.toMutableList()
        val result = snackRepository.update(id, entity)
        return result?.let { toDTO(it) }
    }

    fun delete(id: UUID): Boolean {
        val exists = snackRepository.findById(id) != null
        if (exists) snackRepository.deleteById(id)
        return exists
    }

    fun getSauces(id: UUID): List<Sauce> {
        val snack = snackRepository.findById(id) ?: throw NotFoundException()
        return snack.sides
    }

    private fun toDTO(snack: Snack): SnackDTO = SnackDTO(
        id = snack.id,
        name = snack.name,
        description = snack.description,
        flavor = snack.flavor,
        vegan = snack.vegan,
        sides = snack.sides.mapNotNull { it.id }
    )
}
