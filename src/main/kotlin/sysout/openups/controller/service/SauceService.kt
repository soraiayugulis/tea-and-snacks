package sysout.openups.controller.service

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import sysout.openups.controller.dto.SauceDTO
import sysout.openups.controller.entity.Sauce
import sysout.openups.controller.repository.SauceRepository
import java.util.*

@ApplicationScoped
class SauceService @Inject constructor(
    private val sauceRepository: SauceRepository
) {
    fun listAll(): List<SauceDTO> = sauceRepository.listAll().map { toDTO(it) }

    fun findById(id: UUID): SauceDTO? = sauceRepository.findById(id)?.let { toDTO(it) }

    fun add(dto: SauceDTO): SauceDTO {
        val sauce = Sauce(
            name = dto.name,
            flavour = dto.flavour
        )
        val saved = sauceRepository.save(sauce)
        return toDTO(saved)
    }

    fun update(id: UUID, dto: SauceDTO): SauceDTO? {
        val entity = sauceRepository.findById(id) ?: return null
        entity.name = dto.name
        entity.flavour = dto.flavour
        val result = sauceRepository.update(id, entity)
        return result?.let { toDTO(it) }
    }

    fun delete(id: UUID): Boolean {
        val exists = sauceRepository.findById(id) != null
        if (exists) sauceRepository.deleteById(id)
        return exists
    }

    fun filterSauces(flavour: String?): List<SauceDTO> =
        sauceRepository.filterSauces(flavour).map { toDTO(it) }

    private fun toDTO(sauce: Sauce): SauceDTO = SauceDTO(
        id = sauce.id,
        name = sauce.name,
        flavour = sauce.flavour
    )
}
