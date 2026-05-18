package sysout.openups.product.repository

import jakarta.enterprise.context.ApplicationScoped
import sysout.openups.product.entity.CaffeineLevel
import sysout.openups.product.entity.Tea
import sysout.openups.product.entity.TeaCategory
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
            ingredients = tea.ingredients,
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
                ingredients = tea.ingredients,
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

    fun filterTeasPaginated(
        category: TeaCategory?,
        caffeineLevel: CaffeineLevel?,
        origin: String?,
        page: Int,
        size: Int
    ): List<Tea> {
        return filterTeas(category, caffeineLevel, origin)
            .drop(page * size)
            .take(size)
    }

    fun countFilteredTeas(
        category: TeaCategory?,
        caffeineLevel: CaffeineLevel?,
        origin: String?
    ): Long {
        return filterTeas(category, caffeineLevel, origin).size.toLong()
    }
}