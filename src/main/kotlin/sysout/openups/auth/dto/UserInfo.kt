package sysout.openups.auth.dto

data class UserInfo(
    val username: String,
    val email: String,
    val roles: Set<String>
)

