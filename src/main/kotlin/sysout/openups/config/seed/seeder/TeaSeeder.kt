package sysout.openups.config.seed.seeder

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import sysout.openups.product.entity.CaffeineLevel
import sysout.openups.product.entity.Ingredient
import sysout.openups.product.entity.Tea
import sysout.openups.product.entity.TeaCategory
import sysout.openups.product.entity.UnitOfMeasure
import sysout.openups.product.repository.TeaRepository

@ApplicationScoped
class TeaSeeder @Inject constructor(
    private val teaRepository: TeaRepository
) {
    @Transactional
    fun seed() {
        val teas = listOf(
            Tea(
                name = "Chai Thai",
                origin = "Thailandia",
                description = "infusão de especiarias com chá preto e leite",
                ingredients = listOf(
                    Ingredient(name = "Black Tea Leaves", quantity = 5.0, unitOfMeasure = UnitOfMeasure.GRAMS),
                    Ingredient(name = "Cinnamon", quantity = 2.0, unitOfMeasure = UnitOfMeasure.GRAMS),
                    Ingredient(name = "Cardamom", quantity = 1.5, unitOfMeasure = UnitOfMeasure.GRAMS),
                    Ingredient(name = "Ginger", quantity = 1.0, unitOfMeasure = UnitOfMeasure.GRAMS),
                    Ingredient(name = "Star Anise", quantity = 0.5, unitOfMeasure = UnitOfMeasure.GRAMS)
                ),
                category = TeaCategory.BLACK,
                caffeineLevel = CaffeineLevel.HIGH
            ),
            Tea(
                name = "Jasmin",
                origin = "Franca",
                description = "Infusão de flores de jasmim",
                ingredients = listOf(
                    Ingredient(name = "Jasmine Flowers", quantity = 3.0, unitOfMeasure = UnitOfMeasure.GRAMS),
                    Ingredient(name = "Green Tea Base", quantity = 2.0, unitOfMeasure = UnitOfMeasure.GRAMS)
                ),
                category = TeaCategory.FLORAL,
                caffeineLevel = CaffeineLevel.NONE
            ),
            Tea(
                name = "Hortelã",
                origin = "Chile",
                description = "Infusão de folhas de hortelã frescas",
                ingredients = listOf(
                    Ingredient(name = "Fresh Mint Leaves", quantity = 4.0, unitOfMeasure = UnitOfMeasure.GRAMS)
                ),
                category = TeaCategory.HERBAL,
                caffeineLevel = CaffeineLevel.NONE
            ),
            Tea(
                name = "Black Tie",
                origin = "Japão",
                description = "Chá preto com melado de mexerica e pimentas",
                ingredients = listOf(
                    Ingredient(name = "Black Tea Leaves", quantity = 6.0, unitOfMeasure = UnitOfMeasure.GRAMS),
                    Ingredient(name = "Tangerine Molasses", quantity = 10.0, unitOfMeasure = UnitOfMeasure.ML),
                    Ingredient(name = "Black Pepper", quantity = 0.5, unitOfMeasure = UnitOfMeasure.GRAMS),
                    Ingredient(name = "Chili Flakes", quantity = 0.3, unitOfMeasure = UnitOfMeasure.GRAMS)
                ),
                category = TeaCategory.BLACK,
                caffeineLevel = CaffeineLevel.HIGH
            )
        )

        teas.forEach { tea ->
            teaRepository.save(tea)
        }
    }

    @Transactional
    fun reset() {
        teaRepository.deleteAll()
    }
}
