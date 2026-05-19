package sysout.openups.config.seed.seeder

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional

@ApplicationScoped
class DatabaseSeeder @Inject constructor(
    private val teaSeeder: TeaSeeder,
    private val snackSeeder: SnackSeeder,
    private val sauceSeeder: SauceSeeder,
    private val userSeeder: UserSeeder
) {
    @Transactional
    fun seed() {
        userSeeder.seed()
        teaSeeder.seed()
        sauceSeeder.seed()
        snackSeeder.seed()
    }

    @Transactional
    fun reset() {
        snackSeeder.reset()
        sauceSeeder.reset()
        teaSeeder.reset()
        userSeeder.reset()
    }
}
