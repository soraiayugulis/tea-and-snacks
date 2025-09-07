package sysout.openups.config.seed.seeder

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import sysout.openups.product.entity.CaffeineLevel
import sysout.openups.product.entity.Tea
import sysout.openups.product.entity.TeaCategory
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
                category = TeaCategory.BLACK,
                caffeineLevel = CaffeineLevel.HIGH
            ),
            Tea(
                name = "Jasmin",
                origin = "Franca",
                description = "Infusão de flores de jasmim",
                category = TeaCategory.FLORAL,
                caffeineLevel = CaffeineLevel.NONE
            ),
            Tea(
                name = "Hortelã",
                origin = "Chile",
                description = "Infusão de folhas de hortelã frescas",
                category = TeaCategory.HERBAL,
                caffeineLevel = CaffeineLevel.NONE
            ),
            Tea(
                name = "Black Tie",
                origin = "Japão",
                description = "Chá preto com melado de mexerica e pimentas",
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
