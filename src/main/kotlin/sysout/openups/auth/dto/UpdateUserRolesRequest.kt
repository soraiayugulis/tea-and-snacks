package sysout.openups.auth.dto

import com.fasterxml.jackson.annotation.JsonProperty
import jakarta.validation.constraints.NotEmpty

/**
 * Request DTO for updating user roles.
 */
data class UpdateUserRolesRequest(
    @field:NotEmpty(message = "Roles cannot be empty")
    @JsonProperty("roles")
    val roles: Set<String>
)
