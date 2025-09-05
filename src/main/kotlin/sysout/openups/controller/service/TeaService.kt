package sysout.openups.controller.service

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import sysout.openups.controller.dto.TeaDTO
import sysout.openups.controller.entity.Tea
import sysout.openups.controller.repository.TeaRepository
import java.util.*

@ApplicationScoped
class TeaService @Inject constructor(
    private val teaRepository: TeaRepository
) {
    fun listAll(): List<TeaDTO> = teaRepository.listAll().map { toDTO(it) }

    fun findById(id: UUID): TeaDTO? = teaRepository.findById(id)?.let { toDTO(it) }

    fun add(dto: TeaDTO): TeaDTO {
        val tea = Tea(
            name = dto.name,
            origin = dto.origin,
            description = dto.description,
            category = dto.category,
            caffeineLevel = dto.caffeineLevel
        )
        val saved = teaRepository.save(tea)
        return toDTO(saved)
    }

    fun update(id: UUID, dto: TeaDTO): TeaDTO? {
        val entity = teaRepository.findById(id) ?: return null
        entity.name = dto.name
        entity.origin = dto.origin
        entity.description = dto.description
        entity.category = dto.category
        entity.caffeineLevel = dto.caffeineLevel
        val result = teaRepository.update(id, entity)
        return result?.let { toDTO(it) }
    }

    fun delete(id: UUID): Boolean {
        val exists = teaRepository.findById(id) != null
        if (exists) teaRepository.deleteById(id)
        return exists
    }

    private fun toDTO(tea: Tea): TeaDTO = TeaDTO(
        id = tea.id,
        name = tea.name,
        origin = tea.origin,
        description = tea.description,
        category = tea.category,
        caffeineLevel = tea.caffeineLevel
    )
}
