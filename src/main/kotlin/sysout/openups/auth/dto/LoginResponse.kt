package sysout.openups.auth.dto

data class LoginResponse(
    val token: String,
    val username: String,
    val expiresIn: Long = 1800 // 30 minutes in seconds
)

