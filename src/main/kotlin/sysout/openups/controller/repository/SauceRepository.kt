package sysout.openups.controller.repository

import jakarta.enterprise.context.ApplicationScoped
import sysout.openups.controller.entity.Sauce
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@ApplicationScoped
class SauceRepository {
    private val sauces = ConcurrentHashMap<UUID, Sauce>()

    fun save(sauce: Sauce): Sauce {
        val id = sauce.id ?: UUID.randomUUID()
        val newSauce = Sauce(
            id = id,
            name = sauce.name,
            flavour = sauce.flavour
        )
        sauces[id] = newSauce
        return newSauce
    }

    fun findById(id: UUID): Sauce? {
        return sauces[id]
    }

    fun deleteById(id: UUID) {
        sauces.remove(id)
    }

    fun update(id: UUID, sauce: Sauce): Sauce? {
        return sauces.computeIfPresent(id) { _, _ ->
            Sauce(
                id = id,
                name = sauce.name,
                flavour = sauce.flavour
            )
        }
    }

    fun listAll(): List<Sauce> = sauces.values.toList()

    fun filterSauces(flavour: String?): List<Sauce> {
        return sauces.values.filter { sauce ->
            flavour == null || sauce.flavour.equals(flavour, ignoreCase = true)
        }
    }
}
