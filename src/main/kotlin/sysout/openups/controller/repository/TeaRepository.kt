package sysout.openups.controller.repository

import jakarta.enterprise.context.ApplicationScoped
import sysout.openups.controller.entity.CaffeineLevel
import sysout.openups.controller.entity.Tea
import sysout.openups.controller.entity.TeaCategory
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@ApplicationScoped
class TeaRepository {
    private val teas = ConcurrentHashMap<UUID, Tea>()

    fun save(tea: Tea): Tea {
        val id = tea.id ?: UUID.randomUUID()
        val newTea = Tea(
            id = id,
            name = tea.name,
            origin = tea.origin,
            description = tea.description,
            category = tea.category,
            caffeineLevel = tea.caffeineLevel
        )
        teas[id] = newTea
        return newTea
    }

    fun findById(id: UUID): Tea? {
        return teas[id]
    }

    fun deleteById(id: UUID) {
        teas.remove(id)
    }

    fun deleteAll() {
        teas.clear()
    }

    fun update(id: UUID, tea: Tea): Tea? {
        return teas.computeIfPresent(id) { _, _ ->
            Tea(
                id = id,
                name = tea.name,
                origin = tea.origin,
                description = tea.description,
                category = tea.category,
                caffeineLevel = tea.caffeineLevel
            )
        }
    }

    fun listAll(): List<Tea> = teas.values.toList()

    fun filterTeas(category: TeaCategory?, caffeineLevel: CaffeineLevel?, origin: String?): List<Tea> {
        return teas.values.filter { tea ->
            (category == null || tea.category == category) &&
            (caffeineLevel == null || tea.caffeineLevel == caffeineLevel) &&
            (origin == null || tea.origin.equals(origin, ignoreCase = true))
        }
    }
}
