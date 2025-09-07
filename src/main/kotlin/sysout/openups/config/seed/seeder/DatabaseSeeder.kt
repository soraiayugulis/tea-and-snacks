package sysout.openups.config.seed.seeder

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional

@ApplicationScoped
class DatabaseSeeder @Inject constructor(
    private val teaSeeder: TeaSeeder,
    private val snackSeeder: SnackSeeder,
    private val sauceSeeder: SauceSeeder
) {
    private var isSeeded = false

    @Transactional
    fun seed() {
        if (isSeeded) return

        teaSeeder.seed()
        sauceSeeder.seed()
        snackSeeder.seed()

        isSeeded = true
    }

    @Transactional
    fun reset() {
        if (!isSeeded) return

        snackSeeder.reset()
        sauceSeeder.reset()
        teaSeeder.reset()

        isSeeded = false
    }

    fun isSeeded() = isSeeded
}
