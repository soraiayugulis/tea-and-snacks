package sysout.openups.auth

import jakarta.annotation.security.PermitAll
import jakarta.annotation.security.RolesAllowed
import jakarta.inject.Inject
import jakarta.ws.rs.*
import jakarta.ws.rs.core.Context
import jakarta.ws.rs.core.MediaType
import jakarta.ws.rs.core.Response
import jakarta.ws.rs.core.SecurityContext
import org.eclipse.microprofile.openapi.annotations.Operation
import org.eclipse.microprofile.openapi.annotations.media.Content
import org.eclipse.microprofile.openapi.annotations.media.Schema
import org.eclipse.microprofile.openapi.annotations.parameters.RequestBody
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse
import org.eclipse.microprofile.openapi.annotations.responses.APIResponses
import org.eclipse.microprofile.openapi.annotations.tags.Tag
import sysout.openups.auth.dto.LoginRequest
import sysout.openups.auth.dto.LoginResponse
import sysout.openups.auth.dto.RegisterRequest
import sysout.openups.auth.dto.UpdateUserRolesRequest
import sysout.openups.auth.dto.UserInfo
import sysout.openups.auth.entity.Role
import sysout.openups.auth.service.AuthService

@Path("/auth")
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
@Tag(name = "Authentication", description = "Authentication operations")
class AuthResource @Inject constructor(
    private val authService: AuthService
) {

    @POST
    @Path("/register")
    @PermitAll
    @Operation(
        summary = "Register new user",
        description = "Create a new user account with USER role"
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = "201",
                description = "User registered successfully",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = Schema(implementation = UserInfo::class)
                )]
            ),
            APIResponse(
                responseCode = "400",
                description = "Username already exists or invalid data"
            )
        ]
    )
    fun register(
        @RequestBody(
            description = "User registration data",
            required = true,
            content = [Content(schema = Schema(implementation = RegisterRequest::class))]
        )
        registerRequest: RegisterRequest
    ): Response {
        return try {
            val user = authService.register(
                registerRequest.username,
                registerRequest.password,
                registerRequest.email
            )
            val userInfo = UserInfo(
                username = user.username,
                email = user.email,
                roles = user.roles.map { it.name }.toSet()
            )
            Response.status(Response.Status.CREATED).entity(userInfo).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.BAD_REQUEST)
                .entity(mapOf("message" to e.message))
                .build()
        }
    }

    @POST
    @Path("/login")
    @PermitAll
    @Operation(
        summary = "Login",
        description = "Authenticate user and return JWT token valid for 30 minutes"
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = "200",
                description = "Login successful",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = Schema(implementation = LoginResponse::class)
                )]
            ),
            APIResponse(
                responseCode = "401",
                description = "Invalid credentials or inactive account"
            )
        ]
    )
    fun login(
        @RequestBody(
            description = "Login credentials",
            required = true,
            content = [Content(schema = Schema(implementation = LoginRequest::class))]
        )
        loginRequest: LoginRequest
    ): Response {
        return try {
            val response: LoginResponse = authService.login(loginRequest)
            Response.ok(response).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.UNAUTHORIZED)
                .entity(mapOf("message" to e.message))
                .build()
        }
    }

    @POST
    @Path("/logout")
    @RolesAllowed("ADMIN")
    @Operation(
        summary = "Logout",
        description = "Logout user and deactivate account (Admin only)"
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = "200",
                description = "Logout successful, user account deactivated"
            ),
            APIResponse(
                responseCode = "401",
                description = "User not authenticated"
            ),
            APIResponse(
                responseCode = "403",
                description = "User does not have ADMIN role"
            ),
            APIResponse(
                responseCode = "404",
                description = "User not found"
            )
        ]
    )
    fun logout(@Context securityContext: SecurityContext): Response {
        val username = securityContext.userPrincipal?.name
            ?: return Response.status(Response.Status.UNAUTHORIZED)
                .entity(mapOf("message" to "Not authenticated"))
                .build()

        return try {
            authService.logout(username)
            Response.ok(mapOf(
                "message" to "Logout successful. User account has been deactivated."
            )).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("message" to e.message))
                .build()
        }
    }

    @GET
    @Path("/me")
    @RolesAllowed("ADMIN")
    @Operation(
        summary = "Get current user info",
        description = "Returns information about the authenticated user (Admin only)"
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = "200",
                description = "User information retrieved successfully",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = Schema(implementation = UserInfo::class)
                )]
            ),
            APIResponse(
                responseCode = "401",
                description = "User not authenticated"
            ),
            APIResponse(
                responseCode = "403",
                description = "User does not have ADMIN role"
            ),
            APIResponse(
                responseCode = "404",
                description = "User not found"
            )
        ]
    )
    fun getCurrentUser(@Context securityContext: SecurityContext): Response {
        val username = securityContext.userPrincipal?.name
            ?: return Response.status(Response.Status.UNAUTHORIZED)
                .entity(mapOf("message" to "Not authenticated"))
                .build()

        val userInfo = authService.getUserInfo(username)
            ?: return Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("message" to "User not found"))
                .build()

        return Response.ok(userInfo).build()
    }

    @GET
    @Path("/users")
    @RolesAllowed("ADMIN", "MANAGER")
    @Operation(
        summary = "List all users",
        description = "Returns a list of all users with optional filter for active users only (Admin and Manager only)"
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = "200",
                description = "Users retrieved successfully",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = Schema(implementation = UserInfo::class, type = org.eclipse.microprofile.openapi.annotations.enums.SchemaType.ARRAY)
                )]
            ),
            APIResponse(
                responseCode = "401",
                description = "User not authenticated"
            ),
            APIResponse(
                responseCode = "403",
                description = "User does not have required role"
            )
        ]
    )
    fun listUsers(
        @QueryParam("active") @DefaultValue("true") active: Boolean?
    ): Response {
        val users = authService.listUsers(active)
        return Response.ok(users).build()
    }

    @PUT
    @Path("/users/{username}/roles")
    @RolesAllowed("ADMIN", "MANAGER")
    @Operation(
        summary = "Update user roles",
        description = "Update roles for a specific user. Only ADMIN can assign ADMIN role."
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = "200",
                description = "User roles updated successfully",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = Schema(implementation = UserInfo::class)
                )]
            ),
            APIResponse(
                responseCode = "400",
                description = "Invalid role assignment"
            ),
            APIResponse(
                responseCode = "401",
                description = "User not authenticated"
            ),
            APIResponse(
                responseCode = "403",
                description = "Insufficient privileges to assign roles"
            ),
            APIResponse(
                responseCode = "404",
                description = "User not found"
            )
        ]
    )
    fun updateUserRoles(
        @PathParam("username") username: String,
        @RequestBody(
            description = "New roles for the user",
            required = true,
            content = [Content(schema = Schema(implementation = UpdateUserRolesRequest::class))]
        )
        request: UpdateUserRolesRequest,
        @Context securityContext: SecurityContext
    ): Response {
        val requesterRoles = securityContext.userPrincipal?.name?.let {
            authService.getUserByUsername(it)?.roles
        } ?: return Response.status(Response.Status.UNAUTHORIZED)
            .entity(mapOf("message" to "Not authenticated"))
            .build()

        val targetUser = authService.getUserByUsername(username)
            ?: return Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("message" to "User not found"))
                .build()

        if (!authService.canManageUser(requesterRoles, targetUser.roles)) {
            return Response.status(Response.Status.FORBIDDEN)
                .entity(mapOf("message" to "Cannot manage user with equal or higher privileges"))
                .build()
        }

        val newRoles = request.roles.mapNotNull { roleName ->
            try {
                Role.valueOf(roleName.uppercase())
            } catch (e: IllegalArgumentException) {
                null
            }
        }.toSet()

        if (!authService.validateRoleAssignment(requesterRoles, newRoles)) {
            return Response.status(Response.Status.FORBIDDEN)
                .entity(mapOf("message" to "Cannot assign ADMIN role"))
                .build()
        }

        return try {
            val updatedUser = authService.updateUserRoles(username, newRoles)
            val userInfo = UserInfo(
                username = updatedUser.username,
                email = updatedUser.email,
                roles = updatedUser.roles.map { it.name }.toSet()
            )
            Response.ok(userInfo).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("message" to e.message))
                .build()
        }
    }

    @POST
    @Path("/users/{username}/activate")
    @RolesAllowed("ADMIN", "MANAGER")
    @Operation(
        summary = "Activate user account",
        description = "Reactivate a deactivated user account"
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = "200",
                description = "User activated successfully",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = Schema(implementation = UserInfo::class)
                )]
            ),
            APIResponse(
                responseCode = "401",
                description = "User not authenticated"
            ),
            APIResponse(
                responseCode = "403",
                description = "Insufficient privileges"
            ),
            APIResponse(
                responseCode = "404",
                description = "User not found"
            )
        ]
    )
    fun activateUser(
        @PathParam("username") username: String,
        @Context securityContext: SecurityContext
    ): Response {
        val requesterRoles = securityContext.userPrincipal?.name?.let {
            authService.getUserByUsername(it)?.roles
        } ?: return Response.status(Response.Status.UNAUTHORIZED)
            .entity(mapOf("message" to "Not authenticated"))
            .build()

        val targetUser = authService.getUserByUsername(username)
            ?: return Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("message" to "User not found"))
                .build()

        if (!authService.canManageUser(requesterRoles, targetUser.roles)) {
            return Response.status(Response.Status.FORBIDDEN)
                .entity(mapOf("message" to "Cannot manage user with equal or higher privileges"))
                .build()
        }

        return try {
            val updatedUser = authService.activateUser(username)
            val userInfo = UserInfo(
                username = updatedUser.username,
                email = updatedUser.email,
                roles = updatedUser.roles.map { it.name }.toSet()
            )
            Response.ok(userInfo).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("message" to e.message))
                .build()
        }
    }

    @POST
    @Path("/users/{username}/deactivate")
    @RolesAllowed("ADMIN", "MANAGER")
    @Operation(
        summary = "Deactivate user account",
        description = "Soft delete - deactivate a user account"
    )
    @APIResponses(
        value = [
            APIResponse(
                responseCode = "200",
                description = "User deactivated successfully",
                content = [Content(
                    mediaType = MediaType.APPLICATION_JSON,
                    schema = Schema(implementation = UserInfo::class)
                )]
            ),
            APIResponse(
                responseCode = "401",
                description = "User not authenticated"
            ),
            APIResponse(
                responseCode = "403",
                description = "Insufficient privileges"
            ),
            APIResponse(
                responseCode = "404",
                description = "User not found"
            )
        ]
    )
    fun deactivateUser(
        @PathParam("username") username: String,
        @Context securityContext: SecurityContext
    ): Response {
        val requesterRoles = securityContext.userPrincipal?.name?.let {
            authService.getUserByUsername(it)?.roles
        } ?: return Response.status(Response.Status.UNAUTHORIZED)
            .entity(mapOf("message" to "Not authenticated"))
            .build()

        val targetUser = authService.getUserByUsername(username)
            ?: return Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("message" to "User not found"))
                .build()

        if (!authService.canManageUser(requesterRoles, targetUser.roles)) {
            return Response.status(Response.Status.FORBIDDEN)
                .entity(mapOf("message" to "Cannot manage user with equal or higher privileges"))
                .build()
        }

        return try {
            val updatedUser = authService.deactivateUser(username)
            val userInfo = UserInfo(
                username = updatedUser.username,
                email = updatedUser.email,
                roles = updatedUser.roles.map { it.name }.toSet()
            )
            Response.ok(userInfo).build()
        } catch (e: IllegalArgumentException) {
            Response.status(Response.Status.NOT_FOUND)
                .entity(mapOf("message" to e.message))
                .build()
        }
    }
}
