package sysout.openups.auth.entity

import jakarta.persistence.*
import java.util.UUID

@Entity
@Table(name = "users")
open class User(
    @Id
    @GeneratedValue
    var id: UUID? = null,

    @Column(unique = true, nullable = false)
    var username: String = "",

    @Column(nullable = false)
    var password: String = "", // BCrypt hash

    @Column(nullable = false)
    var email: String = "",

    @Column(nullable = false)
    var active: Boolean = true,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = [JoinColumn(name = "user_id")])
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    var roles: MutableSet<Role> = mutableSetOf(Role.USER)
)
