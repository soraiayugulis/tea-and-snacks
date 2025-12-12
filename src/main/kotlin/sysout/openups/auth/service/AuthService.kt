package sysout.openups.auth.service

import jakarta.enterprise.context.ApplicationScoped
import jakarta.inject.Inject
import jakarta.transaction.Transactional
import sysout.openups.auth.dto.LoginRequest
import sysout.openups.auth.dto.LoginResponse
import sysout.openups.auth.dto.UserInfo
import sysout.openups.auth.entity.User
import sysout.openups.auth.entity.Role
import sysout.openups.auth.repository.UserRepository
import org.wildfly.security.password.interfaces.BCryptPassword
import org.wildfly.security.password.util.ModularCrypt
import java.security.SecureRandom

@ApplicationScoped
class AuthService @Inject constructor(
    private val userRepository: UserRepository,
    private val jwtTokenProvider: JwtTokenProvider
) {

    @Transactional
    fun register(username: String, password: String, email: String): User {
        if (userRepository.existsByUsername(username)) {
            throw IllegalArgumentException("Username already exists")
        }

        val hashedPassword = hashPassword(password)
        val user = User(
            username = username,
            password = hashedPassword,
            email = email,
            active = true,
            roles = mutableSetOf(Role.USER)
        )

        userRepository.persist(user)
        return user
    }

    fun login(loginRequest: LoginRequest): LoginResponse {
        val user = userRepository.findByUsername(loginRequest.username)
            ?: throw IllegalArgumentException("Invalid username or password")

        if (!user.active) {
            throw IllegalArgumentException("User account is inactive")
        }

        if (!verifyPassword(loginRequest.password, user.password)) {
            throw IllegalArgumentException("Invalid username or password")
        }

        val token = jwtTokenProvider.generateToken(user.username, user.roles.map { it.name }.toSet())

        return LoginResponse(
            token = token,
            username = user.username
        )
    }

    @Transactional
    fun logout(username: String) {
        val user = userRepository.findByUsername(username)
            ?: throw IllegalArgumentException("User not found")

        user.active = false
        userRepository.persist(user)
    }

    fun getUserInfo(username: String): UserInfo? {
        val user = userRepository.findByUsername(username) ?: return null
        return UserInfo(
            username = user.username,
            email = user.email,
            roles = user.roles.map { it.name }.toSet()
        )
    }

    fun getUserByUsername(username: String): User? {
        return userRepository.findByUsername(username)
    }

    fun listUsers(active: Boolean?): List<UserInfo> {
        val users = if (active == null) {
            userRepository.listAll()
        } else {
            userRepository.find("active", active).list()
        }

        return users.map { user ->
            UserInfo(
                username = user.username,
                email = user.email,
                roles = user.roles.map { it.name }.toSet()
            )
        }
    }

    private fun hashPassword(password: String): String {
        val salt = ByteArray(BCryptPassword.BCRYPT_SALT_SIZE)
        SecureRandom().nextBytes(salt)
        return ModularCrypt.encodeAsString(
            org.wildfly.security.password.PasswordFactory.getInstance(BCryptPassword.ALGORITHM_BCRYPT)
                .generatePassword(
                    org.wildfly.security.password.spec.EncryptablePasswordSpec(
                        password.toCharArray(),
                        org.wildfly.security.password.spec.IteratedSaltedPasswordAlgorithmSpec(10, salt)
                    )
                ) as BCryptPassword
        )
    }

    private fun verifyPassword(plainPassword: String, hashedPassword: String): Boolean {
        return try {
            val password = ModularCrypt.decode(hashedPassword) as? BCryptPassword ?: return false
            val factory = org.wildfly.security.password.PasswordFactory.getInstance(BCryptPassword.ALGORITHM_BCRYPT)
            val translatedPassword = factory.translate(password)
            factory.verify(
                translatedPassword,
                plainPassword.toCharArray()
            )
        } catch (e: Exception) {
            false
        }
    }
}
