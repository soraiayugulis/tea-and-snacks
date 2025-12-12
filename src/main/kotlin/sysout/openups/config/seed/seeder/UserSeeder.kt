package sysout.openups.config.seed.seeder

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import sysout.openups.auth.entity.Role
import sysout.openups.auth.repository.UserRepository
import sysout.openups.auth.service.AuthService

@ApplicationScoped
class UserSeeder @Inject constructor(
    private val authService: AuthService,
    private val userRepository: UserRepository
) {
    @Transactional
    fun seed() {
        authService.register("admin", "admin123", "admin@maddoxbar.com")

        authService.register("user", "user123", "user@maddoxbar.com")

        val adminUser = userRepository.findByUsername("admin")
        adminUser?.roles?.add(Role.ADMIN)
        if (adminUser != null) {
            userRepository.persist(adminUser)
        }
    }

    @Transactional
    fun reset() {
        userRepository.deleteAll()
    }
}
