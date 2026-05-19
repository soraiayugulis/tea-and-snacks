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
        // Admin user
        authService.register("admin", "admin123", "admin@maddoxbar.com")
        val adminUser = userRepository.findByUsername("admin")
        adminUser?.roles?.add(Role.ADMIN)
        adminUser?.let { userRepository.persist(it) }

        // Manager user
        authService.register("manager", "manager123", "manager@maddoxbar.com")
        val managerUser = userRepository.findByUsername("manager")
        managerUser?.roles?.add(Role.MANAGER)
        managerUser?.let { userRepository.persist(it) }

        // Regular user
        authService.register("user", "user123", "user@maddoxbar.com")
    }

    @Transactional
    fun reset() {
        userRepository.deleteAll()
    }
}
