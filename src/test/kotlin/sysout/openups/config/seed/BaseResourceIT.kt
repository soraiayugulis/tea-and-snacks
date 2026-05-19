package sysout.openups.config.seed

import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import org.junit.jupiter.api.BeforeEach
import sysout.openups.config.seed.seeder.UserSeeder
import sysout.openups.config.seed.seeder.TeaSeeder
import sysout.openups.config.seed.seeder.SnackSeeder
import sysout.openups.config.seed.seeder.SauceSeeder

@QuarkusTest
abstract class BaseResourceIT {
    @Inject
    lateinit var userSeeder: UserSeeder
    @Inject
    lateinit var teaSeeder: TeaSeeder
    @Inject
    lateinit var snackSeeder: SnackSeeder
    @Inject
    lateinit var sauceSeeder: SauceSeeder

    @BeforeEach
    @Transactional
    open fun setUp() {
        // Reset and seed all data
        snackSeeder.reset()
        sauceSeeder.reset()
        teaSeeder.reset()
        userSeeder.reset()

        userSeeder.seed()
        teaSeeder.seed()
        sauceSeeder.seed()
        snackSeeder.seed()
    }
}