package sysout.openups.controller.repository

import jakarta.enterprise.context.ApplicationScoped
import sysout.openups.controller.entity.Snack
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@ApplicationScoped
class SnackRepository {
    private val snacks = ConcurrentHashMap<UUID, Snack>()

    fun save(snack: Snack): Snack {
        val id = snack.id ?: UUID.randomUUID()
        val newSnack = Snack()
        newSnack.id = id
        newSnack.name = snack.name
        newSnack.description = snack.description
        newSnack.flavor = snack.flavor
        newSnack.vegan = snack.vegan
        newSnack.sides = snack.sides
        snacks[id] = newSnack
        return newSnack
    }

    fun findById(id: UUID): Snack? {
        return snacks[id]
    }

    fun deleteById(id: UUID) {
        snacks.remove(id)
    }

    fun update(id: UUID, snack: Snack): Snack? {
        return snacks.computeIfPresent(id) { _, _ ->
            val updatedSnack = Snack()
            updatedSnack.id = id
            updatedSnack.name = snack.name
            updatedSnack.description = snack.description
            updatedSnack.flavor = snack.flavor
            updatedSnack.vegan = snack.vegan
            updatedSnack.sides = snack.sides
            updatedSnack
        }
    }

    fun listAll(): List<Snack> = snacks.values.toList()

    fun filterSnacks(vegan: Boolean?, flavour: String?): List<Snack> {
        return snacks.values.filter { snack ->
            (vegan == null || snack.vegan == vegan) &&
            (flavour == null || snack.flavor.contains(flavour, ignoreCase = true))
        }
    }

    fun deleteAll() {
        snacks.clear()
    }
}
