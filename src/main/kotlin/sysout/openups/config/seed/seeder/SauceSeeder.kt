package sysout.openups.config.seed.seeder

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import sysout.openups.product.entity.Sauce
import sysout.openups.product.repository.SauceRepository

@ApplicationScoped
class SauceSeeder @Inject constructor(
    private val sauceRepository: SauceRepository
) {
    @Transactional
    fun seed(): List<Sauce> {
        val sauces = listOf(
            Sauce(
                name = "Purê de alho com hortelã",
                flavour = "Alho, hortelã"
            ),
            Sauce(
                name = "Gorgonzola com alecrim",
                flavour = "Queijo, alecrim",
            ),
            Sauce(
                name = "Pesto de manjericão com hortelã e alho",
                flavour = "Manjericão, hortelã, alho, pesto"
            ),
            Sauce(
                name = "Purê de tomatinho confit com manjericão e azeite trufado",
                flavour = "Manjericão, azeite trufado"
            ),
            Sauce(
                name = "Queijo com queijo e orégano fresco",
                flavour = "Queijo, orégano fresco"
            )
        )

        return sauces.map { sauce ->
            sauceRepository.save(sauce)
        }
    }

    @Transactional
    fun reset() {
        sauceRepository.deleteAll()
    }
}
