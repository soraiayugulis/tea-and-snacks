package sysout.openups.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class LoginRequest(
    @JsonProperty("username")
    val username: String,
    @JsonProperty("password")
    val password: String
)

