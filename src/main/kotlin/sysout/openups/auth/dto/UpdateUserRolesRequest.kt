package sysout.openups.auth.dto

import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull

/**
 * Request DTO for updating user roles.
 */
data class UpdateUserRolesRequest(
    @field:NotEmpty(message = "Roles cannot be empty")
    val roles: Set<String>
)
