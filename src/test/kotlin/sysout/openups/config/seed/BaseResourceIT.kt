package sysout.openups.config.seed

import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.junit.jupiter.api.BeforeEach
import sysout.openups.config.seed.seeder.DatabaseSeeder

@QuarkusTest
abstract class BaseResourceIT {
    @Inject
    lateinit var databaseSeeder: DatabaseSeeder

    @BeforeEach
    fun setUp() {
        databaseSeeder.reset()
        databaseSeeder.seed()
    }
}