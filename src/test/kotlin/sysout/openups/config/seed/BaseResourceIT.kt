package sysout.openups.config.seed

import io.quarkus.test.junit.QuarkusTest
import io.quarkus.test.TestTransaction
import jakarta.inject.Inject
import sysout.openups.config.seed.seeder.UserSeeder

@QuarkusTest
@TestTransaction
abstract class BaseResourceIT {
    @Inject
    lateinit var userSeeder: UserSeeder
    // Note: Tests should handle their own setup in @BeforeEach if needed
}