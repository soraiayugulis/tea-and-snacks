package sysout.openups.auth.dto

data class RegisterRequest(
    val username: String,
    val password: String,
    val email: String
)

