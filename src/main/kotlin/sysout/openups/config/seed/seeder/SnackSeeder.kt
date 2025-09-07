package sysout.openups.config.seed.seeder

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import sysout.openups.product.entity.Snack
import sysout.openups.product.repository.SnackRepository

@ApplicationScoped
class SnackSeeder @Inject constructor(
    private val snackRepository: SnackRepository,
    private val sauceSeeder: SauceSeeder
) {
    @Transactional
    fun seed() {
        val sauces = sauceSeeder.seed()

        val alhoEHortela = sauces.find { it.name == "Purê de alho com hortelã" }
        val gorgoCrim = sauces.find { it.name == "Gorgonzola com alecrim" }
        val manjericAlho = sauces.find { it.name == "Pesto de manjericão com hortelã e alho" }
        val confitTrufado = sauces.find { it.name == "Purê de tomatinho confit com manjericão e azeite trufado" }
        val queijoComQueijo = sauces.find { it.name == "Queijo com queijo e orégano fresco" }

        val snacks = listOf(
            Snack().apply {
                name = "Pão de queijo provolone"
                description = "Feito totalmente com queijo provolone mineiro"
                flavor = "Queijo"
                vegan = false
                sides = mutableListOf(gorgoCrim, queijoComQueijo).filterNotNull().toMutableList()
            },
            Snack().apply {
                name = "Croissant vegano chapado na manteiga"
                description = "Croissant vegano de massa folhada com manteiga na chapa"
                flavor = "Manteiga, assado"
                vegan = true
                sides = mutableListOf(alhoEHortela, manjericAlho, confitTrufado).filterNotNull().toMutableList()
            },
            Snack().apply {
                name = "Croissant chapado na manteiga"
                description = "Croissant de massa folhada com manteiga na chapa"
                flavor = "Manteiga, assado"
                vegan = false
                sides = mutableListOf(alhoEHortela, gorgoCrim, manjericAlho, confitTrufado).filterNotNull().toMutableList()
            },
            Snack().apply {
                name = "Tirinhas de massa de pastel coberta com alecrim e pimentas"
                description = "Tirinhas crocantes de massa de pastel bem sequinhas e crocantes"
                flavor = "Pimenta, alecrim, pastel, frito"
                vegan = true
                sides = mutableListOf(alhoEHortela, gorgoCrim, manjericAlho, confitTrufado, queijoComQueijo).filterNotNull().toMutableList()
            },
            Snack().apply {
                name = "Tirinhas de massa de pastel coberta com alho e manteiga"
                description = "Tirinhas crocantes de massa de pastel bem sequinhas e crocantes"
                flavor = "Manteiga, alho, pastel, frito"
                vegan = true
                sides = mutableListOf(alhoEHortela, gorgoCrim, manjericAlho, confitTrufado, queijoComQueijo).filterNotNull().toMutableList()
            },
            Snack().apply {
                name = "Anéis de cebola recheados de gorgonzola e empanados"
                description = "Anéis de cebola recheados de gorgonzola e empanados"
                flavor = "Cebola, gorgonzola, frito"
                vegan = false
                sides = mutableListOf(alhoEHortela, gorgoCrim, manjericAlho, confitTrufado, queijoComQueijo).filterNotNull().toMutableList()
            }
        )

        snacks.forEach { snack ->
            snackRepository.save(snack)
        }
    }

    @Transactional
    fun reset() {
        snackRepository.deleteAll()
    }
}
