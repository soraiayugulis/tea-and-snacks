package sysout.openups.auth.service

import io.quarkus.test.InjectMock
import io.quarkus.test.junit.QuarkusTest
import jakarta.inject.Inject
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.doNothing
import org.mockito.kotlin.whenever
import sysout.openups.auth.entity.Role
import sysout.openups.auth.entity.User
import sysout.openups.auth.repository.UserRepository
import java.util.*

@QuarkusTest
class AuthServiceRoleTest {

    @InjectMock
    lateinit var userRepository: UserRepository

    @InjectMock
    lateinit var jwtTokenProvider: JwtTokenProvider

    @Inject
    lateinit var authService: AuthService

    @Test
    fun `should allow admin to update any user roles`() {
        val user = User(
            id = UUID.randomUUID(),
            username = "targetuser",
            password = "hashed",
            email = "target@test.com",
            active = true,
            roles = mutableSetOf(Role.USER)
        )
        whenever(userRepository.findByUsername("targetuser")).thenReturn(user)
        doNothing().whenever(userRepository).persist(any<User>())

        val result = authService.updateUserRoles("targetuser", setOf(Role.USER, Role.MANAGER))

        assertTrue(result.roles.contains(Role.MANAGER))
        assertTrue(result.roles.contains(Role.USER))
    }

    @Test
    fun `should allow manager to assign user and manager roles only`() {
        val requesterRoles = setOf(Role.ADMIN, Role.MANAGER)
        val targetRoles = setOf(Role.USER, Role.MANAGER)

        val result = authService.validateRoleAssignment(requesterRoles, targetRoles)

        assertTrue(result)
    }

    @Test
    fun `should prevent manager from assigning admin role`() {
        val requesterRoles = setOf(Role.MANAGER)
        val targetRoles = setOf(Role.USER, Role.ADMIN)

        val result = authService.validateRoleAssignment(requesterRoles, targetRoles)

        assertFalse(result)
    }

    @Test
    fun `should prevent assigning roles to higher privilege user`() {
        val requesterRoles = setOf(Role.MANAGER)
        val targetUserRoles = setOf(Role.ADMIN)

        val result = authService.canManageUser(requesterRoles, targetUserRoles)

        assertFalse(result)
    }

    @Test
    fun `should throw exception when user not found for role update`() {
        whenever(userRepository.findByUsername("nonexistent")).thenReturn(null)

        assertThrows<IllegalArgumentException> {
            authService.updateUserRoles("nonexistent", setOf(Role.USER))
        }
    }

    @Test
    fun `should activate deactivated user`() {
        val user = User(
            id = UUID.randomUUID(),
            username = "inactiveuser",
            password = "hashed",
            email = "inactive@test.com",
            active = false,
            roles = mutableSetOf(Role.USER)
        )
        whenever(userRepository.findByUsername("inactiveuser")).thenReturn(user)
        doNothing().whenever(userRepository).persist(any<User>())

        val result = authService.activateUser("inactiveuser")

        assertTrue(result.active)
    }

    @Test
    fun `should deactivate active user`() {
        val user = User(
            id = UUID.randomUUID(),
            username = "activeuser",
            password = "hashed",
            email = "active@test.com",
            active = true,
            roles = mutableSetOf(Role.USER)
        )
        whenever(userRepository.findByUsername("activeuser")).thenReturn(user)
        doNothing().whenever(userRepository).persist(any<User>())

        val result = authService.deactivateUser("activeuser")

        assertFalse(result.active)
    }

    @Test
    fun `should throw exception when activating non-existent user`() {
        whenever(userRepository.findByUsername("nonexistent")).thenReturn(null)

        assertThrows<IllegalArgumentException> {
            authService.activateUser("nonexistent")
        }
    }

    @Test
    fun `should allow admin to manage any user`() {
        val requesterRoles = setOf(Role.ADMIN)
        val targetUserRoles = setOf(Role.ADMIN, Role.MANAGER, Role.USER)

        val result = authService.canManageUser(requesterRoles, targetUserRoles)

        assertTrue(result)
    }

    @Test
    fun `should allow manager to manage user only`() {
        val requesterRoles = setOf(Role.MANAGER)
        val targetUserRoles = setOf(Role.USER)

        val result = authService.canManageUser(requesterRoles, targetUserRoles)

        assertTrue(result)
    }

    @Test
    fun `should prevent manager from managing another manager`() {
        val requesterRoles = setOf(Role.MANAGER)
        val targetUserRoles = setOf(Role.MANAGER)

        val result = authService.canManageUser(requesterRoles, targetUserRoles)

        assertFalse(result)
    }
}
