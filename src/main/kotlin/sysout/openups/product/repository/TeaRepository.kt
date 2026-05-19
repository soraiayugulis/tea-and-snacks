package sysout.openups.product.repository

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import sysout.openups.product.entity.CaffeineLevel
import sysout.openups.product.entity.Tea
import sysout.openups.product.entity.TeaCategory
import java.util.*

@ApplicationScoped
class TeaRepository : PanacheRepository<Tea> {

    fun save(tea: Tea): Tea {
        if (tea.id == null) {
            persist(tea)
        } else {
            getEntityManager().merge(tea)
        }
        return tea
    }

    fun findByIdOrNull(id: UUID): Tea? {
        return find("id", id).firstResult()
    }

    fun deleteById(id: UUID) {
        delete("id", id)
    }

    fun update(id: UUID, tea: Tea): Tea? {
        val existing = findByIdOrNull(id) ?: return null
        existing.name = tea.name
        existing.origin = tea.origin
        existing.description = tea.description
        existing.ingredients = tea.ingredients
        existing.category = tea.category
        existing.caffeineLevel = tea.caffeineLevel
        persist(existing)
        return existing
    }

    fun filterTeas(category: TeaCategory?, caffeineLevel: CaffeineLevel?, origin: String?): List<Tea> {
        val query = StringBuilder("FROM Tea WHERE 1=1")
        val params = mutableMapOf<String, Any>()

        category?.let {
            query.append(" AND category = :category")
            params["category"] = it
        }
        caffeineLevel?.let {
            query.append(" AND caffeineLevel = :caffeineLevel")
            params["caffeineLevel"] = it
        }
        origin?.let {
            query.append(" AND LOWER(origin) = LOWER(:origin)")
            params["origin"] = it
        }

        return find(query.toString(), params).list()
    }

    fun filterTeasPaginated(
        category: TeaCategory?,
        caffeineLevel: CaffeineLevel?,
        origin: String?,
        page: Int,
        size: Int
    ): List<Tea> {
        val query = StringBuilder("FROM Tea WHERE 1=1")
        val params = mutableMapOf<String, Any>()

        category?.let {
            query.append(" AND category = :category")
            params["category"] = it
        }
        caffeineLevel?.let {
            query.append(" AND caffeineLevel = :caffeineLevel")
            params["caffeineLevel"] = it
        }
        origin?.let {
            query.append(" AND LOWER(origin) = LOWER(:origin)")
            params["origin"] = it
        }

        return find(query.toString(), params)
            .page(page, size)
            .list()
    }

    fun countFilteredTeas(
        category: TeaCategory?,
        caffeineLevel: CaffeineLevel?,
        origin: String?
    ): Long {
        val query = StringBuilder("FROM Tea WHERE 1=1")
        val params = mutableMapOf<String, Any>()

        category?.let {
            query.append(" AND category = :category")
            params["category"] = it
        }
        caffeineLevel?.let {
            query.append(" AND caffeineLevel = :caffeineLevel")
            params["caffeineLevel"] = it
        }
        origin?.let {
            query.append(" AND LOWER(origin) = LOWER(:origin)")
            params["origin"] = it
        }

        return count(query.toString().replace("FROM Tea WHERE", ""), params)
    }
}