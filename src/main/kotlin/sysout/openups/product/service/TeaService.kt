package sysout.openups.product.service

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import sysout.openups.common.pagination.PaginatedResponse
import sysout.openups.common.pagination.PaginationUtils
import sysout.openups.product.dto.IngredientDTO
import sysout.openups.product.dto.TeaDTO
import sysout.openups.product.entity.CaffeineLevel
import sysout.openups.product.entity.Ingredient
import sysout.openups.product.entity.Tea
import sysout.openups.product.entity.TeaCategory
import sysout.openups.product.entity.UnitOfMeasure
import sysout.openups.product.repository.TeaRepository
import sysout.openups.util.EnumConverter
import java.util.*

@ApplicationScoped
class TeaService @Inject constructor(
    private val teaRepository: TeaRepository
) {
    fun listAll(): List<TeaDTO> = teaRepository.listAll().map { toDTO(it) }

    fun findById(id: UUID): TeaDTO? = teaRepository.findByIdOrNull(id)?.let { toDTO(it) }

    fun add(dto: TeaDTO): TeaDTO {
        val tea = Tea(
            name = dto.name,
            origin = dto.origin,
            description = dto.description,
            category = dto.category,
            caffeineLevel = dto.caffeineLevel,
            ingredients = dto.ingredients.map { ingredientDTOToEntity(it) }
        )
        val saved = teaRepository.save(tea)
        return toDTO(saved)
    }

    fun update(id: UUID, dto: TeaDTO): TeaDTO? {
        val entity = teaRepository.findByIdOrNull(id) ?: return null
        entity.name = dto.name
        entity.origin = dto.origin
        entity.description = dto.description
        entity.category = dto.category
        entity.caffeineLevel = dto.caffeineLevel
        entity.ingredients = dto.ingredients.map { ingredientDTOToEntity(it) }
        val result = teaRepository.update(id, entity)
        return result?.let { toDTO(it) }
    }

    fun delete(id: UUID): Boolean {
        val exists = teaRepository.findByIdOrNull(id) != null
        if (exists) teaRepository.deleteById(id)
        return exists
    }

    fun deleteAll(category: TeaCategory? = null, caffeineLevel: CaffeineLevel? = null, origin: String? = null): Int {
        if (category == null && caffeineLevel == null && origin == null) {
            val count = teaRepository.listAll().size
            teaRepository.deleteAll()
            return count
        }

        val teasToDelete = teaRepository.filterTeas(category, caffeineLevel, origin)
        val count = teasToDelete.size

        teasToDelete.forEach { tea ->
            tea.id?.let { teaRepository.deleteById(it) }
        }

        return count
    }

    fun filterTeas(
        categoryStr: String?,
        caffeineLevelStr: String?,
        origin: String?
    ): List<TeaDTO> {
        val category = EnumConverter.fromString<TeaCategory>(categoryStr)
        val caffeineLevel = EnumConverter.fromString<CaffeineLevel>(caffeineLevelStr)
        return teaRepository.filterTeas(category, caffeineLevel, origin).map { toDTO(it) }
    }

    fun filterTeas(
        categoryStr: String?,
        caffeineLevelStr: String?,
        origin: String?,
        page: Int = 0,
        size: Int = 10
    ): PaginatedResponse<TeaDTO> {
        val category = EnumConverter.fromString<TeaCategory>(categoryStr)
        val caffeineLevel = EnumConverter.fromString<CaffeineLevel>(caffeineLevelStr)

        val validatedPage = PaginationUtils.validateAndGetPageNumber(page)
        val validatedSize = PaginationUtils.validateAndGetPageSize(size)

        val totalElements = teaRepository.countFilteredTeas(category, caffeineLevel, origin)
        val teas = teaRepository.filterTeasPaginated(category, caffeineLevel, origin, validatedPage, validatedSize)
            .map { toDTO(it) }

        return PaginationUtils.createPaginatedResponse(
            data = teas,
            totalElements = totalElements,
            pageSize = validatedSize,
            currentPage = validatedPage
        )
    }

    fun deleteFiltered(categoryStr: String?, caffeineLevelStr: String?, origin: String?): Int {
        val category = EnumConverter.fromString<TeaCategory>(categoryStr)
        val caffeineLevel = EnumConverter.fromString<CaffeineLevel>(caffeineLevelStr)

        return deleteAll(category, caffeineLevel, origin)
    }

    private fun toDTO(tea: Tea): TeaDTO = TeaDTO(
        id = tea.id,
        name = tea.name,
        origin = tea.origin,
        description = tea.description,
        category = tea.category,
        caffeineLevel = tea.caffeineLevel,
        ingredients = tea.ingredients.map { ingredientEntityToDTO(it) }
    )

    private fun ingredientDTOToEntity(dto: IngredientDTO): Ingredient = Ingredient(
        id = dto.id,
        name = dto.name,
        quantity = dto.quantity,
        unitOfMeasure = UnitOfMeasure.valueOf(dto.unitOfMeasure)
    )

    private fun ingredientEntityToDTO(entity: Ingredient): IngredientDTO = IngredientDTO(
        id = entity.id,
        name = entity.name,
        quantity = entity.quantity,
        unitOfMeasure = entity.unitOfMeasure.name
    )
}
