package sysout.openups.auth.repository

import io.quarkus.hibernate.orm.panache.kotlin.PanacheRepository
import jakarta.enterprise.context.ApplicationScoped
import sysout.openups.auth.entity.User

@ApplicationScoped
class UserRepository : PanacheRepository<User> {

    fun findByUsername(username: String): User? {
        return find("username", username).firstResult()
    }

    fun existsByUsername(username: String): Boolean {
        return count("username", username) > 0
    }
}
