package sysout.openups.auth.dto

import jakarta.validation.constraints.NotNull

/**
 * Request DTO for updating user active status.
 */
data class UpdateUserStatusRequest(
    @field:NotNull(message = "Active status must be specified")
    val active: Boolean
)
